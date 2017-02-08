package org.stepik.core.utils;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;
import org.jetbrains.annotations.NotNull;

/**
 * @author meanmail
 */
public class PluginUtils {
    public static final String PLUGIN_ID = "org.stepik.plugin.union";
    private static final String DEFAULT_PLUGIN_VERSION = "unknown";

    @NotNull
    public static String getVersion() {
        PluginId pluginId = PluginId.getId(PLUGIN_ID);
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
        return plugin != null ? plugin.getVersion() : DEFAULT_PLUGIN_VERSION;
    }
}
