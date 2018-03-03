package org.stepik.gradle.plugins.jetbrains.tasks

import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.internal.jvm.Jvm
import org.gradle.internal.os.OperatingSystem
import org.stepik.gradle.plugins.jetbrains.BasePlugin
import org.stepik.gradle.plugins.jetbrains.ProductPluginExtension
import org.stepik.gradle.plugins.jetbrains.Utils.findTask
import org.stepik.gradle.plugins.jetbrains.Utils.getProductJvmArgs
import org.stepik.gradle.plugins.jetbrains.Utils.getProductSystemProperties
import java.io.File


abstract class BaseRunTask : JavaExec() {
    @get:Internal
    var extension: ProductPluginExtension? = null

    var plugin: BasePlugin? = null

    init {
        main = "com.intellij.idea.Main"
        enableAssertions = true
        outputs.upToDateWhen { false }
    }


    @get:InputDirectory
    val idePath: File?
        get() {
            val path = extension?.idePath ?: return null

            return project.file(path)
        }

    @get:OutputDirectory
    val configDirectory: File?
        get() {
            return findPrepareSandboxTask(project)?.configDirectory
        }

    private fun findPrepareSandboxTask(project: Project): PrepareSandboxTask? {
        val plugin = plugin ?: return null
        return findTask(project, plugin.prepareSandboxTaskName) as PrepareSandboxTask
    }

    @get:OutputDirectory
    val systemDirectory: File?
        get() {
            val extension = extension ?: return null
            return project.file("${extension.sandboxDirectory}/system")
        }

    @get:OutputDirectory
    val pluginsDirectory: File?
        get () {
            val extension = extension ?: return null
            return project.file("${extension.sandboxDirectory}/plugins")
        }

    override fun exec() {
        workingDir = project.file("$idePath/bin/")
        configureClasspath()
        configureSystemProperties()
        configureJvmArgs()
        super.exec()
    }

    @Internal
    protected abstract fun getLibs(): Array<String>

    private fun configureClasspath() {
        val ideaDirectory = idePath
        val toolsJar = Jvm.current().toolsJar
        if (toolsJar != null) {
            classpath += project.files(toolsJar)
        }

        getLibs().forEach {
            classpath += project.files("$ideaDirectory/lib/$it")
        }
    }

    @get:Input
    protected abstract val platformPrefix: String?

    private fun configureSystemProperties() {
        val extSystemProperties = extension?.systemProperties
        if (extSystemProperties != null) {
            systemProperties(extSystemProperties)
        }

        val configDirectory = configDirectory
        val systemDirectory = systemDirectory
        val pluginsDirectory = pluginsDirectory

        if (configDirectory != null && systemDirectory != null && pluginsDirectory != null) {
            systemProperties(getProductSystemProperties(configDirectory, systemDirectory, pluginsDirectory))
        }

        val operatingSystem = OperatingSystem.current()
        if (operatingSystem.isMacOsX) {
            systemProperty("idea.smooth.progress", false)
            systemProperty("apple.laf.useScreenMenuBar", true)
        } else if (operatingSystem.isUnix && !systemProperties.containsKey("sun.awt.disablegrab")) {
            systemProperty("sun.awt.disablegrab", true)
        }

        systemProperty("idea.classpath.index.enabled", false)
        systemProperty("idea.is.internal", true)

        val platformPrefix = platformPrefix

        if (platformPrefix != null) {
            if (!systemProperties.containsKey("idea.platform.prefix")) {
                systemProperty("idea.platform.prefix", platformPrefix)
            }
        }
    }

    private fun configureJvmArgs() {
        val idePath = idePath
        if (idePath != null) {
            jvmArgs = getProductJvmArgs(this, jvmArgs, idePath)
        }
    }
}
