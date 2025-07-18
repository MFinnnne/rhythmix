package com.df.rhythmix.pebble;

import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.template.PebbleTemplate;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

class DebugTokenParserTest {
    public static final PebbleEngine ENGINE = new PebbleEngine.Builder().extension(new CustomExtension()).autoEscaping(false).build();


    @BeforeAll
    static void beforeAll() {
    }

    @Test
    void testDebugTag0() throws IOException {
        TemplateEngine.enableDebugModel(true);
        PebbleTemplate template = ENGINE.getTemplate("debug.peb");
        Map<String,Object> context = new HashMap<>();
        Writer writer = new StringWriter();
        Assertions.assertDoesNotThrow(()->{
            template.evaluate(writer,context);
        });
    }

    @Test
    void testDebugTag1() throws IOException {
        TemplateEngine.enableDebugModel(true);
        PebbleTemplate template = ENGINE.getTemplate("debug1.peb");
        Map<String,Object> context = new HashMap<>();
        Writer writer = new StringWriter();
        Assertions.assertDoesNotThrow(()->{
            template.evaluate(writer,context);
        });
    }
}