package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.Project
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.internal.jvm.Jvm
import org.gradle.internal.os.OperatingSystem
import org.gradle.process.JavaForkOptions
import org.jetbrains.annotations.NotNull

/**
 * @author meanmail
 */
abstract class BaseRunTask extends JavaExec {
    private ProductPluginExtension extension
    private BasePlugin plugin

    BaseRunTask() {
        main = "com.intellij.idea.Main"
        enableAssertions = true
        outputs.upToDateWhen({ false })
    }

    @NotNull
    private static Map<String, String> getProductSystemProperties(
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
    private List<String> getProductJvmArgs(
            @NotNull JavaForkOptions options,
            @NotNull List<String> originalArguments) {
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

    @SuppressWarnings("WeakerAccess")
    @InputDirectory
    File getIdePath() {
        if (extension == null) {
            return null
        }

        def path = extension.idePath

        return project.file(path)
    }

    @SuppressWarnings("WeakerAccess")
    @OutputDirectory
    File getConfigDirectory() {
        return findPrepareSandboxTask(project).configDirectory
    }

    private PrepareSandboxTask findPrepareSandboxTask(@NotNull Project project) {
        return Utils.findTask(project, plugin.prepareSandboxTaskName) as PrepareSandboxTask
    }

    @SuppressWarnings("WeakerAccess")
    @OutputDirectory
    File getSystemDirectory() {
        return project.file("$extension.sandboxDirectory/system")
    }

    @SuppressWarnings("WeakerAccess")
    @OutputDirectory
    File getPluginsDirectory() {
        return project.file("$extension.sandboxDirectory/plugins")
    }

    @Override
    void exec() {
        workingDir = project.file("$idePath/bin/")
        configureClasspath()
        configureSystemProperties()
        configureJvmArgs()
        super.exec()
    }

    @Internal
    protected abstract String[] getLibs()

    private void configureClasspath() {
        def ideaDirectory = idePath
        def toolsJar = Jvm.current().toolsJar
        if (toolsJar) {
            classpath = classpath + project.files(toolsJar)
        }

        libs.each {
            classpath = classpath + project.files("$ideaDirectory/lib/$it")
        }
    }

    @Internal
    protected abstract String getPlatformPrefix()

    private void configureSystemProperties() {
        systemProperties(extension.systemProperties)
        systemProperties(getProductSystemProperties(configDirectory, systemDirectory, pluginsDirectory))
        def operatingSystem = OperatingSystem.current()
        if (operatingSystem.isMacOsX()) {
            systemProperty("idea.smooth.progress", false)
            systemProperty("apple.laf.useScreenMenuBar", true)
        } else if (operatingSystem.isUnix() && !getSystemProperties().containsKey("sun.awt.disablegrab")) {
            systemProperty("sun.awt.disablegrab", true)
        }

        systemProperty("idea.classpath.index.enabled", false)
        systemProperty("idea.is.internal", true)

        if (!systemProperties.containsKey("idea.platform.prefix")) {
            systemProperty("idea.platform.prefix", platformPrefix)
        }
    }

    private void configureJvmArgs() {
        jvmArgs = getProductJvmArgs(this, jvmArgs)
    }

    void setExtension(ProductPluginExtension extension) {
        this.extension = extension
    }

    void setPlugin(BasePlugin plugin) {
        this.plugin = plugin
    }
}
