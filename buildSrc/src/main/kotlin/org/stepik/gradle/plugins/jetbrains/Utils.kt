package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.tasks.SourceSet
import org.gradle.process.JavaForkOptions
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.input.SAXBuilder
import org.jdom2.output.Format
import org.jdom2.output.XMLOutputter
import org.rauschig.jarchivelib.ArchiverFactory
import org.slf4j.LoggerFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.net.URL
import java.util.*


object Utils {
    private val logger = LoggerFactory.getLogger(Utils::class.java)
    const val APPLICATION = "application"
    const val COMPONENT = "component"
    const val COMPONENT_NAME = "componentName"
    const val NAME = "name"
    const val OPTIONS = "options"
    const val OPTION_TAG = "optionTag"
    const val VALUE = "value"

    class IdeXml(
            val filename: String,
            val componentName: String,
            val optionTag: String,
            val options: Map<String, String>
    )

    val UPDATE_XML = IdeXml(
            "updates.xml",
            "UpdatesConfigurable",
            "option",
            mapOf(
                    "CHECK_NEEDED" to "false"
            )
    )

    val IDE_GENERAL_XML = IdeXml(
            "ide.general.xml",
            "GeneralSettings",
            "option",
            mapOf(
                    "confirmExit" to "false",
                    "showTipsOnStartup" to "false"
            )
    )
    val OPTIONS_XML = IdeXml(
            "options.xml",
            "PropertiesComponent",
            "property",
            mapOf(
                    "toolwindow.stripes.buttons.info.shown" to "true"
            )
    )

    fun getXmlDocument(file: File): Document? {
        return try {
            SAXBuilder().build(file)
        } catch (ignored: Exception) {
            null
        }
    }

    fun mainSourceSet(project: Project): SourceSet {
        val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
        return javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)
    }

    fun testSourceSet(project: Project): SourceSet {
        val javaConvention = project.convention.getPlugin(JavaPluginConvention::class.java)
        return javaConvention.sourceSets.getByName(SourceSet.TEST_SOURCE_SET_NAME)
    }

    fun sourcePluginXmlFiles(project: Project): FileCollection {
        val result = HashSet<File>()
        mainSourceSet(project).resources.srcDirs.forEach {
            val pluginXml = File(it, "META-INF/plugin.xml")
            if (pluginXml.exists()) {
                if (isPluginXmlFile(pluginXml)) {
                    result.add(pluginXml)
                }
            }
        }

        return project.files(result)
    }

    private fun isPluginXmlFile(file: File): Boolean {
        val doc = getXmlDocument(file)
        if (doc?.hasRootElement() != true) {
            return false
        }
        return doc.rootElement.name == "idea-plugin"
    }

    fun findTask(project: Project, taskName: String): Task? {
        return project.tasks.findByName(taskName)
    }

    fun outputXml(doc: Document, file: File) {
        val outputter = XMLOutputter()
        val format = Format.getPrettyFormat()
        format.indent = "    "
        outputter.format = format
        outputter.output(doc, FileOutputStream(file))
    }

    fun setAttributeValue(node: Element, name: String, value: String?) {
        val attribute = node.getAttribute(name)

        if (value == null) {
            if (attribute != null) {
                node.removeAttribute(attribute)
            }
            return
        }

        node.setAttribute(name, value)
    }

    fun createXml(xml: IdeXml): Document {
        val doc = Document()

        val applicationNode = Element(APPLICATION)
        doc.rootElement = applicationNode

        val component = Element(COMPONENT)
        applicationNode.addContent(component)

        setAttributeValue(component, NAME, xml.componentName)

        xml.options.forEach { option ->
            val optionTag = Element(xml.optionTag)
            component.addContent(optionTag)
            setAttributeValue(optionTag, NAME, option.key)
            setAttributeValue(optionTag, VALUE, option.value)
        }

        return doc
    }

    fun repairXml(doc: Document, xml: IdeXml) {
        if (!doc.hasRootElement()) {
            doc.rootElement = Element(APPLICATION)
        }

        val applicationNode = doc.rootElement

        if (APPLICATION != applicationNode.name) {
            applicationNode.name = APPLICATION
        }

        var component = applicationNode.getChildren(COMPONENT).find {
            val attr = it.getAttribute(NAME)
            return@find attr != null && xml.componentName == attr.value
        }

        if (component == null) {
            component = Element(COMPONENT)
            applicationNode.addContent(component)
        }

        val name = component.getAttribute(NAME)

        if (name == null || xml.componentName == name.value) {
            setAttributeValue(component, NAME, xml.componentName)
        }

        xml.options.forEach { option ->
            var optionTag = component.getChildren(xml.optionTag).find {
                val attr = it.getAttribute(NAME)
                return@find attr != null && option.key == attr.value
            }

            if (optionTag == null) {
                optionTag = Element(xml.optionTag)
                component.addContent(optionTag)
                setAttributeValue(optionTag, NAME, option.key)
            }

            setAttributeValue(optionTag, VALUE, option.value)
        }
    }


    fun getDefaultIdePath(project: Project, plugin: BasePlugin, type: String,
                          version: String, archiveType: String): String {
        val gradleHomePath = project.gradle.gradleUserHomeDir.absolutePath
        val name = plugin.productName.toLowerCase()
        val defaultRelativePath = "caches/modules-2/files-2.1/${plugin.productGroup}/$name/$name$type"

        return "$gradleHomePath/$defaultRelativePath/$version/$archiveType"
    }

    fun getArchivePath(project: Project, plugin: BasePlugin, extension: ProductPluginExtension): File {
        val defaultIdePath = File(getDefaultIdePath(project, plugin, extension.productType,
                extension.version, extension.archiveType))
        val name = plugin.productName.toLowerCase()
        return File(defaultIdePath.parentFile, "$name${extension.productType}-${extension.version}.${extension.archiveType}")
    }

    fun getProductSystemProperties(configDirectory: File, systemDirectory: File,
                                   pluginsDirectory: File): Map<String, String> {
        return mapOf(
                "idea.config.path" to configDirectory.absolutePath,
                "idea.system.path" to systemDirectory.absolutePath,
                "idea.plugins.path" to pluginsDirectory.absolutePath
        )
    }

    fun getProductJvmArgs(options: JavaForkOptions, originalArguments: List<String>,
                          idePath: File): List<String> {
        if (options.maxHeapSize == null) {
            options.maxHeapSize = "512m"
        }
        if (options.minHeapSize == null) {
            options.minHeapSize = "256m"
        }
        val result = mutableListOf<String>()
        result.addAll(originalArguments)

        result += "-Xbootclasspath/a:${idePath.absolutePath}/lib/boot.jar"
        return result
    }

    fun untgz(archive: File, destination: File) {
        destination.parentFile.mkdirs()

        val archiver = ArchiverFactory.createArchiver("tar", "gz")
        archiver.extract(archive, destination)

        val target = destination.listFiles()[0]
        val content = target.listFiles()
        for (i in 0 until content.size) {
            content[i].renameTo(File(destination, content[i].name))
        }
        target.delete()
    }

    fun unzip(archive: File, destination: File) {
        destination.parentFile.mkdirs()

        val archiver = ArchiverFactory.createArchiver("zip")
        archiver.extract(archive, destination)
    }

    fun downloadProduct(
            basePlugin: BasePlugin,
            extension: ProductPluginExtension,
            archive: File): File? {
        val repository = extension.repository

        try {
            val dir = archive.parentFile
            dir.mkdirs()

            URL(repository).openStream().buffered().use { bis ->
                FileOutputStream(archive).use {
                    bis.copyTo(it)
                }
            }
        } catch (e: IOException) {
            logger.error("Failure download ${basePlugin.productName} from $repository", e)
            println("Failure download ${basePlugin.productName} from $repository")
            return null
        }
        return archive
    }

    fun getDefaultArchiveType(): String {
        val osName = System.getProperty("os.name").toLowerCase()
        return if (osName.contains("windows")) "zip" else "tar.gz"
    }

    private fun createOrRepairXml(optionsDir: File, map: IdeXml) {
        val updatesConfig = File(optionsDir, map.filename)
        try {
            if (!updatesConfig.exists() && !updatesConfig.createNewFile()) {
                return
            }
        } catch (ignore: IOException) {
            return
        }

        var doc = getXmlDocument(updatesConfig)

        if (doc?.hasRootElement() != true) {
            doc = createXml(map)
        } else {
            repairXml(doc, map)
        }

        try {
            outputXml(doc, updatesConfig)
        } catch (e: IOException) {
            logger.warn("Failed write to $updatesConfig")
        }
    }

    fun createOrRepairUpdateXml(file: File) {
        createOrRepairXml(file, UPDATE_XML)
    }

    fun createOrRepairIdeGeneralXml(file: File) {
        createOrRepairXml(file, IDE_GENERAL_XML)
    }

    fun createOrRepairOptionsXml(file: File) {
        createOrRepairXml(file, OPTIONS_XML)
    }

}
