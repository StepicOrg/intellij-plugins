package org.stepik.gradle.plugins.jetbrains;

import org.gradle.api.Project;
import org.gradle.api.Task;
import org.gradle.api.file.FileCollection;
import org.gradle.api.plugins.JavaPluginConvention;
import org.gradle.api.tasks.SourceSet;
import org.jdom2.Attribute;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashSet;
import java.util.Set;

/**
 * @author meanmail
 */
class Utils {
    private static final String APPLICATION = "application";
    private static final String COMPONENT = "component";
    private static final String NAME = "name";
    private static final String UPDATES_CONFIGURABLE = "UpdatesConfigurable";
    private static final String OPTION = "option";
    private static final String CHECK_NEEDED = "CHECK_NEEDED";
    private static final String VALUE = "value";
    private static final String FALSE = "false";

    @Nullable
    static Document getXmlDocument(@NotNull File file) {
        try {
            SAXBuilder builder = new SAXBuilder();
            return builder.build(file);
        } catch (JDOMException | IOException e) {
            return null;
        }
    }

    @Nullable
    static Document getXmlDocument(@NotNull InputStream input) {
        try {
            SAXBuilder builder = new SAXBuilder();
            return builder.build(input);
        } catch (JDOMException | IOException e) {
            return null;
        }
    }

    @NotNull
    private static SourceSet mainSourceSet(@NotNull Project project) {
        JavaPluginConvention javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class);
        return javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME);
    }

    @NotNull
    static FileCollection sourcePluginXmlFiles(@NotNull Project project) {
        final Set<File> result = new HashSet<>();
        mainSourceSet(project).getResources().getSrcDirs().forEach(srcDir -> {
            final File pluginXml = new File(srcDir, "META-INF/plugin.xml");
            if (pluginXml.exists()) {
                if (isPluginXmlFile(pluginXml)) {
                    result.add(pluginXml);
                }
            }
        });

        return project.files(result);
    }

    private static boolean isPluginXmlFile(@NotNull File file) {
        Document doc = getXmlDocument(file);
        return doc != null && doc.getRootElement().getName().equals("idea-plugin");
    }

    @Nullable
    static Task findTask(@NotNull Project project, @NotNull String taskName) {
        return project.getTasks().findByName(taskName);
    }

    static void outputXml(@NotNull Document doc, @NotNull File file) throws IOException {
        XMLOutputter outputter = new XMLOutputter();
        Format format = Format.getPrettyFormat();
        format.setIndent("    ");
        outputter.setFormat(format);
        outputter.output(doc, new FileOutputStream(file));
    }

    static void setAttributeValue(@NotNull Element node, @NotNull String name, @Nullable String value) {
        Attribute attribute = node.getAttribute(name);

        if (value == null) {
            if (attribute != null) {
                node.removeAttribute(attribute);
            }
            return;
        }

        node.setAttribute(name, value);
    }

    static Document createUpdatesXml() {
        Document doc = new Document();

        Element applicationNode = new Element(APPLICATION);
        doc.setRootElement(applicationNode);

        Element component = new Element(COMPONENT);
        applicationNode.addContent(component);

        Utils.setAttributeValue(component, NAME, UPDATES_CONFIGURABLE);

        Element option = new Element(OPTION);
        component.addContent(option);
        Utils.setAttributeValue(option, NAME, CHECK_NEEDED);
        Utils.setAttributeValue(option, VALUE, FALSE);

        return doc;
    }

    static void repairUpdateXml(Document doc) {
        Element applicationNode;

        if (!doc.hasRootElement()) {
            applicationNode = new Element(APPLICATION);
            doc.setRootElement(applicationNode);
        }

        applicationNode = doc.getRootElement();

        if (!APPLICATION.equals(applicationNode.getName())) {
            applicationNode.setName(APPLICATION);
        }

        Element component = applicationNode.getChildren(COMPONENT)
                .stream()
                .filter(element -> {
                    Attribute attr = element.getAttribute(NAME);
                    return attr != null && UPDATES_CONFIGURABLE.equals(attr.getValue());
                })
                .findFirst()
                .orElseGet(() -> null);

        if (component == null) {
            component = new Element(COMPONENT);
            applicationNode.addContent(component);
        }

        Attribute name = component.getAttribute(NAME);

        if (name == null || UPDATES_CONFIGURABLE.equals(name.getValue())) {
            Utils.setAttributeValue(component, NAME, UPDATES_CONFIGURABLE);
        }

        Element option = component.getChildren(OPTION)
                .stream()
                .filter(element -> {
                    Attribute attr = element.getAttribute(NAME);
                    return attr != null && CHECK_NEEDED.equals(attr.getValue());
                })
                .findFirst()
                .orElseGet(() -> null);

        if (option == null) {
            option = new Element(OPTION);
            component.addContent(option);
            Utils.setAttributeValue(option, NAME, CHECK_NEEDED);
        }

        Utils.setAttributeValue(option, VALUE, FALSE);
    }

    @Nullable
    static String readFromFile(@NotNull File file) {
        if (!file.exists()) {
            return null;
        }

        StringBuilder sb = new StringBuilder();

        try {
            Files.lines(file.toPath(), Charset.forName("UTF-8"))
                    .forEachOrdered(line -> sb.append(line).append("\n"));

            return sb.toString();
        } catch (IOException e) {
            return null;
        }
    }

    @NotNull
    static File getDefaultIdePath(
            @NotNull Project project,
            @NotNull BasePlugin plugin, @NotNull String type,
            @NotNull String version) {
        final String gradleHomePath = project.getGradle().getGradleUserHomeDir().getAbsolutePath();
        final String name = plugin.getProductName().toLowerCase();
        final String defaultRelativePath = String.join("/", "caches/modules-2/files-2.1/",
                plugin.getProductGroup(), name, name + type);

        return new File(gradleHomePath, defaultRelativePath + "/" + version);
    }

    static void deleteDirectory(Path pluginPath) throws IOException {
        if (!Files.exists(pluginPath)) {
            return;
        }

        Files.walkFileTree(pluginPath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.delete(dir);
                } catch (DirectoryNotEmptyException ignore) {
                }
                return FileVisitResult.CONTINUE;
            }
        });

        try {
            Files.deleteIfExists(pluginPath);
        } catch (DirectoryNotEmptyException ignore) {
        }
    }
}
