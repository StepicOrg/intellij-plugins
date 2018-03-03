package org.stepik.gradle.plugins.common.tasks

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.ProjectDependency
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.JavaPlugin.JAR_TASK_NAME
import org.gradle.api.plugins.JavaPlugin.RUNTIME_CONFIGURATION_NAME
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction
import org.gradle.internal.jvm.Jvm
import org.gradle.jvm.tasks.Jar
import org.stepik.gradle.plugins.common.ProductPluginExtension
import org.stepik.gradle.plugins.common.Utils.createOrRepairIdeGeneralXml
import org.stepik.gradle.plugins.common.Utils.createOrRepairOptionsXml
import org.stepik.gradle.plugins.common.Utils.createOrRepairUpdateXml
import org.stepik.gradle.plugins.common.Utils.findTask
import org.stepik.gradle.plugins.common.dependency.DependencyManager
import java.io.File
import java.io.IOException


open class PrepareSandboxTask : DefaultTask() {

    private val log = Logging.getLogger(PrepareSandboxTask::class.java)

    var extension: ProductPluginExtension? = null

    @get:InputFile
    private val pluginJar: File?
        get() {
            val jarTask = findTask(project, JAR_TASK_NAME) as Jar?
            return jarTask?.archivePath
        }

    @get:Input
    private val pluginName: String = project.name

    @get:OutputDirectory
    val configDirectory: File?
        get() {
            val extension = extension ?: return null
            val configDirectory = File(extension.sandboxDirectory, "config")
            return project.file(configDirectory)
        }

    @get:OutputDirectory
    val destinationDir: File?
        get() {
            val extension = extension ?: return null
            return project.file(File(extension.sandboxDirectory, "plugins"))
        }

    @TaskAction
    fun copy() {
        val pluginJar = pluginJar
        if (pluginJar == null) {
            log.error("Failed prepare sandbox task: plugin jar is null")
            return
        }

        val destinationDir = destinationDir
        if (destinationDir == null) {
            log.error("Failed prepare sandbox task: plugins directory is null")
            return
        }
        val dependenciesJars = getDependenciesJars(project).toMutableSet()
        dependenciesJars.add(pluginJar)
        val pluginPath = destinationDir.resolve(pluginName)
        val libPath = pluginPath.resolve("lib")
        try {
            pluginPath.deleteRecursively()
            libPath.mkdirs()
            dependenciesJars.forEach {
                val target = libPath.resolve(it.name)
                it.copyTo(target, true)
            }
        } catch (e: IOException) {
            log.error("Failed prepare sandbox task: copy from $dependenciesJars to $libPath")
            return
        }
        disableIdeUpdate()
    }

    private fun getDependenciesJars(project: Project): Set<File> {
        val runtimeConfiguration = project.configurations.getByName(RUNTIME_CONFIGURATION_NAME)

        val libsToIgnored = mutableListOf(Jvm.current().toolsJar)

        DependencyManager.dependencies.forEach {
            libsToIgnored.addAll(it.jarFiles)
        }

        val result = mutableSetOf<File>()
        runtimeConfiguration.allDependencies.forEach {
            if (it is ProjectDependency) {
                val dependencyProject = it.dependencyProject
                val jarTask = findTask(dependencyProject, JAR_TASK_NAME) as Jar

                if (result.add(jarTask.archivePath)) {
                    val dependenciesJars = getDependenciesJars(dependencyProject)
                    result.addAll(dependenciesJars)
                }
            }
            // FIXME: Remove a condition with 'slf4j' when the stepik-java-api will extracted into a other project
            runtimeConfiguration.fileCollection(it)
                    .filter {
                        !it.name.startsWith("slf4j-") && it !in libsToIgnored
                    }
                    .forEach {
                        result.add(it.absoluteFile)
                    }
        }

        return result
    }

    private fun disableIdeUpdate() {
        val optionsDir = File(configDirectory, "options")
        if (!optionsDir.exists() && !optionsDir.mkdirs()) {
            return
        }

        createOrRepairUpdateXml(optionsDir)
        createOrRepairIdeGeneralXml(optionsDir)
        createOrRepairOptionsXml(optionsDir)
    }

}
