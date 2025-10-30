package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Lexer;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Unit tests for MultiSourceEventStmt parsing and detection.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1
 */
class MultiSourceEventStmtTest {

    @Test
    void testIsMultiSourceEventTrue() throws LexicalException {
        String code = "{#temp:<30#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        boolean result = MultiSourceEventStmt.isMultiSourceEvent(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertTrue(result, "Should detect {# pattern as multi-source event");
    }

    @Test
    void testIsMultiSourceEventFalse() throws LexicalException {
        String code = "{>30}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        boolean result = MultiSourceEventStmt.isMultiSourceEvent(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertFalse(result, "Should not detect {> pattern as multi-source event");
    }

    @Test
    void testIsMultiSourceEventArrowExpression() throws LexicalException {
        String code = "{>=1}->{<2}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        boolean result = MultiSourceEventStmt.isMultiSourceEvent(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertFalse(result, "Should not detect arrow expression as multi-source event");
    }

    @Test
    void testParseSingleSource() throws LexicalException, ParseException {
        String code = "{#temp:<30#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        ASTNode node = MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(node);
        Assertions.assertEquals(ASTNodeTypes.MULTI_SOURCE_EVENT_EXPR, node.getType());
        Assertions.assertEquals(1, node.getChildren().size());
        
        // The child should be an EventSourceCondition
        ASTNode child = node.getChildren(0);
        Assertions.assertTrue(child instanceof EventSourceCondition);
        EventSourceCondition condition = (EventSourceCondition) child;
        Assertions.assertEquals("temp", condition.getEventSourceAlias());
    }

    @Test
    void testParseTwoSourcesAnd() throws LexicalException, ParseException {
        String code = "{#temp:<30# && #humidity:>80#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        ASTNode node = MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(node);
        Assertions.assertEquals(ASTNodeTypes.MULTI_SOURCE_EVENT_EXPR, node.getType());
        Assertions.assertEquals(1, node.getChildren().size());
        
        // The child should be a binary expression (&&)
        ASTNode child = node.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.BINARY_EXPR, child.getType());
        Assertions.assertEquals("&&", child.getLexeme().getValue());
        
        // Binary expression should have two children
        Assertions.assertEquals(2, child.getChildren().size());
        
        // Left child: temp condition
        ASTNode left = child.getChildren(0);
        Assertions.assertTrue(left instanceof EventSourceCondition);
        Assertions.assertEquals("temp", ((EventSourceCondition) left).getEventSourceAlias());
        
        // Right child: humidity condition
        ASTNode right = child.getChildren(1);
        Assertions.assertTrue(right instanceof EventSourceCondition);
        Assertions.assertEquals("humidity", ((EventSourceCondition) right).getEventSourceAlias());
    }

    @Test
    void testParseTwoSourcesOr() throws LexicalException, ParseException {
        String code = "{#temp:<30# || #humidity:>80#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        ASTNode node = MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(node);
        Assertions.assertEquals(ASTNodeTypes.MULTI_SOURCE_EVENT_EXPR, node.getType());
        
        // The child should be a binary expression (||)
        ASTNode child = node.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.BINARY_EXPR, child.getType());
        Assertions.assertEquals("||", child.getLexeme().getValue());
    }

    @Test
    void testParseThreeSources() throws LexicalException, ParseException {
        String code = "{#temp:<30# && #humidity:>80# || #pressure:>1000#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        ASTNode node = MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(node);
        Assertions.assertEquals(ASTNodeTypes.MULTI_SOURCE_EVENT_EXPR, node.getType());
        
        // The root child should be a binary expression (||) - last operator
        ASTNode child = node.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.BINARY_EXPR, child.getType());
        Assertions.assertEquals("||", child.getLexeme().getValue());
        
        // Left side should be another binary expression (&&)
        ASTNode left = child.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.BINARY_EXPR, left.getType());
        Assertions.assertEquals("&&", left.getLexeme().getValue());
    }

    @Test
    void testParseWithRangeCondition() throws LexicalException, ParseException {
        String code = "{#temp:[20,30]# && #humidity:>80#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        ASTNode node = MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(node);
        Assertions.assertEquals(ASTNodeTypes.MULTI_SOURCE_EVENT_EXPR, node.getType());
        
        // Verify the structure
        ASTNode binaryExpr = node.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.BINARY_EXPR, binaryExpr.getType());
        
        // Left child should have a range expression
        EventSourceCondition tempCondition = (EventSourceCondition) binaryExpr.getChildren(0);
        Assertions.assertEquals("temp", tempCondition.getEventSourceAlias());
        Assertions.assertEquals(ASTNodeTypes.RANGE_EXPR, tempCondition.getChildren(0).getType());
    }

    @Test
    void testParseWithComplexCondition() throws LexicalException, ParseException {
        String code = "{#temp:>20 && <30# && #humidity:>=80#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        ASTNode node = MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(node);
        Assertions.assertEquals(ASTNodeTypes.MULTI_SOURCE_EVENT_EXPR, node.getType());
    }

    @Test
    void testParseMissingOpeningBrace() throws LexicalException {
        String code = "#temp:<30# && #humidity:>80#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        Assertions.assertThrows(ParseException.class, () -> {
            MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        });
    }

    @Test
    void testParseMissingClosingBrace() throws LexicalException {
        String code = "{#temp:<30# && #humidity:>80#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        Assertions.assertThrows(ParseException.class, () -> {
            MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        });
    }

    @Test
    void testParseInvalidOperator() throws LexicalException {
        String code = "{#temp:<30# + #humidity:>80#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        Assertions.assertThrows(ParseException.class, () -> {
            MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        });
    }

    @Test
    void testParseWithFloats() throws LexicalException, ParseException {
        String code = "{#temp:<30.5# && #humidity:>80.0#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        ASTNode node = MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(node);
        Assertions.assertEquals(ASTNodeTypes.MULTI_SOURCE_EVENT_EXPR, node.getType());
    }

    @Test
    void testParseFourSources() throws LexicalException, ParseException {
        String code = "{#temp:<30# && #humidity:>80# || #pressure:>1000# && #wind:<20#}";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        ASTNode node = MultiSourceEventStmt.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(node);
        Assertions.assertEquals(ASTNodeTypes.MULTI_SOURCE_EVENT_EXPR, node.getType());
        
        // Verify it's a binary tree structure
        ASTNode root = node.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.BINARY_EXPR, root.getType());
    }
}

