package org.stepik.gradle.plugins.jetbrains.dependency

import com.sun.istack.internal.NotNull
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.publish.ivy.internal.artifact.DefaultIvyArtifact
import org.gradle.api.publish.ivy.internal.publication.DefaultIvyConfiguration
import org.gradle.api.publish.ivy.internal.publication.DefaultIvyPublicationIdentity
import org.gradle.api.publish.ivy.internal.publisher.IvyDescriptorFileGenerator
import org.gradle.tooling.BuildException
import org.stepik.gradle.plugins.jetbrains.ProductPluginExtension

class DependencyManager {
    @NotNull
    static ProductDependency resolveLocal(@NotNull Project project, @NotNull File localPath, @NotNull String productName) {
        if (!localPath.exists() || !localPath.isDirectory()) {
            throw new BuildException("Specified idePath '$localPath' is not path to $productName", null)
        }
        def buildNumber = project.extensions.getByType(ProductPluginExtension).version

        return createDependency(buildNumber, localPath, null, project)
    }

    @NotNull
    private static ProductDependency createDependency(String version, File classesDirectory, File sourcesDirectory, Project project) {
        return new ProductDependency(version, version, classesDirectory, sourcesDirectory,
                !hasKotlinDependency(project))
    }

    private static def hasKotlinDependency(@NotNull Project project) {
        def configurations = project.configurations
        def closure = {
            if ("org.jetbrains.kotlin" == it.group) {
                return "kotlin-runtime" == it.name || "kotlin-stdlib" == it.name || "kotlin-reflect" == it.name
            }
            return false
        }
        return configurations.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME).getAllDependencies().find(closure) ||
                configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME).getAllDependencies().find(closure)
    }

    static void register(@NotNull Project project, @NotNull ProductDependency dependency, @NotNull String productName) {
        def ivyFile = getOrCreateIvyXml(dependency, productName)
        project.repositories.ivy { repo ->
            repo.url = dependency.classes
            repo.ivyPattern(ivyFile.absolutePath) // ivy xml
            repo.artifactPattern("$dependency.classes.path/[artifact].[ext]")
            if (dependency.sources) {
                repo.artifactPattern("$dependency.sources.parent/[artifact]-openapi-[classifier].[ext]")
            }
        }
        project.dependencies.add(JavaPlugin.COMPILE_CONFIGURATION_NAME, [
                group: 'com.jetbrains', name: productName, version: dependency.version, configuration: 'compile'
        ])
    }

    private static File getOrCreateIvyXml(@NotNull ProductDependency dependency, @NotNull String productName) {
        def ivyFile = new File(dependency.classes, "${dependency.getFqn(productName)}.xml")
        if (!ivyFile.exists()) {
            def generator = new IvyDescriptorFileGenerator(new DefaultIvyPublicationIdentity("com.jetbrains", productName, dependency.version))
            generator.addConfiguration(new DefaultIvyConfiguration("default"))
            generator.addConfiguration(new DefaultIvyConfiguration("compile"))
            generator.addConfiguration(new DefaultIvyConfiguration("sources"))
            dependency.jarFiles.each {
                generator.addArtifact(createJarDependency(it, "compile", dependency.classes))
            }
            if (dependency.sources) {
                // source dependency must be named in the same way as module name
                def artifact = new DefaultIvyArtifact(dependency.sources, productName, "zip", "sources", "src")
                artifact.conf = "sources"
                generator.addArtifact(artifact)
            }
            generator.writeTo(ivyFile)
        }
        return ivyFile
    }

    @NotNull
    static DefaultIvyArtifact createJarDependency(File file, String configuration, File baseDir) {
        return createDependency(baseDir, file, configuration, "jar", "jar")
    }

    private static DefaultIvyArtifact createDependency(File baseDir, File file, String configuration,
                                                       String extension, String type) {
        def relativePath = baseDir.toURI().relativize(file.toURI()).getPath()
        def name = extension ? relativePath - ".$extension" : relativePath
        def artifact = new DefaultIvyArtifact(file, name, extension, type, null)
        artifact.conf = configuration
        return artifact
    }
}


