package io.github.mfinnnne.rhythmix.lexer;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for managing and checking language keywords.
 * <p>
 * This class maintains a set of reserved keywords for the Rhythmix language
 * to distinguish them from variable names during lexical analysis.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class KeyWords {
    /**
     * The list of reserved keywords in the Rhythmix language.
     */
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


    /**
     * Backing set to provide efficient keyword lookup.
     */
    static Set<String> set = new HashSet<>(Arrays.asList(keywords));

    /**
     * Checks if a given word is a reserved keyword.
     *
     * @param word the word to check
     * @return {@code true} if the word is a keyword; {@code false} otherwise
     */
    public static boolean isKeyword(String word) {
        return set.contains(word);
    }
}
