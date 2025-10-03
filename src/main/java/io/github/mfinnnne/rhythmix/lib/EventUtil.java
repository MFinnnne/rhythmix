package io.github.mfinnnne.rhythmix.lib;

import cn.hutool.core.bean.BeanUtil;
import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.lexer.Lexer;
import io.github.mfinnnne.rhythmix.lexer.Token;
import io.github.mfinnnne.rhythmix.util.RhythmixEventData;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Utility class for handling event data within Rhythmix.
 * <p>
 * This class provides helper methods for processing lists of event objects,
 * specifically for converting them into a list of {@link Token}s that represent
 * the event values.
 *
 * @author MFine
 * @version 1.0
 * @since 1.0
 */
public class EventUtil {
    private static final Lexer LEXER = new Lexer();
    /**
     * Converts a list of event objects into a list of value tokens.
     * <p>
     * Each object in the input list is expected to be a bean (like {@link RhythmixEventData})
     * with a "value" property. This method extracts the value, tokenizes it, and returns the first
     * token from the result.
     *
     * @param values a list of event objects
     * @return a list of first {@link Token}s representing each event's value
     * @throws RuntimeException if lexical analysis of any value fails
     */
    public static List<Token> event2ValueToken(List<Object> values){

        return values.stream().map(item -> {
            Map<String, Object> pd = BeanUtil.beanToMap(item);
            String code = pd.get("value").toString();
            try {
                ArrayList<Token> token = LEXER.analyse(code.chars().mapToObj(x -> (char) x));
                return token.get(0);
            } catch (LexicalException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
    }
}
