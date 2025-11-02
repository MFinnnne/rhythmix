package io.github.mfinnnne.rhythmix.translate;

import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNodeTypes;
import io.github.mfinnnne.rhythmix.parser.ast.EventSourceCondition;
import io.pebbletemplates.pebble.template.PebbleTemplate;

import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.github.mfinnnne.rhythmix.pebble.TemplateEngine.ENGINE;

/**
 * Translator for multi-source event expressions.
 * <p>
 * Converts AST nodes of type MULTI_SOURCE_EVENT_EXPR into Aviator code that:
 * <ul>
 *   <li>Routes events by their name field to the appropriate condition</li>
 *   <li>Maintains state for each event source</li>
 *   <li>Evaluates the logical expression combining all states</li>
 *   <li>Resets states after a successful match</li>
 * </ul>
 * <p>
 * Example input: {@code {#temp:<30# && #humidity:>80#}}
 * <p>
 * Generated code structure:
 * <pre>
 * if (event.name == "temp") {
 *     tempState = event.value < 30;
 * } else if (event.name == "humidity") {
 *     humidityState = event.value > 80;
 * }
 * 
 * if (tempState && humidityState) {
 *     tempState = false;
 *     humidityState = false;
 *     return true;
 * }
 * return false;
 * </pre>
 *
 * @author MFine
 * @version 1.0
 * @since 1.1
 */
public class MultiSourceEventExpr {

    /**
     * Data class holding information about a single event source condition.
     */
    public static class EventSourceConditionData {
        /** The event source alias (e.g., "temp", "humidity") */
        private String alias;

        /** The translated condition code (e.g., "event.value < 30") */
        private String conditionCode;

        /** The state variable name (e.g., "tempState") */
        private String stateVarName;

        public EventSourceConditionData(String alias, String conditionCode, String stateVarName) {
            this.alias = alias;
            this.conditionCode = conditionCode;
            this.stateVarName = stateVarName;
        }

        public String getAlias() {
            return alias;
        }

        public String getConditionCode() {
            return conditionCode;
        }

        public String getStateVarName() {
            return stateVarName;
        }
    }

    /**
     * Translates a multi-source event expression AST node into Aviator code.
     *
     * @param astNode the AST node of type MULTI_SOURCE_EVENT_EXPR
     * @param context the translation context
     * @param env the environment proxy
     * @return the generated Aviator code
     * @throws TranslatorException if translation fails
     */
    public static String translate(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {
        try {
            Writer writer = new StringWriter();
            PebbleTemplate template = ENGINE.getTemplate("expr/multi_source_event.peb");
            // Extract all event source conditions from the AST
            List<EventSourceConditionData> conditions = extractConditions(astNode, context, env);

            // Build the logical expression combining all states
            String logicalExpression = buildLogicalExpression(astNode.getChildren(0), conditions);

            // Populate template context
            context.put("conditions", conditions);
            context.put("logicalExpression", logicalExpression);

            // Evaluate template
            template.evaluate(writer, context);
            return writer.toString();
            
        } catch (Exception e) {
            throw new TranslatorException("Failed to translate multi-source event expression: " + e.getMessage());
        }
    }

    /**
     * Translates a multi-source event expression AST node into Aviator code.
     *
     * @param astNode the AST node of type MULTI_SOURCE_EVENT_EXPR
     * @param env the environment proxy
     * @return the generated Aviator code
     * @throws TranslatorException if translation fails
     */
    public static String translate(ASTNode astNode, EnvProxy env) throws TranslatorException {
        try {
            Map<String, Object> context = new HashMap<>();
            return translate(astNode, context, env);
        } catch (Exception e) {
            throw new TranslatorException("Failed to translate multi-source event expression: " + e.getMessage());
        }
    }

    /**
     * Extracts all event source conditions from the AST tree.
     * <p>
     * Traverses the AST tree (which may be a binary tree of logical operators)
     * and collects all EventSourceCondition leaf nodes.
     *
     * @param astNode the root AST node
     * @param context the translation context
     * @param env the environment proxy
     * @return list of event source condition data
     * @throws TranslatorException if extraction fails
     */
    private static List<EventSourceConditionData> extractConditions(ASTNode astNode, Map<String, Object> context, EnvProxy env) throws TranslatorException {
        List<EventSourceConditionData> conditions = new ArrayList<>();
        
        // The child of MultiSourceEventStmt is either:
        // 1. A single EventSourceCondition (for single source)
        // 2. A binary expression tree with EventSourceCondition leaves
        ASTNode child = astNode.getChildren(0);
        
        extractConditionsRecursive(child, conditions, context, env);
        
        return conditions;
    }

    /**
     * Recursively extracts event source conditions from the AST tree.
     *
     * @param node the current AST node
     * @param conditions the list to accumulate conditions
     * @param context the translation context
     * @param env the environment proxy
     * @throws TranslatorException if extraction fails
     */
    private static void extractConditionsRecursive(ASTNode node, List<EventSourceConditionData> conditions, 
                                                   Map<String, Object> context, EnvProxy env) throws TranslatorException {
        if (node instanceof EventSourceCondition) {
            // Leaf node: extract the condition
            EventSourceCondition esc = (EventSourceCondition) node;
            String alias = esc.getEventSourceAlias();
            String stateVarName = alias + "State";
            
            // Translate the condition expression
            // The condition is the child of EventSourceCondition
            ASTNode conditionExpr = esc.getChildren(0);

            // Create a temporary context for condition translation
            // Do NOT set eventValue in context - let templates use literal "event.value"
            Map<String, Object> conditionContext = new HashMap<>(context);

            String conditionCode = Translator.translate(conditionExpr, conditionContext, env);
            
            conditions.add(new EventSourceConditionData(alias, conditionCode, stateVarName));
            
        } else if (node.getType() == ASTNodeTypes.BINARY_EXPR) {
            // Internal node: recurse on both children
            extractConditionsRecursive(node.getChildren(0), conditions, context, env);
            extractConditionsRecursive(node.getChildren(1), conditions, context, env);
        }
    }

    /**
     * Builds the logical expression combining all state variables.
     * <p>
     * Traverses the AST tree and replaces EventSourceCondition nodes with their
     * corresponding state variable names, preserving the logical operators.
     *
     * @param node the current AST node
     * @param conditions the list of all conditions (for looking up state variable names)
     * @return the logical expression string (e.g., "tempState && humidityState")
     */
    private static String buildLogicalExpression(ASTNode node, List<EventSourceConditionData> conditions) {
        if (node instanceof EventSourceCondition) {
            // Leaf node: return the state variable name
            EventSourceCondition esc = (EventSourceCondition) node;
            String alias = esc.getEventSourceAlias();
            
            // Find the corresponding state variable name
            for (EventSourceConditionData cond : conditions) {
                if (cond.alias.equals(alias)) {
                    return cond.stateVarName;
                }
            }
            
            // Fallback (should not happen)
            return alias + "State";
            
        } else if (node.getType() == ASTNodeTypes.BINARY_EXPR) {
            // Internal node: recurse and combine with operator
            String left = buildLogicalExpression(node.getChildren(0), conditions);
            String right = buildLogicalExpression(node.getChildren(1), conditions);
            String operator = node.getLabel();
            
            return "(" + left + " " + operator + " " + right + ")";
        }
        
        // Should not reach here
        return "false";
    }
}

