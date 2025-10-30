package io.github.mfinnnne.rhythmix.parser.ast;

import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.ParseException;
import io.github.mfinnnne.rhythmix.lexer.Lexer;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.PeekTokenIterator;
import jdk.jshell.spi.ExecutionControl;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * Unit tests for EventSourceCondition parsing.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1
 */
class EventSourceConditionTest {

    @Test
    void testParseSimpleCondition() throws LexicalException, ParseException {
        String code = "#temp:<30#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        EventSourceCondition condition = EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(condition);
        Assertions.assertEquals("temp", condition.getEventSourceAlias());
        Assertions.assertEquals(1, condition.getChildren().size());
        
        // The child should be a compare expression
        ASTNode child = condition.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.COMPARE_EXPR, child.getType());
    }

    @Test
    void testParseGreaterThanCondition() throws LexicalException, ParseException {
        String code = "#humidity:>80#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        EventSourceCondition condition = EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(condition);
        Assertions.assertEquals("humidity", condition.getEventSourceAlias());
        Assertions.assertEquals(1, condition.getChildren().size());
    }

    @Test
    void testParseRangeCondition() throws LexicalException, ParseException {
        String code = "#temp:[20,30]#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        EventSourceCondition condition = EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(condition);
        Assertions.assertEquals("temp", condition.getEventSourceAlias());
        Assertions.assertEquals(1, condition.getChildren().size());
        
        // The child should be a range expression
        ASTNode child = condition.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.RANGE_EXPR, child.getType());
    }

    @Test
    void testParseChainCondition() throws LexicalException, ParseException {
        String code = "#temp:>20 && <30#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        EventSourceCondition condition = EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(condition);
        Assertions.assertEquals("temp", condition.getEventSourceAlias());
        Assertions.assertEquals(1, condition.getChildren().size());
        
        // The child should be a binary expression (&&)
        ASTNode child = condition.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.BINARY_EXPR, child.getType());
    }

    @Test
    void testParseFloatCondition() throws LexicalException, ParseException {
        String code = "#temp:<30.5#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        EventSourceCondition condition = EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(condition);
        Assertions.assertEquals("temp", condition.getEventSourceAlias());
        Assertions.assertEquals(1, condition.getChildren().size());
    }

    @Test
    void testParseEqualityCondition() throws LexicalException, ParseException {
        String code = "#status:==1#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        EventSourceCondition condition = EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(condition);
        Assertions.assertEquals("status", condition.getEventSourceAlias());
        Assertions.assertEquals(1, condition.getChildren().size());
    }

    @Test
    void testParseMissingOpeningHash() throws LexicalException {
        String code = "temp:<30#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        Assertions.assertThrows(ParseException.class, () -> {
            EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        });
    }

    @Test
    void testParseMissingColon() throws LexicalException {
        String code = "#temp<30#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        Assertions.assertThrows(ParseException.class, () -> {
            EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        });
    }

    @Test
    void testParseMissingClosingHash() throws LexicalException {
        String code = "#temp:<30";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        Assertions.assertThrows(ParseException.class, () -> {
            EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        });
    }

    @Test
    void testParseInvalidAlias() throws LexicalException {
        String code = "#123:<30#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        Assertions.assertThrows(ParseException.class, () -> {
            EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        });
    }

    @Test
    void testParseLongAliasName() throws LexicalException, ParseException {
        String code = "#temperatureSensor:<30#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        EventSourceCondition condition = EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(condition);
        Assertions.assertEquals("temperatureSensor", condition.getEventSourceAlias());
        Assertions.assertEquals(1, condition.getChildren().size());
    }

    @Test
    void testParseComplexExpression() throws LexicalException, ParseException {
        String code = "#temp:>20 && <30 || ==25#";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        
        EventSourceCondition condition = EventSourceCondition.parse(new PeekTokenIterator(tokens.stream()));
        
        Assertions.assertNotNull(condition);
        Assertions.assertEquals("temp", condition.getEventSourceAlias());
        Assertions.assertEquals(1, condition.getChildren().size());
        
        // The child should be a binary expression with || at the top level
        ASTNode child = condition.getChildren(0);
        Assertions.assertEquals(ASTNodeTypes.BINARY_EXPR, child.getType());
    }
}

