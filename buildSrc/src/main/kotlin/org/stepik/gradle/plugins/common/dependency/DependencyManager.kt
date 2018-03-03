package org.stepik.gradle.plugins.common.dependency

import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.plugins.JavaPlugin.COMPILE_CONFIGURATION_NAME
import org.gradle.api.plugins.JavaPlugin.RUNTIME_CONFIGURATION_NAME
import org.gradle.api.publish.ivy.internal.artifact.DefaultIvyArtifact
import org.gradle.api.publish.ivy.internal.publication.DefaultIvyConfiguration
import org.gradle.api.publish.ivy.internal.publication.DefaultIvyPublicationIdentity
import org.gradle.api.publish.ivy.internal.publisher.IvyDescriptorFileGenerator
import org.gradle.tooling.BuildException
import org.slf4j.LoggerFactory
import org.stepik.gradle.plugins.common.BasePlugin
import org.stepik.gradle.plugins.common.ProductPluginExtension
import org.stepik.gradle.plugins.common.Utils.downloadProduct
import org.stepik.gradle.plugins.common.Utils.getArchivePath
import org.stepik.gradle.plugins.common.Utils.untgz
import org.stepik.gradle.plugins.common.Utils.unzip
import java.io.File
import java.net.URI

object DependencyManager {
    private val logger = LoggerFactory.getLogger(DependencyManager::class.java)

    val dependencies: MutableSet<ProductDependency> = mutableSetOf()

    fun resolveRemoteMaven(project: Project, extension: ProductPluginExtension): ProductDependency {
        logger.debug("Adding ${extension.productName} repository")
        project.repositories.maven { it.url = URI(extension.repository) }

        logger.debug("Adding ${extension.productName} dependency")
        val libraryType = extension.productType
        val version = extension.version
        val group = extension.productGroup
        val name = extension.productName.toLowerCase()
        val dependency = project.dependencies.create("$group:$name$libraryType:$version")
        val configuration = project.configurations.detachedConfiguration(dependency)

        val classesDirectory = getClassesDirectory(project, configuration)
        extension.idePath = classesDirectory.path
        return createCompileDependency(project, name, version, classesDirectory)
    }

    private fun getClassesDirectory(project: Project, configuration: Configuration): File {
        val zipFile = configuration.singleFile
        logger.debug("Product zip: ${zipFile.path}")
        val directoryName = zipFile.name.removeSuffix(".zip")

        val cacheDirectory = File(zipFile.parent, directoryName)
        val markerFile = File(cacheDirectory, "markerFile")
        if (!markerFile.exists()) {
            cacheDirectory.deleteRecursively()
            cacheDirectory.mkdir()
            logger.debug("Unzipping...")
            project.copy {
                it.from(project.zipTree(zipFile))
                it.into(cacheDirectory)
            }
            markerFile.createNewFile()
            logger.debug("Unzipped")
        }
        return cacheDirectory
    }

    fun resolveLocal(project: Project, extension: ProductPluginExtension): ProductDependency? {
        val idePath = extension.ideDirectory ?: return null
        if (!idePath.exists() || !idePath.isDirectory) {
            throw BuildException("Specified idePath '$idePath' is not path to ${extension.productName}", null)
        }

        return createCompileDependency(project, extension.productName.toLowerCase(), extension.version, idePath)
    }


    private fun createCompileDependency(project: Project, productName: String,
                                        version: String, classesDirectory: File): ProductDependency {
        return ProductDependency(productName, version, version,
                classesDirectory, !hasKotlinDependency(project))
    }


    private fun hasKotlinDependency(project: Project): Boolean {
        return project.configurations.let {
            it.getByName(RUNTIME_CONFIGURATION_NAME).allDependencies.union(
                    it.getByName(COMPILE_CONFIGURATION_NAME).allDependencies).any {
                ("org.jetbrains.kotlin" == it.group) && it.name in listOf("kotlin-runtime", "kotlin-stdlib", "kotlin-reflect")
            }
        }
    }

    fun register(project: Project, dependency: ProductDependency, extension: ProductPluginExtension) {
        val name = (extension.productName + extension.productType).toLowerCase()

        val ivyFile = getOrCreateIvyXml(dependency, name)
        project.repositories.ivy {
            it.setUrl(dependency.classes)
            it.ivyPattern(ivyFile.absolutePath)
            it.artifactPattern("${dependency.classes.path}/[artifact].[ext]")
        }

        val map = mapOf(
                "group" to "com.jetbrains",
                "name" to name,
                "version" to dependency.version,
                "configuration" to "compile"
        )

        project.dependencies.add(COMPILE_CONFIGURATION_NAME, map)
        dependencies.add(dependency)
    }

    private fun getOrCreateIvyXml(dependency: ProductDependency, productName: String): File {
        val ivyFile = File(dependency.classes, "${dependency.getFqn(productName)}.xml")
        if (!ivyFile.exists()) {
            val version = dependency.version
            val identity = DefaultIvyPublicationIdentity("com.jetbrains", productName, version)
            val generator = IvyDescriptorFileGenerator(identity)
            generator.addConfiguration(DefaultIvyConfiguration("default"))
            generator.addConfiguration(DefaultIvyConfiguration("compile"))
            generator.addConfiguration(DefaultIvyConfiguration("sources"))
            dependency.jarFiles.forEach {
                generator.addArtifact(createJarCompileDependency(it, dependency.classes))
            }

            generator.writeTo(ivyFile)
        }

        return ivyFile
    }


    private fun createJarCompileDependency(file: File, baseDir: File): DefaultIvyArtifact {
        val relativePath = baseDir.toURI().relativize(file.toURI()).path

        val name = relativePath.removeSuffix(".jar")

        val artifact = DefaultIvyArtifact(file, name, "jar", "jar", null)
        artifact.conf = "compile"
        return artifact
    }

    fun resolveLocalCashRepository(project: Project, basePlugin: BasePlugin,
                                   extension: ProductPluginExtension): ProductDependency? {
        val idePath = extension.ideDirectory ?: return null
        val ideArchiveType = extension.archiveType
        val productName = basePlugin.productName

        if (!idePath.exists()) {
            val archive: File = getArchivePath(project, basePlugin, extension)
            if (!archive.exists()) {
                logger.info("Download {}", extension.repository)
                println("Download ${extension.repository}")

                downloadProduct(basePlugin, extension, archive)

                if (!archive.exists()) {
                    println("$productName not loaded from ${extension.repository}")
                    logger.warn("{} not loaded from {}", productName, extension.repository)
                    return null
                }
            }

            logger.info("{} loaded", productName)
            println("$productName Loaded")

            println("Start unarchive $productName...")

            when (ideArchiveType) {
                "zip" -> unzip(archive, idePath)
                "tar.gz" -> untgz(archive, idePath)
            }

            logger.info("Unarchived $productName to $idePath")
            println("Unarchived $productName to $idePath")
        }

        return resolveLocal(project, extension)
    }

}
