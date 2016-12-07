package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.stepik.gradle.plugins.jetbrains.dependency.ProductDependency

/**
 * @author meanmail
 */
class ProductPluginExtension {
    private static final Logger LOG = Logging.getLogger(ProductPluginExtension.class)

    static final String DEFAULT_VERSION = "LATEST-EAP-SNAPSHOT"
    static final String DEFAULT_ARCHIVE_TYPE = Utils.getDefaultArchiveType()
    private final def systemProperties = new HashMap<String, Object>()
    private File idePath
    private String version = DEFAULT_VERSION
    private String archiveType = DEFAULT_ARCHIVE_TYPE
    private String type
    private String pluginName
    private File sandboxDirectory
    private String repository
    private String sinceBuild
    private String untilBuild
    private ProductDependency dependency
    private boolean updateSinceUntilBuild
    private boolean sameSinceUntilBuild
    private String pluginDescription
    private String changeNotes
    private Project project
    private BasePlugin plugin
    boolean instrumentCode

    RepositoryType repositoryType

    String getType() {
        return type
    }

    void setType(String type) {
        this.type = type
    }

    String getVersion() {
        return version.startsWith("CE-") || version.startsWith("IC-") ? version.substring(3) : version
    }

    void setVersion(String version) {
        this.version = version != null ? version : DEFAULT_VERSION
    }

    @NotNull
    String getArchiveType() {
        return archiveType
    }

    void setArchiveType(String archiveType) {
        this.archiveType = archiveType != null ? archiveType : DEFAULT_ARCHIVE_TYPE
    }

    Map<String, Object> getSystemProperties() {
        return systemProperties
    }

    void setSystemProperties(Map<String, ?> properties) {
        systemProperties.clear()
        systemProperties.putAll(properties)
    }

    String getSinceBuild() {
        if (updateSinceUntilBuild) {
            def ideVersion = IdeVersion.fromString(dependency.buildNumber)
            if (!ideVersion) {
                return null
            }

            return ideVersion.baselineVersion + "." + ideVersion.build
        }
        return null
    }

    void setSinceBuild(String sinceBuild) {
        this.sinceBuild = sinceBuild
    }

    File getSandboxDirectory() {
        return sandboxDirectory
    }

    void setSandboxDirectory(Object sandboxDirectory) {
        if (sandboxDirectory == null) {
            return
        }
        this.sandboxDirectory = new File(sandboxDirectory.toString())
    }

    @NotNull
    File getIdePath() {
        if (idePath != null) {
            if (idePath.name.endsWith(".app")) {
                idePath = new File(idePath, "Contents")
            }

            if (!idePath.exists()) {
                return dependency.classes
            }

            return idePath
        }

        return Utils.getDefaultIdePath(project, plugin, type, version, archiveType)
    }

    void setIdePath(Object idePath) {
        if (idePath instanceof File) {
            this.idePath = (File) idePath
        }

        this.idePath = idePath != null ? new File(idePath.toString()) : null
    }

    String getPluginName() {
        return pluginName
    }

    void setPluginName(String pluginName) {
        this.pluginName = pluginName
    }

    @Nullable
    String getRepository() {
        if (repository == null) {
            return null
        }
        repository = repository.replaceAll('\\[productName]', plugin.productName)
        repository = repository.replaceAll('\\[productName\\.toLowerCase\\(\\)]', plugin.productName.toLowerCase())
        repository = repository.replaceAll('\\[productType]', plugin.productType)
        repository = repository.replaceAll('\\[version]', version)
        repository = repository.replaceAll('\\[archiveType]', archiveType)

        return repository
    }

    void setRepository(String repository) {
        this.repository = repository
    }

    String getUntilBuild() {
        if (updateSinceUntilBuild) {
            def ideVersion = IdeVersion.fromString(dependency.buildNumber)
            if (!ideVersion) {
                return null
            }

            if (sameSinceUntilBuild) {
                return ideVersion.baselineVersion + ".*"
            } else if (untilBuild) {
                return untilBuild + ".*"
            }
        }
        return null
    }

    void setUntilBuild(String untilBuild) {
        this.untilBuild = untilBuild
    }

    ProductDependency getDependency() {
        return dependency
    }

    void setDependency(ProductDependency dependency) {
        this.dependency = dependency
    }

    boolean getUpdateSinceUntilBuild() {
        return updateSinceUntilBuild
    }

    void setUpdateSinceUntilBuild(boolean updateSinceUntilBuild) {
        this.updateSinceUntilBuild = updateSinceUntilBuild
    }

    boolean getSameSinceUntilBuild() {
        return sameSinceUntilBuild
    }

    boolean isSameSinceUntilBuild() {
        return sameSinceUntilBuild
    }

    void setSameSinceUntilBuild(boolean sameSinceUntilBuild) {
        this.sameSinceUntilBuild = sameSinceUntilBuild
    }

    String getPluginDescription() {
        return pluginDescription
    }

    void setPluginDescription(Object pluginDescription) {
        if (pluginDescription instanceof File) {
            def file = (File) pluginDescription

            this.pluginDescription = Utils.readFromFile(file)

            if (this.pluginDescription == null) {
                LOG.warn("Failed read description from " + file)
            }

            return
        }

        this.pluginDescription = pluginDescription.toString()
    }

    String getChangeNotes() {
        return changeNotes
    }

    void setChangeNotes(Object changeNotes) {
        if (changeNotes instanceof File) {
            def file = (File) changeNotes

            this.changeNotes = Utils.readFromFile(file)

            if (this.changeNotes == null) {
                LOG.warn("Failed read change notes from " + file)
            }

            return
        }

        this.changeNotes = changeNotes.toString()
    }

    void setExtensionProject(Project project) {
        this.project = project
    }

    void setPlugin(BasePlugin plugin) {
        this.plugin = plugin
    }

    @Input
    @NotNull
    RepositoryType getRepositoryType() {
        return repositoryType
    }

    void setRepositoryType(@Nullable Object repositoryType) {
        if (repositoryType == null) {
            this.repositoryType = RepositoryType.DIRECTORY
        } else {
            this.repositoryType = RepositoryType.fromString(repositoryType.toString())
        }
    }
}
