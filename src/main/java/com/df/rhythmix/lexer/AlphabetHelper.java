package com.df.rhythmix.lexer;

import java.util.regex.Pattern;

/**
 * Helper class for identifying character types using regular expressions.
 * <p>
 * This utility class provides static methods to check if a character is a letter,
 * a number, an operator, or a literal, which is useful during lexical analysis.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class AlphabetHelper {
    static Pattern ptnLetter = Pattern.compile("^[a-zA-Z]$");
    static Pattern ptnNumber = Pattern.compile("^[0-9]$");
    static Pattern ptnLiteral = Pattern.compile("^[_a-zA-Z0-9$]$");
    static Pattern ptnOperator = Pattern.compile("^[*+-<>=!&|^%/,]$");

    /**
     * Checks if a character is an alphabet letter (a-z, A-Z).
     *
     * @param c the character to check
     * @return {@code true} if the character is a letter; {@code false} otherwise
     */
    public static boolean isLetter(char c) {
        return ptnLetter.matcher(c + "").matches();
    }

    /**
     * Checks if a character is a numeric digit (0-9).
     *
     * @param c the character to check
     * @return {@code true} if the character is a number; {@code false} otherwise
     */
    public static boolean isNumber(char c) {
        return ptnNumber.matcher(c + "").matches();
    }

    /**
     * Checks if a character is a supported operator.
     *
     * @param c the character to check
     * @return {@code true} if the character is an operator; {@code false} otherwise
     */
    public static boolean isOperator(char c) {
        return ptnOperator.matcher(c + "").matches();
    }



    /**
     * Checks if a character can be part of a literal (e.g., variable name).
     * Literals can contain letters, numbers, underscore, and the '$' symbol.
     *
     * @param c the character to check
     * @return {@code true} if the character is a valid literal character; {@code false} otherwise
     */
    public static boolean isLiteral(char c) {
        return ptnLiteral.matcher(c + "").matches();
    }
}
