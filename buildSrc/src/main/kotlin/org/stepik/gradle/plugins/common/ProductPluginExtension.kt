package org.stepik.gradle.plugins.common

import org.gradle.api.Project
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory
import org.stepik.gradle.plugins.common.Utils.getDefaultArchiveType
import org.stepik.gradle.plugins.common.Utils.getDefaultIdePath
import org.stepik.gradle.plugins.common.dependency.ProductDependency
import java.io.File


open class ProductPluginExtension(
        val productName: String,
        val productType: String,
        val productGroup: String,
        repository: String,
        private val project: Project,
        private val plugin: BasePlugin,
        var instrumentCode: Boolean,
        @get:Input var repositoryType: RepositoryType,
        val publish: ProductPluginExtensionPublish
) {

    @get:OutputDirectory
    val sandboxDirectory: String =
            project.buildDir.resolve(File(productName, "sandbox")).toString()

    var systemProperties = mutableMapOf<String, Any>()
        set(value) {
            field.clear()
            field.putAll(value)
        }

    var idePath: String? = null
        get() {
            var value = field
            if (value != null) {
                if (value.endsWith(".app")) {
                    value = File(value, "Contents").toString()
                    field = value
                }

                if (!File(value).exists()) {
                    return dependency?.classes?.path
                }

                return field
            }

            return getDefaultIdePath(project, plugin, productType, version, archiveType)
        }

    val ideDirectory: File?
        get() {
            val idePath = idePath ?: return null
            return File(idePath)
        }

    var version = DEFAULT_VERSION
        get() {
            return if (field != DEFAULT_VERSION) {
                field.substring(field.lastIndexOf('-') + 1)
            } else field
        }

    var archiveType = DEFAULT_ARCHIVE_TYPE

    var repository: String = repository
        get() {
            return field.replace("[productName]", plugin.productName)
                    .replace("[productName.toLowerCase()]", plugin.productName.toLowerCase())
                    .replace("[productType]", plugin.productType)
                    .replace("[version]", version)
                    .replace("[archiveType]", archiveType)
        }

    var sinceBuild: String? = null
        get() {
            if (updateSinceUntilBuild) {
                val buildNumber = dependency?.buildNumber ?: return null
                val ideVersion = IdeVersion.fromString(buildNumber) ?: return null
                return "${ideVersion.baselineVersion}.${ideVersion.build}"
            }
            return null
        }

    var untilBuild: String? = null
        get() {
            if (updateSinceUntilBuild) {
                val buildNumber = dependency?.buildNumber ?: return null
                val ideVersion = IdeVersion.fromString(buildNumber) ?: return null

                if (sameSinceUntilBuild) {
                    return "${ideVersion.baselineVersion}.*"
                } else if (field != null) {
                    return "$field.*"
                }
            }
            return null
        }

    var dependency: ProductDependency? = null

    var updateSinceUntilBuild: Boolean = false

    var sameSinceUntilBuild: Boolean = false

    var pluginDescription: String? = null

    var changeNotes: String? = null


    companion object {

        private val DEFAULT_ARCHIVE_TYPE = getDefaultArchiveType()
        private const val DEFAULT_VERSION = "LATEST-EAP-SNAPSHOT"

    }
}

fun ExtensionContainer.createProductPluginExtension(name: String,
                                                    productName: String,
                                                    productType: String,
                                                    productGroup: String,
                                                    repository: String,
                                                    project: Project,
                                                    plugin: BasePlugin,
                                                    instrumentCode: Boolean,
                                                    repositoryType: RepositoryType,
                                                    publish: ProductPluginExtensionPublish
): ProductPluginExtension {
    return create(name, ProductPluginExtension::class.java, productName, productType,
            productGroup, repository, project, plugin, instrumentCode,
            repositoryType, publish)
}
