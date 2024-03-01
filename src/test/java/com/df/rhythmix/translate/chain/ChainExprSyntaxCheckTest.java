package com.df.rhythmix.translate.chain;

import com.df.rhythmix.exception.LexicalException;
import com.df.rhythmix.exception.ParseException;
import com.df.rhythmix.exception.TranslatorException;
import com.df.rhythmix.lexer.Lexer;
import com.df.rhythmix.lexer.Token;
import com.df.rhythmix.pebble.TemplateEngine;
import com.df.rhythmix.translate.ChainExpr;
import com.df.rhythmix.translate.EnvProxy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;

class ChainExprSyntaxCheckTest {

    @Test
    void check1() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "collect().count().meet(==2)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        Assertions.assertDoesNotThrow(()->ChainExpr.translate(tokens, env));
    }


    @Test
    void check2() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "count().meet(==2)";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class,()->ChainExpr.translate(tokens, env));
    }

    @Test
    void check3() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "collect().count()";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class,()->ChainExpr.translate(tokens, env));
    }
    @Test
    void check4() throws LexicalException, TranslatorException, IOException, ParseException {
        TemplateEngine.enableDebugModel(true);
        String code = "collect().sum().take(-3).count()";
        Lexer lexer = new Lexer();
        ArrayList<Token> tokens = lexer.analyse(code.chars().mapToObj(x -> (char) x));
        EnvProxy env = new EnvProxy();
        Assertions.assertThrows(TranslatorException.class,()->ChainExpr.translate(tokens, env));
    }
}