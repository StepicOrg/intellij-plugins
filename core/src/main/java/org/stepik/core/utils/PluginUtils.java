package org.stepik.core.utils;

import com.intellij.ide.plugins.IdeaPluginDescriptor;
import com.intellij.ide.plugins.PluginManager;
import com.intellij.openapi.extensions.PluginId;

/**
 * @author meanmail
 */
public class PluginUtils {
    private static final String DEFAULT_PLUGIN_VERSION = "unknown";

    public static String getVersion() {
        PluginId pluginId = PluginId.getId("org.stepik.plugin.union");
        IdeaPluginDescriptor plugin = PluginManager.getPlugin(pluginId);
        return plugin != null ? plugin.getVersion() : DEFAULT_PLUGIN_VERSION;
    }
}
