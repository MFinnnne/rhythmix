package io.github.mfinnnne.rhythmix.pebble;


import io.pebbletemplates.pebble.PebbleEngine;
import io.pebbletemplates.pebble.cache.tag.NoOpTagCache;
import io.pebbletemplates.pebble.cache.template.NoOpTemplateCache;

/**
 * <p>TemplateEngine class.</p>
 *
 * author MFine
 * version $Id: $Id
 */
public class TemplateEngine {
    /** Constant <code>ENGINE</code> */
    public static final PebbleEngine ENGINE = new PebbleEngine.Builder().extension(new CustomExtension()).tagCache(new NoOpTagCache()).templateCache(new NoOpTemplateCache()).autoEscaping(false).cacheActive(false).build();
    /**
     * <p>enableDebugModel.</p>
     *
     * @param enable a boolean.
     */
    public static void enableDebugModel(boolean enable) {
        if (enable) {
            ENGINE.getExtensionRegistry().getGlobalVariables().put("debug", true);
        } else {
            ENGINE.getExtensionRegistry().getGlobalVariables().put("debug", false);
        }
    }

    /**
     * <p>isDebugMode.</p>
     *
     * @return a boolean.
     */
    public static boolean isDebugMode() {
        return (boolean) ENGINE.getExtensionRegistry().getGlobalVariables().getOrDefault("debug", false);
    }
}
