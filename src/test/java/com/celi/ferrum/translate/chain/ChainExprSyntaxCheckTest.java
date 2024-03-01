package com.celi.ferrum.translate.chain;

import com.celi.ferrum.exception.LexicalException;
import com.celi.ferrum.exception.ParseException;
import com.celi.ferrum.exception.TranslatorException;
import com.celi.ferrum.lexer.Lexer;
import com.celi.ferrum.lexer.Token;
import com.celi.ferrum.pebble.TemplateEngine;
import com.celi.ferrum.translate.ChainExpr;
import com.celi.ferrum.translate.EnvProxy;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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