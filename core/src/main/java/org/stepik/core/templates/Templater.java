package org.stepik.core.templates;

import com.intellij.openapi.diagnostic.Logger;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jetbrains.annotations.NotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @author meanmail
 */
public class Templater {
    private static final Logger logger = Logger.getInstance(Templater.class);
    private static Configuration config;

    @NotNull
    public static String processTemplate(@NotNull String templateName, @NotNull Map<String, Object> map) {
        if (config == null) {
            initConfig();
        }

        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Template template = config.getTemplate(templateName + ".ftl");
            template.process(map, new PrintWriter(out));
            return out.toString();
        } catch (TemplateException | IOException e) {
            logger.warn(e);
        }

        return "";
    }

    private static void initConfig() {
        config = new Configuration(Configuration.VERSION_2_3_23);
        config.setClassLoaderForTemplateLoading(Templater.class.getClassLoader(), "/templates");
    }
}
