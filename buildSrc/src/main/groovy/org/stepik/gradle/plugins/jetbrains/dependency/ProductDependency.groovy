package org.stepik.gradle.plugins.jetbrains.dependency

import com.sun.istack.internal.NotNull
import groovy.transform.ToString
import org.gradle.api.Nullable

@ToString(includeNames = true, includeFields = true, ignoreNulls = true)
class ProductDependency implements Serializable {
    @NotNull
    private final String version
    @NotNull
    private final String buildNumber
    @NotNull
    private final File classes
    @Nullable
    private final File sources
    @NotNull
    private final Collection<File> jarFiles
    private final boolean withKotlin

    ProductDependency(@NotNull String version, @NotNull String buildNumber, @NotNull File classes, @Nullable File sources,
                      boolean withKotlin) {
        this.version = version
        this.buildNumber = buildNumber
        this.classes = classes
        this.sources = sources
        this.withKotlin = withKotlin
        this.jarFiles = collectJarFiles()
    }

    protected Collection<File> collectJarFiles() {
        def lib = new File(classes, "lib")
        if (lib.exists()) {
            def collection = new ArrayList<File>()
            lib.eachFile {
                if (it.name.endsWith(".jar")) {
                    collection.add(it)
                }
            }

            return collection
        }
        return Collections.emptyList()
    }

    @NotNull
    String getVersion() {
        return version
    }

    @NotNull
    String getBuildNumber() {
        return buildNumber
    }

    @NotNull
    File getClasses() {
        return classes
    }

    @Nullable
    File getSources() {
        return sources
    }

    @NotNull
    Collection<File> getJarFiles() {
        return jarFiles
    }

    boolean isWithKotlin() {
        return withKotlin
    }

    String getFqn(@NotNull String productName) {
        def fqn = "$productName$version"
        if (withKotlin) {
            fqn += '-withKotlin'
        }
        if (sources) {
            fqn += '-withSources'
        }
        return fqn
    }

    boolean equals(o) {
        if (this.is(o)) return true
        if (!(o instanceof ProductDependency)) return false
        ProductDependency that = (ProductDependency) o
        if (withKotlin != that.withKotlin) return false
        if (buildNumber != that.buildNumber) return false
        if (classes != that.classes) return false
        if (jarFiles != that.jarFiles) return false
        if (sources != that.sources) return false
        if (version != that.version) return false
        return true
    }

    int hashCode() {
        int result
        result = version.hashCode()
        result = 31 * result + buildNumber.hashCode()
        result = 31 * result + classes.hashCode()
        result = 31 * result + (sources != null ? sources.hashCode() : 0)
        result = 31 * result + jarFiles.hashCode()
        result = 31 * result + (withKotlin ? 1 : 0)
        return result
    }
}
