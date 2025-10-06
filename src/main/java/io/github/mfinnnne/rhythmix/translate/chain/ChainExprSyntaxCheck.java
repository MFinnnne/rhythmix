package io.github.mfinnnne.rhythmix.translate.chain;

import io.github.mfinnnne.rhythmix.config.ChainFunctionConfig;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.parser.ast.ASTNode;
import io.github.mfinnnne.rhythmix.util.ParserUtils;

import java.util.*;

/**
 * Performs syntax checking for chain expressions.
 * This class ensures that the sequence of functions in a chain expression is valid
 * according to the configured rules (e.g., start functions, end functions, and allowed transitions).
 *
 * @author MFine
 * @version $Id: $Id
 */
public class ChainExprSyntaxCheck {


    /**
     * Checks the syntax of a chain expression represented by an ASTNode.
     *
     * @param astNode The root ASTNode of the chain expression.
     * @throws TranslatorException if any syntax violation is found.
     */
    public static void check(ASTNode astNode) throws TranslatorException {
        List<ASTNode> nodes = ParserUtils.getAllCallStmtNode(astNode);

        // Get current configuration from singleton
        ChainFunctionConfig config = ChainFunctionConfig.getInstance();
        List<String> startFunc = config.getStartFunc();
        List<String> postProcessing = config.getPostProcessing();
        Map<String, List<String>> callTree = config.getCallTree();

        for (int i = 0; i < nodes.size(); i++) {
            if (i == 0) {
                // Check if first function is allowed to start a chain
                if (!startFunc.contains(nodes.get(0).getLabel())) {
                    throw new TranslatorException("{} cannot be the first operator", nodes.get(0).getLexeme(), nodes.get(0).getLabel());
                }
            }
            if (i == nodes.size() - 1) {
                // Check if last function is allowed to end a chain
                if (!postProcessing.contains(nodes.get(nodes.size() - 1).getLabel())) {
                    throw new TranslatorException("{} cannot be the last operator", nodes.get(nodes.size() - 1).getLexeme(), nodes.get(nodes.size() - 1).getLabel());
                }
                break;
            }

            var curFunc = nodes.get(i).getLabel();
            var nextFunc = nodes.get(i + 1).getLabel();

            // Check if current function is defined in call tree
            if (callTree.containsKey(curFunc)) {
                // Check if transition from current to next function is allowed
                if (!callTree.get(curFunc).contains(nextFunc)) {
                    throw new TranslatorException("'{}' operator cannot follow by '{}' operator", nodes.get(i+1).getLexeme(),nextFunc, curFunc);
                }
            } else {
                throw new TranslatorException("{} operator is not defined", nodes.get(i).getLexeme(), curFunc);
            }
        }
    }

}
