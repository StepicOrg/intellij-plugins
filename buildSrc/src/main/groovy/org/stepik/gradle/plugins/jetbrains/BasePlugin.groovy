package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.bundling.Zip
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.jvm.Jvm
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.annotations.NotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.stepik.gradle.plugins.jetbrains.dependency.DependencyManager

/**
 * @author meanmail
 */
abstract class BasePlugin implements Plugin<Project> {
    private static final Logger logger = LoggerFactory.getLogger(BasePlugin)

    protected String extensionName
    protected String productType
    protected String productGroup
    protected String tasksGroupName
    protected Class<BaseRunTask> runTaskClass
    private String productName
    private String prepareSandboxTaskName
    private String prepareTestSandboxTaskName
    private String patchPluginXmlTaskName
    private String pluginXmlDirName
    private String buildPluginTaskName
    protected boolean extensionInstrumentCode
    RepositoryType repositoryType

    @Override
    void apply(Project project) {
        project.getPlugins().apply(JavaPlugin)
        def extension = project.getExtensions().create(extensionName, ProductPluginExtension)
        extension.with {
            type = productType
            pluginName = project.getName()
            sandboxDirectory = new File(project.getBuildDir(), "${productName.toLowerCase()}-sandbox")
            repository = getRepositoryTemplate()
            extensionProject = project
            plugin = this
            instrumentCode = extensionInstrumentCode
            repositoryType = this.repositoryType
            publish = new ProductPluginExtensionPublish()
        }

        configureTasks(project, extension)
    }

    protected abstract String getRepositoryTemplate()

    private void configureTasks(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        logger.info("Configuring {} gradle plugin", productName)
        configurePatchPluginXmlTask(project, extension)
        configurePrepareSandboxTask(project, extension)
        configureRunTask(project, extension)
        configureBuildPluginTask(project)
        configurePublishPluginTask(project, extension)
        configureProcessResources(project)
        project.afterEvaluate(new Action<Project>() {
            @Override
            void execute(Project it) {
                configureProjectAfterEvaluate(it, extension)
            }
        })
    }

    private void configureProjectAfterEvaluate(
            @NotNull Project project,
            @NotNull ProductPluginExtension extension) {
        for (Project subproject : project.getSubprojects()) {
            def subprojectExtension = subproject.getExtensions()
                    .findByName(extensionName) as ProductPluginExtension
            if (subprojectExtension) {
                configureProjectAfterEvaluate(subproject, subprojectExtension)
            }
        }

        configureDependency(project, extension)
        configureInstrumentation(project, extension)
        configureTestTasks(project, extension)
    }

    private void configureDependency(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        logger.info("Configuring {} dependency", productName)
        def dependency
        if (extension.repositoryType == RepositoryType.MAVEN) {
            dependency = DependencyManager.resolveRemoteMaven(project, this, extension)
        } else {
            dependency = DependencyManager.resolveLocalCashRepository(project, this, extension)
        }

        if (!dependency) {
            dependency = DependencyManager.resolveLocal(project, extension, productName)
        }

        extension.dependency = dependency
        DependencyManager.register(project, dependency, productName)

        def toolsJar = Jvm.current().getToolsJar()
        if (toolsJar) {
            project.getDependencies().add(JavaPlugin.RUNTIME_ELEMENTS_CONFIGURATION_NAME, project.files(toolsJar))
        }
    }

    private void configurePrepareSandboxTask(
            @NotNull Project project,
            @NotNull ProductPluginExtension ext) {
        logger.info("Configuring prepare {} sandbox task", productName)

        project.getTasks().create(prepareSandboxTaskName, PrepareSandboxTask).with {
            group = tasksGroupName
            description = "Prepare sandbox directory with installed plugin and its dependencies."
            extension = ext
            dependsOn(JavaPlugin.JAR_TASK_NAME)
        }
    }

    String getPluginXmlDirName() {
        return pluginXmlDirName
    }

    private void configurePatchPluginXmlTask(
            @NotNull Project project,
            @NotNull ProductPluginExtension ext) {
        logger.info("Configuring patch plugin.xml task")

        project.getTasks().create(patchPluginXmlTaskName, PatchPluginXmlTask).with {
            group = tasksGroupName
            description = "Patch plugin xml files with corresponding since/until build numbers and version attributes"
            destinationDirName = getPluginXmlDirName()
            extension = ext
        }
    }

    private void configureRunTask(@NotNull Project project, @NotNull ProductPluginExtension ext) {
        logger.info("Configuring run {} task", productName)

        project.getTasks().create("run$productName", runTaskClass).with {
            group = tasksGroupName
            description = "Runs $productName with installed plugin."
            extension = ext
            plugin = this
            dependsOn(prepareSandboxTaskName)
        }
    }

    private void configurePublishPluginTask(@NotNull Project project, @NotNull ProductPluginExtension ext) {
        logger.info("Configuring publishing {} plugin task", productName)
        project.tasks.create("publish$productName", PublishTask).with {
            group = tasksGroupName
            description = "Publish plugin distribution on plugins.jetbrains.com."
            extension = ext
            plugin = this
            dependsOn(buildPluginTaskName)
        }
    }

    private void configureProcessResources(@NotNull Project project) {
        logger.info("Configuring {} resources task", productName)

        def processResourcesTask = project.tasks.findByName(JavaPlugin.PROCESS_RESOURCES_TASK_NAME) as ProcessResources
        if (processResourcesTask) {
            processResourcesTask.from(project.tasks.findByName(patchPluginXmlTaskName)) {
                into("META-INF")
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
    }

    private void configureInstrumentation(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        logger.info("Configuring IntelliJ compile tasks")
        def instrumentCode = { extension.instrumentCode && extension.type != 'RS' }
        project.sourceSets.all { SourceSet sourceSet ->
            def instrumentTask = project.tasks.create(sourceSet.getTaskName("${productName}Instrument", 'code'), InstrumentCodeTask)
            instrumentTask.sourceSet = sourceSet
            instrumentTask.with {
                dependsOn sourceSet.classesTaskName
                onlyIf instrumentCode

                conventionMapping("ideaDependency", { extension.dependency })
                conventionMapping('outputDir', { new File(sourceSet.output.classesDirs[0].getParent(), "${sourceSet.name}") })
            }

            // Ensure that our task is invoked when the source set is built
            sourceSet.compiledBy(instrumentTask)
        }
    }

    private void configureTestTasks(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        logger.info("Configuring IntelliJ tests tasks")
        project.tasks.withType(Test).each {
            def configDirectory = project.file("${extension.sandboxDirectory}/config-test")
            def systemDirectory = project.file("${extension.sandboxDirectory}/system-test")
            def pluginsDirectory = project.file("${extension.sandboxDirectory}/plugins-test")

            it.enableAssertions = true
            it.systemProperties(extension.systemProperties)
            it.systemProperties(Utils.getProductSystemProperties(configDirectory, systemDirectory, pluginsDirectory))
            it.jvmArgs = Utils.getProductJvmArgs(it, it.jvmArgs, extension.idePath)
            if (extension.dependency != null) {
                it.classpath += project.files("$extension.dependency.classes/lib/resources.jar",
                        "$extension.dependency.classes/lib/idea.jar")
            }
            it.outputs.dir(systemDirectory)
            it.outputs.dir(configDirectory)
            it.dependsOn(project.getTasksByName(prepareTestSandboxTaskName, false))
        }
    }

    private void configureBuildPluginTask(@NotNull Project project) {
        logger.info("Configuring building plugin task")
        def prepareSandboxTask = project.tasks.findByName(prepareSandboxTaskName) as PrepareSandboxTask
        project.tasks.create(buildPluginTaskName, Zip).with {
            description = "Bundles the project as a distribution."
            group = tasksGroupName
            from { "${prepareSandboxTask.getDestinationDir()}/${prepareSandboxTask.getPluginName()}" }
            into { prepareSandboxTask.getPluginName() }
            dependsOn(prepareSandboxTask)
            conventionMapping.map('baseName', { prepareSandboxTask.getPluginName() })
            it
        }
    }

    String getProductName() {
        return productName
    }

    protected void setProductName(String productName) {
        this.productName = productName
        this.prepareSandboxTaskName = "prepare${productName}Sandbox"
        this.prepareTestSandboxTaskName = "prepare${productName}TestSandbox"
        this.patchPluginXmlTaskName = "patch${productName}PluginXml"
        this.pluginXmlDirName = "patched${productName}PluginXmlFiles"
        this.buildPluginTaskName = "build${productName}Plugin"
    }

    String getProductGroup() {
        return productGroup
    }

    String getPrepareSandboxTaskName() {
        return prepareSandboxTaskName
    }

    String getPrepareTestSandboxTaskName() {
        return prepareTestSandboxTaskName
    }

    String getBuildPluginTaskName() {
        return buildPluginTaskName
    }

    String getProductType() {
        return productType
    }

    String getExtensionName() {
        return extensionName
    }
}
