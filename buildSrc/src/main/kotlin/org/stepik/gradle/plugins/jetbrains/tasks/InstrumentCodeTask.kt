package org.stepik.gradle.plugins.jetbrains.tasks

import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.gradle.tooling.BuildException
import org.stepik.gradle.plugins.jetbrains.dependency.ProductDependency
import java.io.File

open class InstrumentCodeTask : ConventionTask() {
    companion object {
        private const val FILTER_ANNOTATION_REGEXP_CLASS = "com.intellij.ant.ClassFilterAnnotationRegexp"
        private const val LOADER_REF = "java2.loader"
    }

    @Internal
    var sourceSet: SourceSet? = null

    @Input
    var ideaDependency: ProductDependency? = null

    @OutputDirectory
    var outputDir: File? = null

    @InputFiles
    fun getSourceDirs(): FileCollection {
        val sourceSet = sourceSet ?: return project.files()
        return project.files(sourceSet.allSource.srcDirs.find {
            it !in sourceSet.resources && it.exists()
        })
    }

    @TaskAction
    fun instrumentClasses() {
        val ideaDependency = ideaDependency ?: return
        val classpath = project.files(
                "${ideaDependency.classes}/lib/javac2.jar",
                "${ideaDependency.classes}/lib/jdom.jar",
                "${ideaDependency.classes}/lib/asm-all.jar",
                "${ideaDependency.classes}/lib/jgoodies-forms.jar")

        ant.properties.putAll(mapOf(
                "name" to "instrumentIdeaExtensions",
                "classpath" to classpath.asPath,
                "loaderref" to LOADER_REF,
                "classname" to "com.intellij.ant.InstrumentIdeaExtensions"
        ))

        logger.info("Compiling forms and instrumenting code with nullability preconditions")
        val instrumentNotNull = prepareNotNullInstrumenting(classpath)
        val outputDir = outputDir ?: return
        instrumentCode(getSourceDirs(), outputDir, instrumentNotNull)
    }

    private fun prepareNotNullInstrumenting(classpath: ConfigurableFileCollection): Boolean {
        try {
            ant.properties.putAll(mapOf(
                    "name" to "skip",
                    "classpath" to classpath.asPath,
                    "loaderref" to LOADER_REF,
                    "classname" to FILTER_ANNOTATION_REGEXP_CLASS))
        } catch (e: BuildException) {
            val cause = e.cause
            if (cause is ClassNotFoundException && cause.message == FILTER_ANNOTATION_REGEXP_CLASS) {
                logger.info("Old version of Javac2 is used, " +
                        "instrumenting code with nullability will be skipped. Use IDEA >14 SDK (139.*) to fix this")
                return false
            } else {
                throw e
            }
        }
        return true
    }

    private fun instrumentCode(srcDirs: FileCollection, outputDir: File, instrumentNotNull: Boolean) {
        val headlessOldValue = System.setProperty("java.awt.headless", "true")
//        ant.instrumentIdeaExtensions.(mapOf(
//                "srcdir" to srcDirs.asPath,
//                "destdir" to outputDir,
//                "classpath" to sourceSet.compileClasspath.asPath,
//                "includeantruntime" to false,
//                "instrumentNotNull" to instrumentNotNull)) {
//            if (instrumentNotNull) {
//                ant.skip(mapOf("pattern" to "kotlin/Metadata"))
//            }
//        }
        if (headlessOldValue != null) {
            System.setProperty("java.awt.headless", headlessOldValue)
        } else {
            System.clearProperty("java.awt.headless")
        }
    }
}
