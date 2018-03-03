package org.stepik.gradle.plugins.common.tasks

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction
import org.jdom2.CDATA
import org.jdom2.Content
import org.jdom2.Document
import org.jdom2.Element
import org.jdom2.Text
import org.stepik.gradle.plugins.common.ProductPluginExtension
import org.stepik.gradle.plugins.common.Utils
import org.stepik.gradle.plugins.common.Utils.sourcePluginXmlFiles
import java.io.File
import java.io.IOException


open class PatchPluginXmlTask : ConventionTask() {

    @get:Input
    private var destinationDirName: String? = "patchedPluginXmlFiles"
        get() {
            val directory = extension?.productName ?: return field
            return "$directory${File.separator}$field"
        }

    var extension: ProductPluginExtension? = null

    @get:[Input Optional]
    val version: String
        get() = project.version.toString()

    @get:OutputDirectory
    private val destinationDir: File?
        get() {
            destinationDirName ?: return null
            return File(project.buildDir, destinationDirName)
        }

    @get:[SkipWhenEmpty InputFiles]
    private val pluginXmlFiles: FileCollection
        get() {
            return sourcePluginXmlFiles(project)
        }

    @get:Input
    private val pluginDescription: String
        get() {
            val filename = extension?.pluginDescription ?: return ""
            val file = File(filename)
            return if (file.exists()) file.readText() else ""
        }

    @get:[Input Optional]
    private val sinceBuild: String?
        get() = extension?.sinceBuild

    @get:[Input Optional]
    private val untilBuild: String?
        get() = extension?.untilBuild

    @get:Input
    private val changeNotes: String
        get() {
            val filename = extension?.changeNotes ?: return ""
            val file = File(filename)
            return if (file.exists()) file.readText() else ""
        }

    @TaskAction
    fun patchPluginXmlFiles() {
        pluginXmlFiles.forEach {
            try {
                val pluginXml = Utils.getXmlDocument(it) ?: return

                patchSinceUntilBuild(pluginXml, sinceBuild, untilBuild)
                patchElement(pluginXml, "description", CDATA(pluginDescription))
                patchElement(pluginXml, "change-notes", CDATA(changeNotes))
                patchElement(pluginXml, "version", version)

                val destinationFile = File(destinationDir, it.name)

                Utils.outputXml(pluginXml, destinationFile)
            } catch (ignore: IOException) {
            }
        }
    }

    private fun patchSinceUntilBuild(pluginXml: Document, sinceBuild: String?, untilBuild: String?) {
        if (extension?.updateSinceUntilBuild != true) {
            return
        }

        val result = pluginXml.rootElement.getChild("idea-version")

        if (result != null) {
            Utils.setAttributeValue(result, "since-build", sinceBuild)
            Utils.setAttributeValue(result, "until-build", untilBuild)
        }
    }

    companion object {

        fun patchElement(pluginXml: Document, name: String, value: String?) {
            patchElement(pluginXml, name, Text(value))
        }

        fun patchElement(pluginXml: Document, name: String, value: Content?) {
            if (value != null) {
                var result = pluginXml.rootElement.getChild(name)

                if (result == null) {
                    result = Element(name)
                    pluginXml.rootElement.addContent(result)
                }

                result.setContent(value)
            }
        }
    }
}
