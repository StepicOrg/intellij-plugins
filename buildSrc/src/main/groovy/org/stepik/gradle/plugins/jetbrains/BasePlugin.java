package org.stepik.gradle.plugins.jetbrains;

import org.gradle.api.Action;
import org.gradle.api.Plugin;
import org.gradle.api.Project;
import org.gradle.api.file.DuplicatesStrategy;
import org.gradle.api.plugins.JavaPlugin;
import org.gradle.internal.jvm.Jvm;
import org.gradle.language.jvm.tasks.ProcessResources;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.stepik.gradle.plugins.jetbrains.dependency.DependencyManager;
import org.stepik.gradle.plugins.jetbrains.dependency.ProductDependency;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * @author meanmail
 */
public abstract class BasePlugin implements Plugin<Project> {
    private static final Logger LOG = LoggerFactory.getLogger(BasePlugin.class);

    protected String extensionName;
    protected String productType;
    protected String productGroup;
    protected String tasksGroupName;
    protected Class<? extends BaseRunTask> runTaskClass;
    private String productName;
    private String prepareSandboxTaskName;
    private String patchPluginXmlTaskName;
    private String pluginXmlDirName;

    @Override
    public void apply(final Project project) {
        project.getPlugins().apply(JavaPlugin.class);
        final ProductPluginExtension extension = project.getExtensions()
                .create(extensionName, ProductPluginExtension.class);

        extension.setType(productType);
        extension.setPluginName(project.getName());
        File defaultSandboxDirectory = new File(project.getBuildDir(),productName.toLowerCase() + "-sandbox");
        extension.setSandboxDirectory(defaultSandboxDirectory);
        extension.setRepository(getRepositoryTemplate());
        extension.setExtensionProject(project);
        extension.setPlugin(this);
        extension.setIdePath(Utils.getDefaultIdePath(project, this, productType,
                ProductPluginExtension.DEFAULT_VERSION));

        configureTasks(project, extension);
    }

    protected abstract String getRepositoryTemplate();

    private void configureTasks(@NotNull Project project, @NotNull final ProductPluginExtension extension) {
        LOG.info("Configuring {} gradle plugin", productName);
        configurePatchPluginXmlTask(project, extension);
        configurePrepareSandboxTask(project, extension);
        configureRunTask(project, extension);
        configureProcessResources(project);
        project.afterEvaluate(it -> configureProjectAfterEvaluate(it, extension));
    }

    private void configureProjectAfterEvaluate(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        for (Project subproject : project.getSubprojects()) {
            ProductPluginExtension subprojectExtension = (ProductPluginExtension) subproject.getExtensions()
                    .findByName(extensionName);
            if (subprojectExtension != null) {
                configureProjectAfterEvaluate(subproject, subprojectExtension);
            }
        }

        configureDependency(project, extension);
    }

    private void configureDependency(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        LOG.info("Configuring {} dependency", productName);

        File idePath = extension.getIdePath();

        final String ideVersion = extension.getVersion();

        if (!idePath.exists()) {
            System.out.println("Start download " + productName + extension.getType() + "-" + ideVersion);

            File file = downloadProduct(extension, ideVersion);

            if (file == null) {
                System.out.println(productName + " not loaded");
                LOG.warn("{} not loaded from {}", productName, extension.getRepository());
                return;
            }

            System.out.println(productName + " Loaded");
            System.out.println("Start Unzip " + productName + "...");

            UnZipper.unZip(file, idePath);
            System.out.println("Unzipped " + productName + " to " + idePath);
        }

        ProductDependency dependency = DependencyManager.resolveLocal(project, extension, idePath, productName);
        extension.setDependency(dependency);
        DependencyManager.register(project, dependency, productName);

        File toolsJar = Jvm.current().getToolsJar();
        if (toolsJar != null) {
            project.getDependencies().add(JavaPlugin.RUNTIME_CONFIGURATION_NAME, project.files(toolsJar));
        }
    }

    @Nullable
    private File downloadProduct(@NotNull ProductPluginExtension extension, @NotNull String ideVersion) {
        URL url;
        File file;
        String repository = extension.getRepository();
        if (repository == null) {
            return null;
        }

        try {
            url = new URL(repository);
            Path tempDirectory = Files.createTempDirectory("product");
            file = tempDirectory.resolve(ideVersion + ".zip").toFile();

            try (BufferedInputStream bis = new BufferedInputStream(url.openStream())) {
                try (FileOutputStream fis = new FileOutputStream(file)) {
                    byte[] buffer = new byte[1024];
                    int count;
                    while ((count = bis.read(buffer, 0, 1024)) != -1) {
                        fis.write(buffer, 0, count);
                    }
                }
            }
        } catch (IOException e) {
            LOG.error("Failure download {} from {}", productName, repository);
            return null;
        }
        return file;
    }

    private void configurePrepareSandboxTask(
            @NotNull final Project project,
            @NotNull final ProductPluginExtension extension) {
        LOG.info("Configuring prepare {} sandbox task", productName);

        project.getTasks().create(prepareSandboxTaskName, PrepareSandboxTask.class,
                task -> {
                    task.setGroup(tasksGroupName);
                    task.setDescription("Prepare sandbox directory with installed plugin and its dependencies.");
                    task.setExtension(extension);
                    task.dependsOn(JavaPlugin.JAR_TASK_NAME);
                });
    }

    private void configurePatchPluginXmlTask(
            @NotNull final Project project,
            @NotNull final ProductPluginExtension extension) {
        LOG.info("Configuring patch plugin.xml task");

        project.getTasks().create(patchPluginXmlTaskName, PatchPluginXmlTask.class,
                task -> {
                    task.setGroup(tasksGroupName);
                    task.setDescription("Patch plugin xml files with corresponding since/until build numbers and version attributes");
                    task.setDestinationDirName(pluginXmlDirName);
                    task.setExtension(extension);
                });
    }

    private void configureRunTask(@NotNull final Project project, @NotNull final ProductPluginExtension extension) {
        LOG.info("Configuring run {} task", productName);

        project.getTasks().create("run" + productName,
                (Class<? extends BaseRunTask>) runTaskClass,
                (Action<BaseRunTask>) task -> {
                    task.setGroup(tasksGroupName);
                    task.setDescription("Runs " + productName + " with installed plugin.");
                    task.setExtension(extension);
                    task.setPlugin(this);

                    task.dependsOn(prepareSandboxTaskName);
                });
    }

    private void configureProcessResources(@NotNull Project project) {
        LOG.info("Configuring {} resources task", productName);
        ProcessResources task = (ProcessResources) Utils.findTask(project, JavaPlugin.PROCESS_RESOURCES_TASK_NAME);

        if (task != null) {
            PatchPluginXmlTask patchTask = (PatchPluginXmlTask) Utils.findTask(project, patchPluginXmlTaskName);
            task.from(patchTask, copySpec -> {
                copySpec.into(new File("META-INF"));
                copySpec.setDuplicatesStrategy(DuplicatesStrategy.INCLUDE);
            });
        }
    }

    String getProductName() {
        return productName;
    }

    protected void setProductName(final String productName) {
        this.productName = productName;
        this.prepareSandboxTaskName = "prepare" + productName + "Sandbox";
        this.patchPluginXmlTaskName = "patch" + productName + "PluginXml";
        this.pluginXmlDirName = "patched" + productName + "PluginXmlFiles";
    }

    String getProductGroup() {
        return productGroup;
    }

    String getPrepareSandboxTaskName() {
        return prepareSandboxTaskName;
    }

    String getProductType() {
        return productType;
    }
}
