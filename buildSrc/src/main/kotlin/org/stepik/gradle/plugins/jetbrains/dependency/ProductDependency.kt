package org.stepik.gradle.plugins.jetbrains.dependency

import groovy.transform.ToString
import org.gradle.api.artifacts.Dependency
import java.io.File
import java.io.Serializable


@ToString(includeNames = true, includeFields = true, ignoreNulls = true)
class ProductDependency(val productName: String, val version: String, val buildNumber: String,
                        val classes: File, val withKotlin: Boolean) : Serializable {

    val jarFiles by lazy {
        if (classes.isDirectory) {
            return@lazy jarsSet(File(classes, "lib")).union(
                    jarsSet(File(classes, "plugins")))
        }
        return@lazy emptySet<File>()
    }

    private fun jarsSet(path: File): Collection<File> {
        return path.walkTopDown().filter {
            return@filter it.name.endsWith(".jar")
                    && (withKotlin || it.name !in listOf("kotlin-runtime.jar", "kotlin-reflect.jar"))
        }.toList()
    }

    fun getFqn(productName: String): String {
        val fqn = productName + version
        if (withKotlin) {
            return "$fqn-withKotlin"
        }

        return fqn
    }

    fun contentEquals(dependency: Dependency): Boolean {
        return dependency.group == "com.jetbrains" && dependency.name == productName && dependency.version == version
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ProductDependency

        if (productName != other.productName) return false
        if (version != other.version) return false
        if (buildNumber != other.buildNumber) return false
        if (classes != other.classes) return false
        if (withKotlin != other.withKotlin) return false
        if (jarFiles != other.jarFiles) return false

        return true
    }

    override fun hashCode(): Int {
        var result = productName.hashCode()
        result = 31 * result + version.hashCode()
        result = 31 * result + buildNumber.hashCode()
        result = 31 * result + classes.hashCode()
        result = 31 * result + withKotlin.hashCode()
        result = 31 * result + jarFiles.hashCode()
        return result
    }
}
