package org.stepik.gradle.plugins.jetbrains;

import com.intellij.structure.domain.IdeVersion;
import org.gradle.api.file.FileCollection;
import org.gradle.api.internal.ConventionTask;
import org.gradle.api.tasks.Input;
import org.gradle.api.tasks.InputFiles;
import org.gradle.api.tasks.Optional;
import org.gradle.api.tasks.OutputDirectory;
import org.gradle.api.tasks.SkipWhenEmpty;
import org.gradle.api.tasks.TaskAction;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * @author meanmail
 */
public class PatchPluginXmlTask extends ConventionTask {
    private String destinationDirName;
    private ProductPluginExtension extension;

    @SuppressWarnings("WeakerAccess")
    @OutputDirectory
    public File getDestinationDir() {
        return destinationDirName != null ? new File(getProject().getBuildDir(), destinationDirName) : null;
    }

    @SuppressWarnings("WeakerAccess")
    @SkipWhenEmpty
    @InputFiles
    public FileCollection getPluginXmlFiles() {
        return Utils.sourcePluginXmlFiles(getProject());
    }

    @SuppressWarnings("WeakerAccess")
    @Input
    @Optional
    public String getVersion() {
        return getProject().getVersion().toString();
    }

    @SuppressWarnings("WeakerAccess")
    @Input
    @Optional
    public String getPluginDescription() {
        return extension != null ? extension.getPluginDescription() : null;
    }

    @SuppressWarnings("WeakerAccess")
    @Input
    @Optional
    public String getSinceBuild() {
        if (extension.getUpdateSinceUntilBuild()) {
            IdeVersion ideVersion = IdeVersion.createIdeVersion(extension.getDependency().getBuildNumber());
            return ideVersion.getBaselineVersion() + "." + ideVersion.getBuild();
        }

        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Input
    @Optional
    public String getUntilBuild() {
        if (extension.getUpdateSinceUntilBuild()) {
            IdeVersion ideVersion = IdeVersion.createIdeVersion(extension.getDependency().getBuildNumber());

            if (extension.getSameSinceUntilBuild()) {
                return ideVersion.getBaselineVersion() + ".*";
            } else if (extension.getUntilBuild() != null) {
                return extension.getUntilBuild() + ".*";
            }
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @Input
    @Optional
    public String getChangeNotes() {
        return extension != null ? extension.getChangeNotes() : null;
    }

    @TaskAction
    public void patchPluginXmlFiles() {
        FileCollection files = getPluginXmlFiles();
        if (files == null) {
            return;
        }

        files.forEach(file -> {
            try {
                Document pluginXml = Utils.getXmlDocument(file);
                if (pluginXml == null) {
                    return;
                }
                patchSinceUntilBuild(pluginXml, getSinceBuild(), getUntilBuild());
                patchElement(pluginXml, "description", getPluginDescription());
                patchElement(pluginXml, "change-notes", getChangeNotes());
                patchElement(pluginXml, "version", getVersion());

                File destinationFile = new File(getDestinationDir(), file.getName());

                Utils.outputXml(pluginXml, destinationFile);
            } catch (IOException ignore) {
            }
        });
    }

    private void patchSinceUntilBuild(
            @NotNull Document pluginXml,
            @Nullable String sinceBuild,
            @Nullable String untilBuild) {
        if (extension.isUpdateSinceUntilBuild()) {
            Element result = pluginXml.getRootElement().getChild("idea-version");

            if (result != null) {
                Utils.setAttributeValue(result, "since-build", sinceBuild);
                Utils.setAttributeValue(result, "until-build", untilBuild);
            }
        }
    }

    private static void patchElement(@NotNull Document pluginXml, @NotNull String name, @Nullable String value) {
        if (value != null) {
            Element result = pluginXml.getRootElement().getChild(name);

            if (result == null) {
                result = new Element(name);
                pluginXml.getRootElement().addContent(result);
            }

            result.setText(value);
        }
    }

    void setDestinationDirName(String destinationDirName) {
        this.destinationDirName = destinationDirName;
    }

    void setExtension(ProductPluginExtension extension) {
        this.extension = extension;
    }
}
