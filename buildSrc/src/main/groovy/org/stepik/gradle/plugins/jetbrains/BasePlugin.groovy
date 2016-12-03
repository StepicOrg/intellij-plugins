package org.stepik.gradle.plugins.jetbrains

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.file.DuplicatesStrategy
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.tasks.compile.AbstractCompile
import org.gradle.api.tasks.testing.Test
import org.gradle.internal.jvm.Jvm
import org.gradle.language.jvm.tasks.ProcessResources
import org.jetbrains.annotations.NotNull
import org.jetbrains.annotations.Nullable
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.stepik.gradle.plugins.jetbrains.dependency.DependencyManager

/**
 * @author meanmail
 */
abstract class BasePlugin implements Plugin<Project> {
    private static final Logger LOG = LoggerFactory.getLogger(BasePlugin)

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
    protected boolean extensionInstrumentCode

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
        }

        configureTasks(project, extension)
    }

    protected abstract String getRepositoryTemplate()

    private void configureTasks(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        LOG.info("Configuring {} gradle plugin", productName)
        configurePatchPluginXmlTask(project, extension)
        configurePrepareSandboxTask(project, extension)
        configureRunTask(project, extension)
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
        LOG.info("Configuring {} dependency", productName)

        def idePath = extension.getIdePath()

        def ideVersion = extension.getVersion()

        if (!idePath.exists()) {
            System.out.println("Download $extension.repository")

            def file = downloadProduct(extension, ideVersion)

            if (!file) {
                System.out.println("$productName not loaded")
                LOG.warn("{} not loaded from {}", productName, extension.getRepository())
                return
            }

            System.out.println("$productName Loaded")
            System.out.println("Start Unzip ${productName}...")

            UnZipper.unZip(file, idePath)
            System.out.println("Unzipped $productName to $idePath")
        }

        def dependency = DependencyManager.resolveLocal(project, extension, idePath, productName)
        extension.dependency = dependency
        DependencyManager.register(project, dependency, productName)

        def toolsJar = Jvm.current().getToolsJar()
        if (toolsJar) {
            project.getDependencies().add(JavaPlugin.RUNTIME_CONFIGURATION_NAME, project.files(toolsJar))
        }
    }

    @Nullable
    private File downloadProduct(@NotNull ProductPluginExtension extension, @NotNull String ideVersion) {
        URL url
        File file
        def repository = extension.getRepository()
        if (!repository) {
            return null
        }

        try {
            url = new URL(repository)
            def dir = extension.getIdePath().parentFile
            dir.mkdirs()
            file = new File(dir, "${ideVersion}.zip")

            def bis
            try {
                bis = new BufferedInputStream(url.openStream())
                def fis
                try {
                    fis = new FileOutputStream(file)
                    byte[] buffer = new byte[1024]
                    int count
                    while ((count = bis.read(buffer, 0, 1024)) != -1) {
                        fis.write(buffer, 0, count)
                    }
                } finally {
                    if (fis) {
                        fis.close()
                    }
                }
            } finally {
                if (bis) {
                    bis.close()
                }
            }
        } catch (IOException ignored) {
            LOG.error("Failure download {} from {}", productName, repository)
            return null
        }
        return file
    }

    private void configurePrepareSandboxTask(
            @NotNull Project project,
            @NotNull ProductPluginExtension ext) {
        LOG.info("Configuring prepare {} sandbox task", productName)

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
        LOG.info("Configuring patch plugin.xml task")

        project.getTasks().create(patchPluginXmlTaskName, PatchPluginXmlTask).with {
            group = tasksGroupName
            description = "Patch plugin xml files with corresponding since/until build numbers and version attributes"
            destinationDirName = getPluginXmlDirName()
            extension = ext
        }
    }

    private void configureRunTask(@NotNull Project project, @NotNull ProductPluginExtension ext) {
        LOG.info("Configuring run {} task", productName)

        project.getTasks().create("run$productName", runTaskClass).with {
            group = tasksGroupName
            description = "Runs $productName with installed plugin."
            extension = ext
            plugin = this
            dependsOn(prepareSandboxTaskName)
        }
    }

    private void configureProcessResources(@NotNull Project project) {
        LOG.info("Configuring {} resources task", productName)

        def processResourcesTask = project.tasks.findByName(JavaPlugin.PROCESS_RESOURCES_TASK_NAME) as ProcessResources
        if (processResourcesTask) {
            processResourcesTask.from(project.tasks.findByName(patchPluginXmlTaskName)) {
                into("META-INF")
                duplicatesStrategy = DuplicatesStrategy.INCLUDE
            }
        }
    }

    private void configureInstrumentation(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        LOG.info("Configuring IntelliJ compile tasks")
        def abstractCompileDependencies = { String taskName ->
            project.tasks.findByName(taskName).collect {
                it.taskDependencies.getDependencies(it).findAll { it instanceof AbstractCompile }
            }.flatten() as Collection<AbstractCompile>
        }
        abstractCompileDependencies(JavaPlugin.CLASSES_TASK_NAME).each {
            it.inputs.property("intellijIdeaDependency", extension.dependency.toString())
            it.doLast(new InstrumentCodeAction(false, extensionName) as Action<? super Task>)
        }
        abstractCompileDependencies(JavaPlugin.TEST_CLASSES_TASK_NAME).each {
            it.inputs.property("intellijIdeaDependency", extension.dependency.toString())
            it.doLast(new InstrumentCodeAction(true, extensionName) as Action<? super Task>)
        }
    }

    private void configureTestTasks(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        LOG.info("Configuring IntelliJ tests tasks")
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

    String getProductName() {
        return productName
    }

    protected void setProductName(String productName) {
        this.productName = productName
        this.prepareSandboxTaskName = "prepare${productName}Sandbox"
        this.prepareTestSandboxTaskName = "prepare${productName}TestSandbox"
        this.patchPluginXmlTaskName = "patch${productName}PluginXml"
        this.pluginXmlDirName = "patched${productName}PluginXmlFiles"
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

    String getProductType() {
        return productType
    }

    String getExtensionName() {
        return extensionName
    }
}
