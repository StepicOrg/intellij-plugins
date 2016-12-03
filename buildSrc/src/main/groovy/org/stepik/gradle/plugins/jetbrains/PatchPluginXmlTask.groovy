package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.*
import org.jdom2.Document
import org.jdom2.Element
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

/**
 * @author meanmail
 */
class PatchPluginXmlTask extends ConventionTask {
    private String destinationDirName
    private ProductPluginExtension extension

    @OutputDirectory
    File getDestinationDir() {
        return destinationDirName != null ? new File(project.buildDir, destinationDirName) : null
    }

    @SkipWhenEmpty
    @InputFiles
    FileCollection getPluginXmlFiles() {
        return Utils.sourcePluginXmlFiles(project)
    }

    @Input
    @Optional
    String getVersion() {
        return project.version.toString()
    }

    @Input
    @Optional
    @Nullable
    String getPluginDescription() {
        return extension != null ? extension.pluginDescription : null
    }

    @Input
    @Optional
    @Nullable
    String getSinceBuild() {
        if (!extension) {
            return null
        }

        return extension.sinceBuild
    }

    @Input
    @Optional
    @Nullable
    String getUntilBuild() {
        if (!extension) {
            return null
        }

        return extension.untilBuild
    }

    @Input
    @Optional
    String getChangeNotes() {
        return extension != null ? extension.changeNotes : null
    }

    @TaskAction
    void patchPluginXmlFiles() {
        def files = pluginXmlFiles
        if (files == null) {
            return
        }

        files.each {
            try {
                def pluginXml = Utils.getXmlDocument(it)
                if (pluginXml == null) {
                    return
                }

                patchSinceUntilBuild(pluginXml, sinceBuild, untilBuild)
                patchElement(pluginXml, "description", pluginDescription)
                patchElement(pluginXml, "change-notes", changeNotes)
                patchElement(pluginXml, "version", version)

                def destinationFile = new File(destinationDir, it.name)

                Utils.outputXml(pluginXml, destinationFile)
            } catch (IOException ignore) {
            }
        }
    }

    void patchSinceUntilBuild(
            @NotNull Document pluginXml,
            @Nullable String sinceBuild,
            @Nullable String untilBuild) {
        if (!extension.updateSinceUntilBuild) {
            return
        }

        def result = pluginXml.getRootElement().getChild("idea-version")

        if (result != null) {
            Utils.setAttributeValue(result, "since-build", sinceBuild)
            Utils.setAttributeValue(result, "until-build", untilBuild)
        }
    }

    static void patchElement(@NotNull Document pluginXml, @NotNull String name, @Nullable String value) {
        if (value != null) {
            def result = pluginXml.getRootElement().getChild(name)

            if (result == null) {
                result = new Element(name)
                pluginXml.getRootElement().addContent(result)
            }

            result.text = value
        }
    }

    void setDestinationDirName(String destinationDirName) {
        this.destinationDirName = destinationDirName
    }

    void setExtension(ProductPluginExtension extension) {
        this.extension = extension
    }
}
