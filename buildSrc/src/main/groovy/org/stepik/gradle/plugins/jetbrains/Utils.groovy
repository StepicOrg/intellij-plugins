package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.process.JavaForkOptions
import org.jdom2.Attribute
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.JDOMException
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * @author meanmail
 */
class Utils {
    private static final String APPLICATION = "application"
    private static final String COMPONENT = "component"
    private static final String NAME = "name"
    private static final String UPDATES_CONFIGURABLE = "UpdatesConfigurable"
    private static final String OPTION = "option"
    private static final String CHECK_NEEDED = "CHECK_NEEDED"
    private static final String VALUE = "value"
    private static final String FALSE = "false"

    @Nullable
    static Document getXmlDocument(@NotNull File file) {
        try {
            def builder = new SAXBuilder()
            return builder.build(file)
        } catch (JDOMException | IOException ignored) {
            return null
        }
    }

    @Nullable
    static Document getXmlDocument(@NotNull InputStream input) {
        try {
            def builder = new SAXBuilder()
            return builder.build(input)
        } catch (JDOMException | IOException ignored) {
            return null
        }
    }

    @NotNull
    private static SourceSet mainSourceSet(@NotNull Project project) {
        def javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class)
        return javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    }

    @NotNull
    private static SourceSet testSourceSet(@NotNull Project project) {
        def javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class)
        return javaConvention.getSourceSets().getByName(SourceSet.TEST_SOURCE_SET_NAME)
    }

    @NotNull
    static FileCollection sourcePluginXmlFiles(@NotNull Project project) {
        final def result = new HashSet<>()
        mainSourceSet(project).getResources().getSrcDirs().each {
            final def pluginXml = new File(it, "META-INF/plugin.xml")
            if (pluginXml.exists()) {
                if (isPluginXmlFile(pluginXml)) {
                    result.add(pluginXml)
                }
            }
        }

        return project.files(result)
    }

    private static boolean isPluginXmlFile(@NotNull File file) {
        def doc = getXmlDocument(file)
        if (!doc || !doc.hasRootElement()) {
            return false
        }
        return doc.getRootElement().getName() == "idea-plugin"
    }

    @Nullable
    static Task findTask(@NotNull Project project, @NotNull String taskName) {
        return project.getTasks().findByName(taskName)
    }

    static void outputXml(@NotNull Document doc, @NotNull File file) throws IOException {
        def outputter = new XMLOutputter()
        def format = Format.getPrettyFormat()
        format.setIndent("    ")
        outputter.setFormat(format)
        outputter.output(doc, new FileOutputStream(file))
    }

    static void setAttributeValue(@NotNull Element node, @NotNull String name, @Nullable String value) {
        def attribute = node.getAttribute(name)

        if (value == null) {
            if (attribute != null) {
                node.removeAttribute(attribute)
            }
            return
        }

        node.setAttribute(name, value)
    }

    static Document createUpdatesXml() {
        def doc = new Document()

        def applicationNode = new Element(APPLICATION)
        doc.setRootElement(applicationNode)

        def component = new Element(COMPONENT)
        applicationNode.addContent(component)

        setAttributeValue(component, NAME, UPDATES_CONFIGURABLE)

        def option = new Element(OPTION)
        component.addContent(option)
        setAttributeValue(option, NAME, CHECK_NEEDED)
        setAttributeValue(option, VALUE, FALSE)

        return doc
    }

    static void repairUpdateXml(Document doc) {
        def applicationNode

        if (!doc.hasRootElement()) {
            applicationNode = new Element(APPLICATION)
            doc.setRootElement(applicationNode)
        }

        applicationNode = doc.getRootElement()

        if (APPLICATION != applicationNode.getName()) {
            applicationNode.setName(APPLICATION)
        }

        def component = applicationNode.getChildren(COMPONENT).find {
            Attribute attr = it.getAttribute(NAME)
            return attr != null && UPDATES_CONFIGURABLE == attr.getValue()
        }

        if (component == null) {
            component = new Element(COMPONENT)
            applicationNode.addContent(component)
        }

        def name = component.getAttribute(NAME)

        if (name == null || UPDATES_CONFIGURABLE == name.getValue()) {
            setAttributeValue(component, NAME, UPDATES_CONFIGURABLE)
        }

        def option = component.getChildren(OPTION).find {
            Attribute attr = it.getAttribute(NAME)
            return attr != null && CHECK_NEEDED == attr.getValue()
        }

        if (option == null) {
            option = new Element(OPTION)
            component.addContent(option)
            setAttributeValue(option, NAME, CHECK_NEEDED)
        }

        setAttributeValue(option, VALUE, FALSE)
    }

    @Nullable
    static String readFromFile(@NotNull File file) {
        if (!file.exists()) {
            return null
        }

        def sb = new StringBuilder()

        try {
            Files.lines(file.toPath(), Charset.forName("UTF-8")).each {
                sb.append(it).append("\n")
            }

            return sb.toString()
        } catch (IOException ignored) {
            return null
        }
    }

    @NotNull
    static File getDefaultIdePath(
            @NotNull Project project,
            @NotNull BasePlugin plugin,
            @NotNull String type,
            @NotNull String version) {
        final def gradleHomePath = project.getGradle().getGradleUserHomeDir().getAbsolutePath()
        final def name = plugin.getProductName().toLowerCase()
        final def defaultRelativePath = "caches/modules-2/files-2.1/$plugin.productGroup/$name/$name$type"

        return new File("$gradleHomePath/$defaultRelativePath/$version")
    }

    static void deleteDirectory(Path pluginPath) throws IOException {
        if (!Files.exists(pluginPath)) {
            return
        }

        Files.walkFileTree(pluginPath, new FileVisitor<Path>() {
            @Override
            FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE
            }

            @Override
            FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file)
                return FileVisitResult.CONTINUE
            }

            @Override
            FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE
            }

            @Override
            FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.delete(dir)
                } catch (DirectoryNotEmptyException ignore) {
                }
                return FileVisitResult.CONTINUE
            }
        })

        try {
            Files.deleteIfExists(pluginPath)
        } catch (DirectoryNotEmptyException ignore) {
        }
    }

    @NotNull
    static Map<String, String> getProductSystemProperties(
            @NotNull File configDirectory,
            @NotNull File systemDirectory,
            @NotNull File pluginsDirectory) {
        def map = new LinkedHashMap<>(3)
        map["idea.config.path"] = configDirectory.absolutePath
        map["idea.system.path"] = systemDirectory.absolutePath
        map["idea.plugins.path"] = pluginsDirectory.absolutePath

        map
    }

    @NotNull
    static List<String> getProductJvmArgs(
            @NotNull JavaForkOptions options,
            @NotNull List<String> originalArguments,
            @NotNull File idePath) {
        if (options.maxHeapSize == null) {
            options.maxHeapSize = "512m"
        }
        if (options.minHeapSize == null) {
            options.minHeapSize = "256m"
        }
        boolean hasPermSizeArg = false
        def result = []
        for (String arg : originalArguments) {
            if (arg.startsWith("-XX:MaxPermSize")) {
                hasPermSizeArg = true
            }
            result += arg
        }

        result += "-Xbootclasspath/a:${idePath.absolutePath}/lib/boot.jar"
        if (!hasPermSizeArg) result += "-XX:MaxPermSize=250m"
        return result
    }
}
