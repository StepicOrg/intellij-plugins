package org.stepik.gradle.plugins.jetbrains

import org.apache.tools.ant.BuildException
import org.gradle.api.Action
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.SourceDirectorySet
import org.gradle.api.tasks.compile.AbstractCompile
import org.jetbrains.annotations.NotNull
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class InstrumentCodeAction implements Action<AbstractCompile> {
    private static final Logger logger = LoggerFactory.getLogger(InstrumentCodeAction)

    private static final String FILTER_ANNOTATION_REGEXP_CLASS = 'com.intellij.ant.ClassFilterAnnotationRegexp'
    private static final LOADER_REF = "java2.loader"
    private final boolean testInstrumentation
    private final String extensionName

    InstrumentCodeAction(boolean testInstrumentation, @NotNull String extensionName) {
        this.testInstrumentation = testInstrumentation
        this.extensionName = extensionName
    }

    @Override
    void execute(AbstractCompile task) {
        def extension = task.project.extensions.getByName(extensionName) as ProductPluginExtension
        if (extension == null || !extension.instrumentCode) {
            return
        }
        def classpath = task.project.files(
                "$extension.dependency.classes/lib/javac2.jar",
                "$extension.dependency.classes/lib/jdom.jar",
                "$extension.dependency.classes/lib/asm-all.jar",
                "$extension.dependency.classes/lib/jgoodies-forms.jar")
        task.project.ant.taskdef(name: 'instrumentIdeaExtensions',
                classpath: classpath.asPath,
                loaderref: LOADER_REF,
                classname: 'com.intellij.ant.InstrumentIdeaExtensions')

        logger.info("Compiling forms and instrumenting code with nullability preconditions")
        //noinspection GroovyAssignabilityCheck
        boolean instrumentNotNull = prepareNotNullInstrumenting(task, classpath)
        def sourceSet = testInstrumentation ?
                Utils.testSourceSet(task.project).compiledBy(task) :
                Utils.mainSourceSet(task.project).compiledBy(task)
        def srcDirs = existingDirs(sourceSet.allSource)
        srcDirs.removeAll(existingDirs(sourceSet.resources))
        if (!srcDirs.empty) {
            instrumentCode(task, srcDirs, instrumentNotNull)
        }
    }

    private static HashSet<File> existingDirs(SourceDirectorySet sourceDirectorySet) {
        return sourceDirectorySet.srcDirs.findAll { it.exists() }
    }

    private static boolean prepareNotNullInstrumenting(@NotNull Task compileTask,
            @NotNull ConfigurableFileCollection classpath) {
        try {
            compileTask.project.ant.typedef(name: 'skip', classpath: classpath.asPath, loaderref: LOADER_REF,
                    classname: FILTER_ANNOTATION_REGEXP_CLASS)
        } catch (BuildException e) {
            def cause = e.getCause()
            if (cause instanceof ClassNotFoundException && FILTER_ANNOTATION_REGEXP_CLASS == cause.getMessage()) {
                logger.info("Old version of Javac2 is used, " +
                        "instrumenting code with nullability will be skipped. Use IDEA >14 SDK (139.*) to fix this")
                return false
            } else {
                throw e
            }
        }
        return true
    }

    private static void instrumentCode(@NotNull AbstractCompile compileTask,
            @NotNull Collection<File> srcDirs,
            boolean instrumentNotNull) {
        def headlessOldValue = System.setProperty('java.awt.headless', 'true')
        compileTask.project.ant.instrumentIdeaExtensions(srcdir: compileTask.project.files(srcDirs).asPath,
                destdir: compileTask.destinationDir, classpath: compileTask.classpath.asPath,
                includeantruntime: false, instrumentNotNull: instrumentNotNull) {
            if (instrumentNotNull) {
                compileTask.project.ant.skip(pattern: 'kotlin/Metadata')
            }
        }
        if (headlessOldValue != null) {
            System.setProperty('java.awt.headless', headlessOldValue)
        } else {
            System.clearProperty('java.awt.headless')
        }
    }
}
