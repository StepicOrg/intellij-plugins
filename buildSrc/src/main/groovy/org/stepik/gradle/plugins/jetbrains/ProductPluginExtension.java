package org.stepik.gradle.plugins.jetbrains;

import org.gradle.api.Project;
import org.gradle.api.logging.Logger;
import org.gradle.api.logging.Logging;
import org.jetbrains.annotations.NotNull;
import org.stepik.gradle.plugins.jetbrains.dependency.ProductDependency;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ProductPluginExtension {
    private static final Logger LOG = Logging.getLogger(ProductPluginExtension.class);

    public static final String DEFAULT_VERSION = "LATEST-EAP-SNAPSHOT";
    private final Map<String, Object> systemProperties = new HashMap<>();
    private File idePath;
    private String version = DEFAULT_VERSION;
    private String type;
    private String pluginName;
    private File sandboxDirectory;
    private String repository;
    private String sinceBuild;
    private String untilBuild;
    private ProductDependency dependency;
    private boolean updateSinceUntilBuild;
    private boolean sameSinceUntilBuild;
    private String pluginDescription;
    private String changeNotes;
    private Project project;
    private BasePlugin plugin;

    public String getType() {
        return type;
    }

    void setType(String type) {
        this.type = type;
    }

    public String getVersion() {
        return version.startsWith("CE-") || version.startsWith("IC-") ? version.substring(3) : version;
    }

    @SuppressWarnings("unused")
    public void setVersion(String version) {
        this.version = version != null? version : DEFAULT_VERSION;
    }

    public Map<String, Object> getSystemProperties() {
        return systemProperties;
    }

    @SuppressWarnings("unused")
    public void setSystemProperties(Map<String, ?> properties) {
        systemProperties.clear();
        systemProperties.putAll(properties);
    }

    @SuppressWarnings("unused")
    String getSinceBuild() {
        return sinceBuild;
    }

    @SuppressWarnings("unused")
    public void setSinceBuild(String sinceBuild) {
        this.sinceBuild = sinceBuild;
    }

    File getSandboxDirectory() {
        return sandboxDirectory;
    }

    void setSandboxDirectory(Object sandboxDirectory) {
        if (sandboxDirectory == null) {
            return;
        }
        this.sandboxDirectory = new File(sandboxDirectory.toString());
    }

    @NotNull
    File getIdePath() {
        if (idePath != null) {
            if (idePath.getName().endsWith(".app")) {
                idePath = new File(idePath, "Contents");
            }

            if (!idePath.exists()) {
                return dependency.getClasses();
            }

            return idePath;
        }

        return Utils.getDefaultIdePath(project, plugin, getType(), getVersion());
    }

    public void setIdePath(Object idePath) {
        if (idePath instanceof File) {
            this.idePath = (File) idePath;
        }

        this.idePath = idePath != null? new File(idePath.toString()) : null;
    }

    String getPluginName() {
        return pluginName;
    }

    void setPluginName(String pluginName) {
        this.pluginName = pluginName;
    }

    @SuppressWarnings("unused")
    public String getRepository() {
        return repository;
    }

    void setRepository(String repository) {
        this.repository = repository;
    }

    public String getUntilBuild() {
        return untilBuild;
    }

    @SuppressWarnings("unused")
    public void setUntilBuild(String untilBuild) {
        this.untilBuild = untilBuild;
    }

    ProductDependency getDependency() {
        return dependency;
    }

    void setDependency(ProductDependency dependency) {
        this.dependency = dependency;
    }

    boolean getUpdateSinceUntilBuild() {
        return updateSinceUntilBuild;
    }

    public boolean isUpdateSinceUntilBuild() {
        return updateSinceUntilBuild;
    }

    @SuppressWarnings("unused")
    public void setUpdateSinceUntilBuild(boolean updateSinceUntilBuild) {
        this.updateSinceUntilBuild = updateSinceUntilBuild;
    }

    boolean getSameSinceUntilBuild() {
        return sameSinceUntilBuild;
    }

    @SuppressWarnings("unused")
    public boolean isSameSinceUntilBuild() {
        return sameSinceUntilBuild;
    }

    @SuppressWarnings("unused")
    public void setSameSinceUntilBuild(boolean sameSinceUntilBuild) {
        this.sameSinceUntilBuild = sameSinceUntilBuild;
    }

    public String getPluginDescription() {
        return pluginDescription;
    }

    @SuppressWarnings("unused")
    public void setPluginDescription(Object pluginDescription) {
        if (pluginDescription instanceof File) {
            File file = (File) pluginDescription;

            this.pluginDescription = Utils.readFromFile(file);

            if (this.pluginDescription == null) {
                LOG.warn("Failed read description from " + file);
            }

            return;
        }

        this.pluginDescription = pluginDescription.toString();
    }

    public String getChangeNotes() {
        return changeNotes;
    }

    @SuppressWarnings("unused")
    public void setChangeNotes(Object changeNotes) {
        if (changeNotes instanceof File) {
            File file = (File) changeNotes;

            this.changeNotes = Utils.readFromFile(file);

            if (this.changeNotes == null) {
                LOG.warn("Failed read change notes from " + file);
            }

            return;
        }

        this.changeNotes = changeNotes.toString();
    }

    void setExtensionProject(Project project) {
        this.project = project;
    }

    void setPlugin(BasePlugin plugin) {
        this.plugin = plugin;
    }
}
