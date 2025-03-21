package com.df.rhythmix.translate;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.exception.TypeInferException;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.lexer.TokenType;
import com.df.rhythmix.parser.ast.ASTNode;
import com.df.rhythmix.parser.ast.Expr;
import com.df.rhythmix.util.PeekTokenIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

class TypeInferTest {

    @Test
    void typeInfer1() throws LexicalException, ParseException, TypeInferException, TranslatorException {
        String code = "1/10";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        TokenType tokenType = TypeInfer.infer(parse, new EnvProxy());
        Assertions.assertEquals(TokenType.FLOAT,tokenType);
    }

    @Test
    void typeInfer2() throws LexicalException, ParseException, TypeInferException, TranslatorException {
        String code = "1*10";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        TokenType tokenType = TypeInfer.infer(parse, new EnvProxy());
        Assertions.assertEquals(TokenType.INTEGER,tokenType);
    }

    @Test
    void typeInfer3() throws LexicalException, ParseException, TypeInferException, TranslatorException {
        String code = "1+10";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        TokenType tokenType = TypeInfer.infer(parse,  new EnvProxy());
        Assertions.assertEquals(TokenType.INTEGER,tokenType);
    }

    @Test
    void typeInfer4() throws LexicalException, ParseException, TypeInferException, TranslatorException {
        String code = "1-10";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        TokenType tokenType = TypeInfer.infer(parse,  new EnvProxy());
        Assertions.assertEquals(TokenType.INTEGER,tokenType);
    }

    @Test
    void typeInfer5() throws LexicalException, ParseException, TypeInferException, TranslatorException {
        String code = "((1-10)*(-2))/10+100";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        TokenType tokenType = TypeInfer.infer(parse,  new EnvProxy());
        Assertions.assertEquals(TokenType.FLOAT,tokenType);
    }

    @Test
    void typeInfer6() throws LexicalException, ParseException, TypeInferException, TranslatorException {
        String code = "1+MAX";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        EnvProxy env = new EnvProxy();
        env.rawPut("MAX","100");
        TokenType tokenType = TypeInfer.infer(parse,env);
        Assertions.assertEquals(TokenType.INTEGER,tokenType);
    }

    @Test
    void typeInfer7() throws LexicalException, ParseException, TypeInferException, TranslatorException {
        String code = "(1+MAX)/K";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        EnvProxy env = new EnvProxy();
        env.rawPut("MAX","100");
        env.rawPut("K","10.0");
        TokenType tokenType = TypeInfer.infer(parse,env);
        Assertions.assertEquals(TokenType.FLOAT,tokenType);
    }

    @Test
    void typeInfer8() throws LexicalException, ParseException, TypeInferException, TranslatorException {
        String code = "(1+MAX)/K";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        ASTNode parse = Expr.parse(new PeekTokenIterator(tokens.stream()));
        EnvProxy env = new EnvProxy();
        env.put("MAX","100");
        Assertions.assertThrows(TypeInferException.class,()->{
            TokenType tokenType = TypeInfer.infer(parse,env);
        });
    }
}