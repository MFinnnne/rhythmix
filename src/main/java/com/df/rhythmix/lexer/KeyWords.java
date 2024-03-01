package com.df.rhythmix.lexer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KeyWords {
    static String[] keywords = {
            "let",
            "if",
            "else",
            "for",
            "while",
            "break",
            "func",
            "return",
    };


    static Set<String> set = new HashSet<>(Arrays.asList(keywords));

    public static boolean isKeyword(String word) {
        return set.contains(word);
    }
}
