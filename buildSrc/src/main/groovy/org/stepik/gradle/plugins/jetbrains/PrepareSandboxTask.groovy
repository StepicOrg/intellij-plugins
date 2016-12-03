package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.FileUtils
import org.gradle.jvm.tasks.Jar
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable

import java.nio.file.Files
import java.nio.file.StandardCopyOption

/**
 * @author meanmail
 */
class PrepareSandboxTask extends DefaultTask {
    private static final Logger LOG = Logging.getLogger(PrepareSandboxTask.class)

    private ProductPluginExtension extension

    @Nullable
    private static File getArchivePath(@NotNull Project project) {
        Jar jarTask = Utils.findTask(project, JavaPlugin.JAR_TASK_NAME) as Jar
        if (!jarTask) {
            return null
        }
        return jarTask.archivePath
    }

    @InputFile
    @Nullable
    File getPluginJar() {
        File pluginJar = getArchivePath(project)
        return pluginJar != null ? pluginJar : null
    }

    @Input
    @Nullable
    String getPluginName() {
        if (extension == null) {
            return null
        }
        def pluginName = extension.getPluginName()
        return pluginName != null ? FileUtils.toSafeFileName(pluginName) : null
    }

    @Input
    @Nullable
    File getConfigDirectory() {
        if (extension == null) {
            return null
        }
        def configDirectory = new File(extension.sandboxDirectory, "config")
        return project.file(configDirectory)
    }

    @OutputDirectory
    @Nullable
    File getDestinationDir() {
        if (extension == null) {
            return null
        }
        return project.file(new File(extension.sandboxDirectory, "plugins"))
    }

    PrepareSandboxTask() {
    }

    @TaskAction
    protected void copy() {
        if (pluginJar == null) {
            LOG.error("Failed prepare sandbox task: plugin jar is null")
            return
        }

        if (destinationDir == null) {
            LOG.error("Failed prepare sandbox task: plugins directory is null")
            return
        }

        def source = pluginJar.toPath()
        def pluginPath = destinationDir.toPath().resolve(pluginName)
        def target = pluginPath.resolve("lib/" + source.getFileName())
        try {
            Utils.deleteDirectory(pluginPath)
            Files.createDirectories(target.getParent())
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING)
        } catch (IOException ignored) {
            LOG.error("Failed prepare sandbox task: copy from " + source + " to " + target)
            return
        }
        disableIdeUpdate()
    }

    private void disableIdeUpdate() {
        def optionsDir = new File(configDirectory, "options")
        if (!optionsDir.exists() && !optionsDir.mkdirs()) {
            return
        }

        def updatesConfig = new File(optionsDir, "updates.xml")
        try {
            if (!updatesConfig.exists() && !updatesConfig.createNewFile()) {
                return
            }
        } catch (IOException ignore) {
            return
        }

        def doc = Utils.getXmlDocument(updatesConfig)

        if (!doc || !doc.hasRootElement()) {
            doc = Utils.createUpdatesXml()
        } else {
            Utils.repairUpdateXml(doc)
        }

        try {
            Utils.outputXml(doc, updatesConfig)
        } catch (IOException ignored) {
            LOG.warn("Failed write to " + updatesConfig)
        }
    }

    void setExtension(@NotNull ProductPluginExtension extension) {
        this.extension = extension
    }
}
