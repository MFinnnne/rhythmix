package io.github.mfinnnne.rhythmix.translate;

import io.github.mfinnnne.rhythmix.exception.LexicalException;
import io.github.mfinnnne.rhythmix.exception.TranslatorException;
import io.github.mfinnnne.rhythmix.pebble.TemplateEngine;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for MultiSourceEventExpr translation.
 *
 * @author MFine
 * @version 1.0
 * @since 1.1
 */
class MultiSourceEventExprTest {

    @Test
    void testMultiSourceTranslationTwoSourcesAnd() throws LexicalException, TranslatorException {
        String code = "{#temp:<30# && #humidity:>80#}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        
        // Verify the translated code contains expected elements
        Assertions.assertTrue(translatedCode.contains("event.name"), 
            "Translated code should contain event.name for routing");
        Assertions.assertTrue(translatedCode.contains("tempState"), 
            "Translated code should contain tempState variable");
        Assertions.assertTrue(translatedCode.contains("humidityState"), 
            "Translated code should contain humidityState variable");
        Assertions.assertTrue(translatedCode.contains("\"temp\""), 
            "Translated code should check for temp event source");
        Assertions.assertTrue(translatedCode.contains("\"humidity\""), 
            "Translated code should check for humidity event source");
        Assertions.assertTrue(translatedCode.contains("&&"), 
            "Translated code should contain AND operator");
        Assertions.assertTrue(translatedCode.contains("event.value"), 
            "Translated code should reference event.value");
    }

    @Test
    void testMultiSourceTranslationTwoSourcesOr() throws LexicalException, TranslatorException {
        String code = "{#temp:<30# || #humidity:>80#}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        
        // Verify the translated code contains expected elements
        Assertions.assertTrue(translatedCode.contains("event.name"), 
            "Translated code should contain event.name for routing");
        Assertions.assertTrue(translatedCode.contains("tempState"), 
            "Translated code should contain tempState variable");
        Assertions.assertTrue(translatedCode.contains("humidityState"), 
            "Translated code should contain humidityState variable");
        Assertions.assertTrue(translatedCode.contains("||"), 
            "Translated code should contain OR operator");
    }

    @Test
    void testMultiSourceTranslationSingleSource() throws LexicalException, TranslatorException {
        String code = "{#temp:<30#}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        
        // Verify the translated code contains expected elements
        Assertions.assertTrue(translatedCode.contains("event.name"), 
            "Translated code should contain event.name for routing");
        Assertions.assertTrue(translatedCode.contains("tempState"), 
            "Translated code should contain tempState variable");
        Assertions.assertTrue(translatedCode.contains("\"temp\""), 
            "Translated code should check for temp event source");
        Assertions.assertTrue(translatedCode.contains("<"), 
            "Translated code should contain comparison operator");
    }

    @Test
    void testMultiSourceTranslationThreeSources() throws LexicalException, TranslatorException {
        String code = "{#temp:<30# && #humidity:>80# || #pressure:>1000#}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        
        // Verify the translated code contains expected elements
        Assertions.assertTrue(translatedCode.contains("tempState"), 
            "Translated code should contain tempState variable");
        Assertions.assertTrue(translatedCode.contains("humidityState"), 
            "Translated code should contain humidityState variable");
        Assertions.assertTrue(translatedCode.contains("pressureState"), 
            "Translated code should contain pressureState variable");
        Assertions.assertTrue(translatedCode.contains("\"temp\""), 
            "Translated code should check for temp event source");
        Assertions.assertTrue(translatedCode.contains("\"humidity\""), 
            "Translated code should check for humidity event source");
        Assertions.assertTrue(translatedCode.contains("\"pressure\""), 
            "Translated code should check for pressure event source");
    }

    @Test
    void testMultiSourceTranslationWithRange() throws LexicalException, TranslatorException {
        String code = "{#temp:[20,30]# && #humidity:>80#}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        
        // Verify the translated code contains expected elements
        Assertions.assertTrue(translatedCode.contains("tempState"), 
            "Translated code should contain tempState variable");
        Assertions.assertTrue(translatedCode.contains("humidityState"), 
            "Translated code should contain humidityState variable");
        // Range expressions should be translated to compound conditions
        Assertions.assertTrue(translatedCode.contains(">=") || translatedCode.contains("<="), 
            "Translated code should contain range operators");
    }

    @Test
    void testMultiSourceTranslationWithComplexCondition() throws LexicalException, TranslatorException {
        String code = "{#temp:>20 && <30# && #humidity:>=80#}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        
        // Verify the translated code contains expected elements
        Assertions.assertTrue(translatedCode.contains("tempState"), 
            "Translated code should contain tempState variable");
        Assertions.assertTrue(translatedCode.contains("humidityState"), 
            "Translated code should contain humidityState variable");
        Assertions.assertTrue(translatedCode.contains("&&"), 
            "Translated code should contain AND operator");
    }

    @Test
    void testMultiSourceTranslationStateReset() throws LexicalException, TranslatorException {
        String code = "{#temp:<30# && #humidity:>80#}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        
        // Verify that states are reset after successful match
        // The template should set states to false after returning true
        int tempStateCount = countOccurrences(translatedCode, "tempState");
        int humidityStateCount = countOccurrences(translatedCode, "humidityState");
        
        // Each state should appear at least 3 times:
        // 1. Initialization check (if nil)
        // 2. Update when event arrives
        // 3. Reset after successful match
        Assertions.assertTrue(tempStateCount >= 3, 
            "tempState should appear at least 3 times (init, update, reset)");
        Assertions.assertTrue(humidityStateCount >= 3, 
            "humidityState should appear at least 3 times (init, update, reset)");
    }

    @Test
    void testMultiSourceTranslationReturnStatements() throws LexicalException, TranslatorException {
        String code = "{#temp:<30# && #humidity:>80#}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);
        
        // Verify return statements
        Assertions.assertTrue(translatedCode.contains("return true"), 
            "Translated code should return true when conditions are met");
        Assertions.assertTrue(translatedCode.contains("return false"), 
            "Translated code should return false when conditions are not met");
    }


    @Test
    void testMultiSourceTranslationWithChainExpression() throws LexicalException, TranslatorException {
        String code = "{#temp: filter(>0).limit(5).sum().meet(>100)# && #humidity:>80#}";
        TemplateEngine.enableDebugModel(true);
        EnvProxy env = new EnvProxy();
        String translatedCode = Translator.translate(code, env);

        // Verify return statements
        Assertions.assertTrue(translatedCode.contains("return true"),
                "Translated code should return true when conditions are met");
        Assertions.assertTrue(translatedCode.contains("return false"),
                "Translated code should return false when conditions are not met");
    }

    /**
     * Helper method to count occurrences of a substring in a string.
     */
    private int countOccurrences(String str, String substring) {
        int count = 0;
        int index = 0;
        while ((index = str.indexOf(substring, index)) != -1) {
            count++;
            index += substring.length();
        }
        return count;
    }
}

