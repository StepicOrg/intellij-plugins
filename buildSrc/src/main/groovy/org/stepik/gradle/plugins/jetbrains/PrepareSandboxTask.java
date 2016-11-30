package org.stepik.gradle.plugins.jetbrains;

import org.gradle.api.DefaultTask;
import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFile;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.TaskAction;
import org.gradle.internal.FileUtils;
import org.gradle.jvm.tasks.Jar;
import org.jdom2.Document;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;

public class PrepareSandboxTask extends DefaultTask {
    private static final Logger LOG = Logging.getLogger(PrepareSandboxTask.class);

    private ProductPluginExtension extension;

    @Nullable
    private File getArchivePath(@NotNull Project project) {
        Jar jarTask = ((Jar) Utils.findTask(project, JavaPlugin.JAR_TASK_NAME));
        if (jarTask == null) {
            return null;
        }
        return jarTask.getArchivePath();
    }

    @SuppressWarnings("WeakerAccess")
    @InputFile
    @Nullable
    public File getPluginJar() {
        File pluginJar = getArchivePath(getProject());
        return pluginJar != null ? pluginJar : null;
    }

    @SuppressWarnings("WeakerAccess")
    @Input
    @Nullable
    public String getPluginName() {
        if (extension == null) {
            return null;
        }
        String pluginName = extension.getPluginName();
        return pluginName != null ? FileUtils.toSafeFileName(pluginName) : null;
    }

    @Input
    @Nullable
    public File getConfigDirectory() {
        if (extension == null) {
            return null;
        }
        File configDirectory = new File(extension.getSandboxDirectory(), "config");
        return getProject().file(configDirectory);
    }

    @SuppressWarnings("WeakerAccess")
    @OutputDirectory
    @Nullable
    public File getDestinationDir() {
        if (extension == null) {
            return null;
        }
        return getProject().file(new File(extension.getSandboxDirectory(), "plugins"));
    }

    public PrepareSandboxTask() {
    }

    @TaskAction
    protected void copy() {
        File pluginJar = getPluginJar();
        if (pluginJar == null) {
            LOG.error("Failed prepare sandbox task: plugin jar is null");
            return;
        }

        File destinationDir = getDestinationDir();
        if (destinationDir == null) {
            LOG.error("Failed prepare sandbox task: plugins directory is null");
            return;
        }

        Path source = pluginJar.toPath();
        Path pluginPath = destinationDir.toPath().resolve(getPluginName());
        Path target = pluginPath.resolve("lib/" + source.getFileName());
        try {
            deleteDirectory(pluginPath);
            Files.createDirectories(target.getParent());
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            LOG.error("Failed prepare sandbox task: copy from " + source + " to " + target);
            return;
        }
        disableIdeUpdate();
    }

    private void deleteDirectory(Path pluginPath) throws IOException {
        if (!Files.exists(pluginPath)) {
            return;
        }

        Files.walkFileTree(pluginPath, new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                try {
                    Files.delete(dir);
                } catch (DirectoryNotEmptyException ignore) {
                }
                return FileVisitResult.CONTINUE;
            }
        });

        try {
            Files.deleteIfExists(pluginPath);
        } catch (DirectoryNotEmptyException ignore) {
        }
    }

    private void disableIdeUpdate() {
        File optionsDir = new File(getConfigDirectory(), "options");
        if (!optionsDir.exists() && !optionsDir.mkdirs()) {
            return;
        }

        File updatesConfig = new File(optionsDir, "updates.xml");
        try {
            if (!updatesConfig.exists() && !updatesConfig.createNewFile()) {
                return;
            }
        } catch (IOException ignore) {
            return;
        }

        Document doc = Utils.getXmlDocument(updatesConfig);

        if (doc == null || !doc.hasRootElement()) {
            doc = Utils.createUpdatesXml();
        } else {
            Utils.repairUpdateXml(doc);
        }

        try {
            Utils.outputXml(doc, updatesConfig);
        } catch (IOException e) {
            LOG.warn("Failed write to " + updatesConfig);
        }
    }

    void setExtension(@NotNull ProductPluginExtension extension) {
        this.extension = extension;
    }
}
