package org.stepik.gradle.plugins.jetbrains;

import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.gradle.api.Project;
import org.gradle.api.file.FileCollection;
import org.gradle.api.tasks.InputDirectory;
import org.gradle.api.tasks.Internal;
import org.gradle.api.tasks.JavaExec;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.internal.jvm.Jvm;
import org.gradle.internal.os.OperatingSystem;
import org.gradle.process.JavaForkOptions;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @author meanmail
 */
public abstract class BaseRunTask extends JavaExec {
    private ProductPluginExtension extension;
    private BasePlugin plugin;

    @SuppressWarnings({"WeakerAccess", "unused"})
    public BaseRunTask() {
        setMain("com.intellij.idea.Main");
        setEnableAssertions(true);
        getOutputs().upToDateWhen(task -> false);
    }

    @NotNull
    private static Map<String, String> getProductSystemProperties(
            @NotNull File configDirectory,
            @NotNull File systemDirectory,
            @NotNull File pluginsDirectory) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>(3);
        map.put("idea.config.path", configDirectory.getAbsolutePath());
        map.put("idea.system.path", systemDirectory.getAbsolutePath());
        map.put("idea.plugins.path", pluginsDirectory.getAbsolutePath());

        return map;
    }

    @NotNull
    private static List<String> getProductJvmArgs(
            @NotNull JavaForkOptions options,
            @NotNull List<String> originalArguments,
            @NotNull final File pyCharmDirectory) {
        if (options.getMaxHeapSize() == null) options.setMaxHeapSize("512m");
        if (options.getMinHeapSize() == null) options.setMinHeapSize("256m");
        boolean hasPermSizeArg = false;
        List<String> result = new ArrayList<>();
        for (String arg : originalArguments) {
            if (arg.startsWith("-XX:MaxPermSize")) {
                hasPermSizeArg = true;
            }

            result = DefaultGroovyMethods.plus(result, arg);
        }

        result = DefaultGroovyMethods.plus(result,
                "-Xbootclasspath/a:" + pyCharmDirectory.getAbsolutePath() + "/lib/boot.jar");
        if (!hasPermSizeArg) result = DefaultGroovyMethods.plus(result, "-XX:MaxPermSize=250m");

        return result;
    }

    @SuppressWarnings("WeakerAccess")
    @InputDirectory
    public File getIdePath() {
        if (extension == null) {
            return null;
        }

        File path = extension.getIdePath();

        return getProject().file(path);
    }

    @SuppressWarnings("WeakerAccess")
    @OutputDirectory
    public File getConfigDirectory() {
        return findPrepareSandboxTask(getProject()).getConfigDirectory();
    }

    private PrepareSandboxTask findPrepareSandboxTask(@NotNull Project project) {
        return (PrepareSandboxTask) Utils.findTask(project, plugin.getPrepareSandboxTaskName());
    }

    @SuppressWarnings("WeakerAccess")
    @OutputDirectory
    public File getSystemDirectory() {
        return getProject().file(extension.getSandboxDirectory() + "/system");
    }

    @SuppressWarnings("WeakerAccess")
    @OutputDirectory
    public File getPluginsDirectory() {
        return getProject().file(extension.getSandboxDirectory() + "/plugins");
    }

    @Override
    public void exec() {
        setWorkingDir(getProject().file(getIdePath() + "/bin/"));
        configureClasspath();
        configureSystemProperties();
        configureJvmArgs();
        super.exec();
    }

    @Internal
    protected abstract String[] getLibs();

    private void configureClasspath() {
        final File ideaDirectory = getIdePath();
        File toolsJar = Jvm.current().getToolsJar();
        if (toolsJar != null) setClasspath(getClasspath().plus(getProject().files(toolsJar)));
        if (toolsJar != null) setClasspath(getClasspath().plus(getProject().files(toolsJar)));
        String[] libs = getLibs();
        FileCollection classpath = getClasspath();
        Project project = getProject();

        Arrays.stream(libs).forEach(lib -> classpath.add(project.files(String.valueOf(ideaDirectory) + "/lib/" + lib)));
    }

    @Internal
    protected abstract String getPlatformPrefix();

    private void configureSystemProperties() {
        systemProperties(extension.getSystemProperties());
        systemProperties(getProductSystemProperties(getConfigDirectory(), getSystemDirectory(), getPluginsDirectory()));
        OperatingSystem operatingSystem = OperatingSystem.current();
        if (operatingSystem.isMacOsX()) {
            systemProperty("idea.smooth.progress", false);
            systemProperty("apple.laf.useScreenMenuBar", true);
        } else if (operatingSystem.isUnix() && !getSystemProperties().containsKey("sun.awt.disablegrab")) {
            systemProperty("sun.awt.disablegrab", true);
        }

        systemProperty("idea.classpath.index.enabled", false);
        systemProperty("idea.is.internal", true);

        if (!getSystemProperties().containsKey("idea.platform.prefix")) {
            systemProperty("idea.platform.prefix", getPlatformPrefix());
        }
    }

    private void configureJvmArgs() {
        setJvmArgs(getProductJvmArgs(this, getJvmArgs(), getIdePath()));
    }

    void setExtension(ProductPluginExtension extension) {
        this.extension = extension;
    }

    void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }
}
