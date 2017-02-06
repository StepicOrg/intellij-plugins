package org.stepik.core.utils;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.JDOMUtil;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.IOException;

/**
 * @author meanmail
 */
public class PluginUtils {
    private static final Logger logger = Logger.getInstance(PluginUtils.class);
    private static final String DEFAULT_PLUGIN_VERSION = "Unknown";

    public static String getVersion() {
        try {
            Document e = JDOMUtil.loadDocument(PluginUtils.class, "/META-INF/plugin.xml");
            Element root = e.getRootElement();
            if (root == null) {
                return DEFAULT_PLUGIN_VERSION;
            }
            Element version = root.getChild("version");
            if (version == null) {
                return DEFAULT_PLUGIN_VERSION;
            }
            return version.getText();
        } catch (JDOMException | IOException e) {
            logger.warn("Failed get plugin version", e);
            return DEFAULT_PLUGIN_VERSION;
        }
    }
}
