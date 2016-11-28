package org.stepik.gradle.plugins.pycharm

import com.sun.istack.internal.NotNull
import org.gradle.api.tasks.InputDirectory
import org.gradle.api.tasks.JavaExec
import org.gradle.api.tasks.OutputDirectory
import org.gradle.internal.jvm.Jvm
import org.gradle.internal.os.OperatingSystem
import org.gradle.process.JavaForkOptions

class RunPyCharmTask extends JavaExec {

    private Object idePath
    private Object configDirectory
    private Object systemDirectory
    private Object pluginsDirectory

    @InputDirectory
    File getIdePath() {
        idePath != null ? project.file(idePath) : null
    }

    void setIdePath(Object ideDirectory) {
        this.idePath = ideDirectory
    }

    @OutputDirectory
    File getConfigDirectory() {
        configDirectory != null ? project.file(configDirectory) : null
    }

    void setConfigDirectory(Object configDirectory) {
        this.configDirectory = configDirectory
    }

    @OutputDirectory
    File getSystemDirectory() {
        systemDirectory != null ? project.file(systemDirectory) : null
    }

    void setSystemDirectory(Object systemDirectory) {
        this.systemDirectory = systemDirectory
    }

    @OutputDirectory
    File getPluginsDirectory() {
        pluginsDirectory != null ? project.file(pluginsDirectory) : null
    }

    void setPluginsDirectory(Object pluginsDirectory) {
        this.pluginsDirectory = pluginsDirectory
    }

    RunPyCharmTask() {
        setMain("com.intellij.idea.Main")
        enableAssertions = true
        outputs.upToDateWhen { false }
    }

    @Override
    void exec() {
        workingDir = project.file("${getIdePath()}/bin/")
        configureClasspath()
        configureSystemProperties()
        configureJvmArgs()
        super.exec()
    }

    private void configureClasspath() {
        File pyCharmDirectory = getIdePath()
        def toolsJar = Jvm.current().toolsJar
        if (toolsJar != null) classpath += project.files(toolsJar)
        classpath += project.files(
                "$pyCharmDirectory/lib/bootstrap.jar",
                "$pyCharmDirectory/lib/extensions.jar",
                "$pyCharmDirectory/lib/util.jar",
                "$pyCharmDirectory/lib/trove4j.jar",
                "$pyCharmDirectory/lib/jdom.jar",
                "$pyCharmDirectory/lib/log4j.jar",
                "$pyCharmDirectory/lib/jna.jar")
    }

    def configureSystemProperties() {
        systemProperties(getPyCharmSystemProperties(getConfigDirectory(), getSystemDirectory(), getPluginsDirectory()))
        def operatingSystem = OperatingSystem.current()
        if (operatingSystem.isMacOsX()) {
            systemProperty("idea.smooth.progress", false)
            systemProperty("apple.laf.useScreenMenuBar", true)
        } else if (operatingSystem.isUnix() && !getSystemProperties().containsKey("sun.awt.disablegrab")) {
            systemProperty("sun.awt.disablegrab", true)
        }
        systemProperty("idea.classpath.index.enabled", false)
        systemProperty("idea.is.internal", true)

        if (!getSystemProperties().containsKey('idea.platform.prefix')) {
            systemProperty('idea.platform.prefix', 'PyCharmCore')
        }
    }

    @NotNull
    private static Map<String, Object> getPyCharmSystemProperties(@NotNull File configDirectory,
                                                                  @NotNull File systemDirectory,
                                                                  @NotNull File pluginsDirectory) {
        def result = ["idea.config.path" : configDirectory.absolutePath,
                      "idea.system.path" : systemDirectory.absolutePath,
                      "idea.plugins.path": pluginsDirectory.absolutePath]
        result
    }

    def configureJvmArgs() {
        jvmArgs = getPyCharmJvmArgs(this, getJvmArgs(), getIdePath())
    }

    @NotNull
    private static List<String> getPyCharmJvmArgs(@NotNull JavaForkOptions options,
                                                  @NotNull List<String> originalArguments,
                                                  @NotNull File pyCharmDirectory) {
        if (options.maxHeapSize == null) options.maxHeapSize = "512m"
        if (options.minHeapSize == null) options.minHeapSize = "256m"
        boolean hasPermSizeArg = false
        def result = []
        for (String arg : originalArguments) {
            if (arg.startsWith("-XX:MaxPermSize")) {
                hasPermSizeArg = true
            }
            result += arg
        }

        result += "-Xbootclasspath/a:${pyCharmDirectory.absolutePath}/lib/boot.jar"
        if (!hasPermSizeArg) result += "-XX:MaxPermSize=250m"
        return result
    }
}
