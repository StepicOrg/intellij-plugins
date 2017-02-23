package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.FileUtils
import org.gradle.internal.jvm.Jvm
import org.gradle.jvm.tasks.Jar
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.stepik.gradle.plugins.jetbrains.dependency.DependencyManager

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption

/**
 * @author meanmail
 */
class PrepareSandboxTask extends DefaultTask {
    private static final Logger logger = Logging.getLogger(PrepareSandboxTask.class)

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
            logger.error("Failed prepare sandbox task: plugin jar is null")
            return
        }

        if (destinationDir == null) {
            logger.error("Failed prepare sandbox task: plugins directory is null")
            return
        }
        def dependenciesJars = getDependenciesJars(project)
        dependenciesJars.add(pluginJar.toPath())
        def pluginPath = destinationDir.toPath().resolve(pluginName)
        def libPath = pluginPath.resolve("lib")
        try {
            Utils.deleteDirectory(pluginPath)
            Files.createDirectories(libPath)
            dependenciesJars.each {
                def target = libPath.resolve(it.getFileName())
                Files.copy(it, target, StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (IOException ignored) {
            logger.error("Failed prepare sandbox task: copy from " + dependenciesJars + " to " + libPath)
            return
        }
        disableIdeUpdate()
    }

    private static HashSet<Path> getDependenciesJars(@NotNull Project project) {
        def runtimeConfiguration = project.configurations.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME)

        def libsToIgnored = [Jvm.current().toolsJar]

        DependencyManager.dependencies.forEach {
            libsToIgnored.addAll(it.jarFiles)
        }

        def result = new HashSet<>()
        runtimeConfiguration.getAllDependencies().each {
            if (it instanceof ProjectDependency) {
                Project dependencyProject = it.dependencyProject
                Jar jarTask = Utils.findTask(dependencyProject, JavaPlugin.JAR_TASK_NAME) as Jar

                if (result.add(jarTask.archivePath.toPath())) {
                    def dependenciesJars = getDependenciesJars(dependencyProject)
                    result.addAll(dependenciesJars)
                }
            }
            // FIXME: Remove a condition with 'slf4j' when the stepik-java-api will extracted into a other project
            runtimeConfiguration.fileCollection(it)
                    .filter {
                !it.name.startsWith('slf4j-') && !libsToIgnored.contains(it)
            }
            .forEach {
                result.add(it.absoluteFile.toPath())
            }
        }

        result
    }

    private void disableIdeUpdate() {
        def optionsDir = new File(configDirectory, "options")
        if (!optionsDir.exists() && !optionsDir.mkdirs()) {
            return
        }

        Utils.createOrRepairUpdateXml(optionsDir)
        Utils.createOrRepairIdeGeneralXml(optionsDir)
        Utils.createOrRepairOptionsXml(optionsDir)
    }

    void setExtension(@NotNull ProductPluginExtension extension) {
        this.extension = extension
    }
}
