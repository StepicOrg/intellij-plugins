package org.stepik.gradle.plugins.idea

import com.sun.istack.internal.NotNull
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.JavaPlugin
import org.gradle.internal.jvm.Jvm
import org.gradle.jvm.tasks.Jar
import org.stepik.gradle.plugins.jetbrains.PrepareSandboxTask
import org.stepik.gradle.plugins.jetbrains.ProductPluginExtension
import org.stepik.gradle.plugins.jetbrains.UnZipper
import org.stepik.gradle.plugins.jetbrains.dependency.DependencyManager

import java.nio.file.Files

/**
 * @author meanmail
 */
class IdeaPlugin implements Plugin<Project> {
    public static final PRODUCT_NAME = "idea"
    public static final GROUP_NAME = "intellij"
    public static final EXTENSION_NAME = "intellij"
    public static final String DEFAULT_SANDBOX = 'idea-sandbox'
    public static final String PREPARE_SANDBOX_TASK_NAME = "prepareIdeaSandbox"
    public static final String RUN_TASK_NAME = "runIdea"

    public static final LOG = Logging.getLogger(IdeaPlugin)
    public static final String DEFAULT_VERSION = "LATEST-EAP-SNAPSHOT"
    public static final String DEFAULT_REPO = "https://www.jetbrains.com/intellij-repository/releases/com/jetbrains/intellij"
    public static final String PRODUCT_GROUP = "com.jetbrains.intellij.idea"

    static String getRepository(@NotNull ProductPluginExtension extension) {
        def type = extension.type
        def version = extension.version

        "$DEFAULT_REPO/$PRODUCT_NAME/$PRODUCT_NAME$type/${version}/$PRODUCT_NAME$type-${version}.zip"
    }
    @Override
    void apply(Project project) {
        project.getPlugins().apply(JavaPlugin)
        def extension = project.extensions.create(EXTENSION_NAME, ProductPluginExtension)
        extension.with {
            version = DEFAULT_VERSION
            type = 'IC'
            pluginName = project.name
            sandboxDirectory = new File(project.buildDir, DEFAULT_SANDBOX).absolutePath
            repository = getRepository(extension)
        }
        configureTasks(project, extension)
    }

    private static def configureTasks(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        LOG.info("Configuring $PRODUCT_NAME gradle plugin")
        configurePrepareSandboxTask(project, extension)
        configureRunTask(project, extension)
        project.afterEvaluate { configureProjectAfterEvaluate(it, extension) }
    }

    private static void configureProjectAfterEvaluate(@NotNull Project project,
                                                      @NotNull ProductPluginExtension extension) {
        for (def subproject : project.subprojects) {
            def subprojectExtension = subproject.extensions.findByType(ProductPluginExtension)
            if (subprojectExtension) {
                configureProjectAfterEvaluate(subproject, subprojectExtension)
            }
        }
        configureDependency(project, extension)
    }

    private static void configureDependency(@NotNull Project project,
                                            @NotNull ProductPluginExtension extension) {
        LOG.info("Configuring $PRODUCT_NAME dependency")

        File idePath = getIdePath(project, extension)

        def ideVersion = extension.version

        if (!idePath.exists()) {
            println "Start download $PRODUCT_NAME$extension.type-$ideVersion"

            def url = new URL(getRepository(extension))
            def tempDirectory = Files.createTempDirectory("product")
            def file = tempDirectory.resolve("${ideVersion}.zip").toFile()
            BufferedInputStream bis = new BufferedInputStream(url.openStream())
            FileOutputStream fis = new FileOutputStream(file)
            byte[] buffer = new byte[1024]
            //noinspection GroovyUnusedAssignment
            def count = 0
            while ((count = bis.read(buffer, 0, 1024)) != -1) {
                fis.write(buffer, 0, count)
            }
            fis.close()
            bis.close()

            println "$PRODUCT_NAME Loaded"
            println "Start Unzip $PRODUCT_NAME..."

            UnZipper.unZip(file, idePath)

            println "Unziped $PRODUCT_NAME to $idePath"
        } else {
            println "$PRODUCT_NAME Exists"
        }

        def dependency = DependencyManager.resolveLocal(project, idePath, PRODUCT_NAME)
        extension.dependency = dependency
        DependencyManager.register(project, dependency, PRODUCT_NAME)

        def toolsJar = Jvm.current().toolsJar
        if (toolsJar) {
            project.dependencies.add(JavaPlugin.RUNTIME_CONFIGURATION_NAME, project.files(toolsJar))
        }
    }

    private static File getIdePath(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        def path = extension.idePath
        if (path) {
            def dir = new File(path)
            if (dir.getName().endsWith(".app")) {
                dir = new File(dir, "Contents")
            }
            if (!dir.exists()) {
                def directory = extension.dependency.classes
                LOG.error("Cannot find alternate SDK path: $dir. Default IDEA will be used : $directory")
                return directory
            }
            return dir
        }
        return getDefaultIdePath(project, extension)
    }

    private static File getDefaultIdePath(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        final String gradleHomePath = project.gradle.gradleUserHomeDir.absolutePath
        final DEFAULT_RELATIVE_PATH = "/caches/modules-2/files-2.1/$PRODUCT_GROUP/$PRODUCT_NAME/$PRODUCT_NAME$extension.type/"

        def idePath = new File(gradleHomePath + DEFAULT_RELATIVE_PATH + extension.version)

        idePath
    }

    private static void configurePrepareSandboxTask(@NotNull Project project,
                                                    @NotNull ProductPluginExtension extension) {
        LOG.info("Configuring prepare $PRODUCT_NAME sandbox task")
        def taskName = PREPARE_SANDBOX_TASK_NAME
        project.tasks.create(taskName, PrepareSandboxTask).with {
            group = GROUP_NAME
            description = "Prepare sandbox directory with installed plugin and its dependencies."
            conventionMapping('pluginName', { extension.pluginName })
            conventionMapping('pluginJar', { (project.tasks.findByName(JavaPlugin.JAR_TASK_NAME) as Jar).archivePath })
            conventionMapping('destinationDir', {
                project.file("${extension.sandboxDirectory}/plugins")
            })
            conventionMapping('configDirectory', {
                project.file("${extension.sandboxDirectory}/config")
            })
            conventionMapping('librariesToIgnore', { project.files(extension.dependency.jarFiles) })
            dependsOn(JavaPlugin.JAR_TASK_NAME)
        }
    }

    private static void configureRunTask(@NotNull Project project, @NotNull ProductPluginExtension extension) {
        LOG.info("Configuring run IntelliJ task")
        project.tasks.create(RUN_TASK_NAME, RunIdeaTask).with {
            group = GROUP_NAME
            description = "Runs $PRODUCT_NAME with installed plugin."
            conventionMapping.map("idePath", { getIdePath(project, extension) })
            conventionMapping.map("systemProperties", { extension.systemProperties })
            conventionMapping.map("configDirectory", {
                (project.tasks.findByName(PREPARE_SANDBOX_TASK_NAME) as PrepareSandboxTask).getConfigDirectory()
            })
            conventionMapping.map("pluginsDirectory", {
                (project.tasks.findByName(PREPARE_SANDBOX_TASK_NAME) as PrepareSandboxTask).getDestinationDir()
            })
            conventionMapping.map("systemDirectory", {
                project.file("$extension.sandboxDirectory/system")
            })
            dependsOn(PREPARE_SANDBOX_TASK_NAME)
        }
    }
}
