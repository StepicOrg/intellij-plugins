package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.internal.Pair
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
import org.rauschig.jarchivelib.Archiver
import org.rauschig.jarchivelib.ArchiverFactory
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.Charset
import java.nio.file.*
import java.nio.file.attribute.BasicFileAttributes

/**
 * @author meanmail
 */
class Utils {
    private static final Logger logger = LoggerFactory.getLogger(Utils)
    private static final String APPLICATION = "application"
    private static final String COMPONENT = "component"
    private static final String NAME = "name"
    private static final String VALUE = "value"
    static final UPDATE_XML = [
            filename     : "updates.xml",
            componentName: "UpdatesConfigurable",
            optionTag    : "option",
            options      : [
                    Pair.of("CHECK_NEEDED", "false")
            ]
    ]
    static final IDE_GENERAL_XML = [
            filename     : "ide.general.xml",
            componentName: "GeneralSettings",
            optionTag    : "option",
            options      : [
                    Pair.of("confirmExit", "false"),
                    Pair.of("showTipsOnStartup", "false")
            ]
    ]
    static final OPTIONS_XML = [
            filename     : "options.xml",
            componentName: "PropertiesComponent",
            optionTag    : "property",
            options      : [
                    Pair.of("toolwindow.stripes.buttons.info.shown", "true")
            ]
    ]

    @Nullable
    static Document getXmlDocument(@NotNull File file) {
        try {
            def builder = new SAXBuilder()
            return builder.build(file)
        } catch (JDOMException | IOException ignored) {
            return null
        }
    }

    @NotNull
    static SourceSet mainSourceSet(@NotNull Project project) {
        def javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class)
        return javaConvention.getSourceSets().getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    }

    @NotNull
    static SourceSet testSourceSet(@NotNull Project project) {
        def javaConvention = project.getConvention().getPlugin(JavaPluginConvention.class)
        return javaConvention.getSourceSets().getByName(SourceSet.TEST_SOURCE_SET_NAME)
    }

    @NotNull
    static FileCollection sourcePluginXmlFiles(@NotNull Project project) {
        final result = new HashSet<>()
        mainSourceSet(project).getResources().getSrcDirs().each {
            final pluginXml = new File(it, "META-INF/plugin.xml")
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

    static Document createXml(Map map) {
        def doc = new Document()

        def applicationNode = new Element(APPLICATION)
        doc.setRootElement(applicationNode)

        def component = new Element(COMPONENT)
        applicationNode.addContent(component)

        setAttributeValue(component, NAME, map["componentName"] as String)

        map["options"].each { Pair option ->
            def optionTag = new Element(map["optionTag"] as String)
            component.addContent(optionTag)
            setAttributeValue(optionTag, NAME, option.getLeft() as String)
            setAttributeValue(optionTag, VALUE, option.getRight() as String)
        }

        return doc
    }

    static void repairXml(Document doc, Map map) {
        def applicationNode

        if (!doc.hasRootElement()) {
            applicationNode = new Element(APPLICATION)
            doc.setRootElement(applicationNode)
        }

        applicationNode = doc.getRootElement()

        if (APPLICATION != applicationNode.getName()) {
            applicationNode.setName(APPLICATION)
        }

        String componentName = map["componentName"]
        def component = applicationNode.getChildren(COMPONENT).find {
            Attribute attr = it.getAttribute(NAME)
            return attr != null && componentName == attr.getValue()
        }

        if (component == null) {
            component = new Element(COMPONENT)
            applicationNode.addContent(component)
        }

        def name = component.getAttribute(NAME)

        if (name == null || componentName == name.getValue()) {
            setAttributeValue(component, NAME, componentName)
        }

        map["options"].each { Pair option ->
            def optionTag = component.getChildren(map["optionTag"] as String).find {
                Attribute attr = it.getAttribute(NAME)
                return attr != null && option.getLeft() == attr.getValue()
            }

            if (optionTag == null) {
                optionTag = new Element(map["optionTag"] as String)
                component.addContent(optionTag)
                setAttributeValue(optionTag, NAME, option.getLeft() as String)
            }

            setAttributeValue(optionTag, VALUE, option.getRight() as String)
        }
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
            @NotNull String version,
            @NotNull String archiveType) {
        final gradleHomePath = project.getGradle().getGradleUserHomeDir().getAbsolutePath()
        final name = plugin.getProductName().toLowerCase()
        final defaultRelativePath = "caches/modules-2/files-2.1/$plugin.productGroup/$name/$name$type"

        return new File("$gradleHomePath/$defaultRelativePath/$version/$archiveType")
    }

    @NotNull
    static File getArchivePath(@NotNull Project project,
            @NotNull BasePlugin plugin,
            @NotNull ProductPluginExtension extension) {
        def defaultIdePath = getDefaultIdePath(
                project,
                plugin,
                extension.type,
                extension.version,
                extension.archiveType)
        final name = plugin.getProductName().toLowerCase()
        return new File(defaultIdePath.parentFile, "$name$extension.type-$extension.version.$extension.archiveType")
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

    static void Untgz(@NotNull File archive, @NotNull File destination) {
        destination.parentFile.mkdirs()

        Archiver archiver = ArchiverFactory.createArchiver("tar", "gz")
        archiver.extract(archive, destination)

        def target = destination.listFiles()[0]
        File[] content = target.listFiles()
        for (int i = 0; i < content.length; i++) {
            content[i].renameTo(new File(destination, content[i].name))
        }
        target.delete()
    }

    static void Unzip(@NotNull File archive, @NotNull File destination) {
        destination.parentFile.mkdirs()

        Archiver archiver = ArchiverFactory.createArchiver("zip")
        archiver.extract(archive, destination)
    }

    @Nullable
    static File downloadProduct(
            @NotNull BasePlugin basePlugin,
            @NotNull ProductPluginExtension extension,
            @NotNull File archive) {
        URL url
        def repository = extension.getRepository()
        if (!repository) {
            return null
        }

        try {
            url = new URL(repository)
            def dir = archive.parentFile
            dir.mkdirs()

            def bis
            try {
                bis = new BufferedInputStream(url.openStream())
                def fis
                try {
                    fis = new FileOutputStream(archive)
                    byte[] buffer = new byte[1024]
                    int count
                    while ((count = bis.read(buffer, 0, 1024)) != -1) {
                        fis.write(buffer, 0, count)
                    }
                } finally {
                    if (fis) {
                        fis.close()
                    }
                }
            } finally {
                if (bis) {
                    bis.close()
                }
            }
        } catch (IOException ignored) {
            logger.error("Failure download ${basePlugin.productName} from ${repository}", ignored)
            println("Failure download ${basePlugin.productName} from ${repository}")
            ignored.printStackTrace()
            return null
        }
        return archive
    }

    static String getDefaultArchiveType() {
        if (System.properties['os.name'].toLowerCase().contains('windows')) {
            return "zip"
        } else {
            return "tar.gz"
        }
    }

    static void createOrRepairXml(@NotNull File optionsDir, Map map) {
        def updatesConfig = new File(optionsDir, map["filename"] as String)
        try {
            if (!updatesConfig.exists() && !updatesConfig.createNewFile()) {
                return
            }
        } catch (IOException ignore) {
            return
        }

        def doc = getXmlDocument(updatesConfig)

        if (!doc || !doc.hasRootElement()) {
            doc = createXml(map)
        } else {
            repairXml(doc, map)
        }

        try {
            outputXml(doc, updatesConfig)
        } catch (IOException ignored) {
            logger.warn("Failed write to " + updatesConfig)
        }
    }

    static void createOrRepairUpdateXml(@NotNull File file) {
        createOrRepairXml(file, UPDATE_XML)
    }

    static void createOrRepairIdeGeneralXml(@NotNull File file) {
        createOrRepairXml(file, IDE_GENERAL_XML)
    }

    static void createOrRepairOptionsXml(File file) {
        createOrRepairXml(file, OPTIONS_XML)
    }
}
