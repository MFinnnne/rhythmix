package com.df.rhythmix.pebble;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.cache.tag.NoOpTagCache;
import com.mitchellbosecke.pebble.cache.template.NoOpTemplateCache;

public class TemplateEngine {
    public static final PebbleEngine ENGINE = new PebbleEngine.Builder().extension(new CustomExtension()).tagCache(new NoOpTagCache()).templateCache(new NoOpTemplateCache()).autoEscaping(false).cacheActive(false).build();
    public static void enableDebugModel(boolean enable) {
        if (enable) {
            ENGINE.getExtensionRegistry().getGlobalVariables().put("debug", true);
        } else {
            ENGINE.getExtensionRegistry().getGlobalVariables().put("debug", false);
        }
    }

    public static boolean isDebugMode() {
        return (boolean) ENGINE.getExtensionRegistry().getGlobalVariables().getOrDefault("debug", false);
    }
}
