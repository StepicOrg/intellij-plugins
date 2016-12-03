package org.stepik.gradle.plugins.jetbrains.dependency

import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.ivy.internal.artifact.DefaultIvyArtifact
import org.gradle.api.publish.ivy.internal.publication.DefaultIvyConfiguration
import org.gradle.api.publish.ivy.internal.publication.DefaultIvyPublicationIdentity
import org.gradle.api.publish.ivy.internal.publisher.IvyDescriptorFileGenerator
import org.gradle.tooling.BuildException
import org.jetbrains.annotations.NotNull
import org.stepik.gradle.plugins.jetbrains.ProductPluginExtension

/**
 * @author meanmail
 */
class DependencyManager {
    @NotNull
    static ProductDependency resolveLocal(
            @NotNull Project project,
            @NotNull ProductPluginExtension extension,
            @NotNull File idePath,
            @NotNull String productName) {
        if (!idePath.exists() || !idePath.isDirectory()) {
            throw new BuildException("Specified idePath '$idePath' is not path to $productName", null)
        }

        return createCompileDependency(extension.version, idePath, project)
    }

    @NotNull
    private static ProductDependency createCompileDependency(
            @NotNull String version,
            @NotNull File classesDirectory,
            @NotNull Project project) {
        return new ProductDependency(version,
                version,
                classesDirectory,
                !hasKotlinDependency(project))
    }

    @NotNull
    private static Boolean hasKotlinDependency(@NotNull Project project) {
        def configurations = project.configurations
        def closure = {
            if ("org.jetbrains.kotlin" == it.group) {
                return "kotlin-runtime" == it.name || "kotlin-stdlib" == it.name || "kotlin-reflect" == it.name
            }
            return false
        }
        return configurations.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME).allDependencies.find(closure) ||
                configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).allDependencies.find(closure)
    }

    static void register(
            @NotNull Project project,
            @NotNull final ProductDependency dependency,
            @NotNull String productName) {
        productName = productName.toLowerCase()

        final File ivyFile = getOrCreateIvyXml(dependency, productName)
        project.repositories.ivy {
            it.setUrl(dependency.classes)
            it.ivyPattern(ivyFile.absolutePath)
            it.artifactPattern("${dependency.classes.path}/[artifact].[ext]")
        }

        def map = [
                group        : "com.jetbrains",
                name         : productName,
                version      : dependency.version,
                configuration: "compile"
        ]

        project.dependencies.add(JavaPlugin.COMPILE_CONFIGURATION_NAME, map)
    }

    private static File getOrCreateIvyXml(
            @NotNull final ProductDependency dependency,
            @NotNull final String productName) {
        def ivyFile = new File(dependency.classes, "${dependency.getFqn(productName)}.xml")
        if (!ivyFile.exists()) {
            final def version = dependency.version
            final def identity = new DefaultIvyPublicationIdentity("com.jetbrains", productName, version)
            final def generator = new IvyDescriptorFileGenerator(identity)
            generator.addConfiguration(new DefaultIvyConfiguration("default"))
            generator.addConfiguration(new DefaultIvyConfiguration("compile"))
            generator.addConfiguration(new DefaultIvyConfiguration("sources"))
            dependency.jarFiles.each {
                generator.addArtifact(createJarCompileDependency(it, dependency.classes))
            }

            generator.writeTo(ivyFile)
        }

        return ivyFile
    }

    @NotNull
    private static DefaultIvyArtifact createJarCompileDependency(@NotNull File file, @NotNull File baseDir) {
        def relativePath = baseDir.toURI().relativize(file.toURI()).path

        def name = relativePath - ".jar"

        def artifact = new DefaultIvyArtifact(file, name, "jar", "jar", null)
        artifact.conf = "compile"
        return artifact
    }
}
