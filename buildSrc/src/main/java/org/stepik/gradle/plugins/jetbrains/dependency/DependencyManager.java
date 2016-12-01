package org.stepik.gradle.plugins.jetbrains.dependency;

import org.gradle.api.Project;
import org.gradle.api.artifacts.Configuration;
import org.gradle.api.artifacts.ConfigurationContainer;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.api.publish.ivy.internal.artifact.DefaultIvyArtifact;
import org.gradle.api.publish.ivy.internal.publication.DefaultIvyConfiguration;
import org.gradle.api.publish.ivy.internal.publication.DefaultIvyPublicationIdentity;
import org.gradle.api.publish.ivy.internal.publisher.IvyDescriptorFileGenerator;
import org.gradle.tooling.BuildException;
import org.jetbrains.annotations.NotNull;
import org.stepik.gradle.plugins.jetbrains.ProductPluginExtension;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.function.Predicate;

/**
 * @author meanmail
 */
public class DependencyManager {
    @NotNull
    public static ProductDependency resolveLocal(
            @NotNull Project project,
            @NotNull ProductPluginExtension extension,
            @NotNull File localPath,
            @NotNull String productName) {
        if (!localPath.exists() || !localPath.isDirectory()) {
            throw new BuildException("Specified idePath \'" + localPath + "\' is not path to " + productName, null);
        }

        String buildNumber = extension.getVersion();

        return createCompileDependency(buildNumber, localPath, project);
    }

    @NotNull
    private static ProductDependency createCompileDependency(
            String version,
            File classesDirectory,
            Project project) {
        return new ProductDependency(version,
                version,
                classesDirectory,
                !hasKotlinDependency(project));
    }

    private static Boolean hasKotlinDependency(@NotNull Project project) {
        ConfigurationContainer configurations = project.getConfigurations();

        Predicate<Configuration> testConfiguration = (configuration) ->
                configuration.getAllDependencies().stream()
                        .filter((dependency) -> {
                            if ("org.jetbrains.kotlin".equals(dependency.getGroup())) {
                                String name = dependency.getName();
                                if ("kotlin-runtime".equals(name) || "kotlin-stdlib".equals(name) || "kotlin-reflect".equals(
                                        name)) {
                                    return true;
                                }
                            }
                            return false;
                        })
                        .count() > 0;

        Configuration runtimeConfiguration = configurations.getByName(JavaPlugin.RUNTIME_CONFIGURATION_NAME);
        Configuration compileConfiguration = configurations.getByName(JavaPlugin.COMPILE_CONFIGURATION_NAME);

        return testConfiguration.test(runtimeConfiguration) || testConfiguration.test(compileConfiguration);
    }

    public static void register(
            @NotNull Project project,
            @NotNull final ProductDependency dependency,
            @NotNull String productName) {
        productName = productName.toLowerCase();

        final File ivyFile = getOrCreateIvyXml(dependency, productName);
        project.getRepositories().ivy(repo -> {
            repo.setUrl(dependency.getClasses());
            repo.ivyPattern(ivyFile.getAbsolutePath());
            repo.artifactPattern(dependency.getClasses().getPath() + "/[artifact].[ext]");
        });

        LinkedHashMap<String, String> map = new LinkedHashMap<>(4);
        map.put("group", "com.jetbrains");
        map.put("name", productName);
        map.put("version", dependency.getVersion());
        map.put("configuration", "compile");
        project.getDependencies().add(JavaPlugin.COMPILE_CONFIGURATION_NAME, map);
    }

    private static File getOrCreateIvyXml(
            @NotNull final ProductDependency dependency,
            @NotNull final String productName) {
        File ivyFile = new File(dependency.getClasses(), dependency.getFqn(productName) + ".xml");
        if (!ivyFile.exists()) {
            final IvyDescriptorFileGenerator generator = new IvyDescriptorFileGenerator(new DefaultIvyPublicationIdentity(
                    "com.jetbrains",
                    productName,
                    dependency.getVersion()));
            generator.addConfiguration(new DefaultIvyConfiguration("default"));
            generator.addConfiguration(new DefaultIvyConfiguration("compile"));
            generator.addConfiguration(new DefaultIvyConfiguration("sources"));
            dependency.getJarFiles()
                    .forEach(jar -> generator.addArtifact(createJarCompileDependency(jar, dependency.getClasses())));

            generator.writeTo(ivyFile);
        }

        return ivyFile;
    }

    @NotNull
    private static DefaultIvyArtifact createJarCompileDependency(File file, File baseDir) {
        String relativePath = baseDir.toURI().relativize(file.toURI()).getPath();

        String name;
        if (relativePath.endsWith(".jar")) {
            name = relativePath.substring(0, relativePath.length() - "jar".length() - 1);
        } else {
            name = relativePath;
        }

        DefaultIvyArtifact artifact = new DefaultIvyArtifact(file, name, "jar", "jar", null);
        artifact.setConf("compile");
        return artifact;
    }
}
