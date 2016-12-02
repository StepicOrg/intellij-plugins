package org.stepik.gradle.plugins.jetbrains.dependency

import groovy.transform.ToString
import org.jetbrains.annotations.NotNull

/**
 * @author meanmail
 */
@ToString(includeNames = true, includeFields = true, ignoreNulls = true)
class ProductDependency implements Serializable {
    @NotNull
    private final String version
    @NotNull
    private final String buildNumber
    @NotNull
    private final File classes
    @NotNull
    private final Collection<File> jarFiles
    private final boolean withKotlin

    ProductDependency(
            @NotNull String version,
            @NotNull String buildNumber,
            @NotNull File classes,
            boolean withKotlin) {
        this.version = version
        this.buildNumber = buildNumber
        this.classes = classes
        this.withKotlin = withKotlin
        this.jarFiles = collectJarFiles()
    }

    @NotNull
    private Collection<File> collectJarFiles() {
        if (classes.isDirectory()) {
            println classes
            File lib = new File(classes, "lib")
            def jars = new ArrayList();

            if (lib.isDirectory()) {
                lib.eachFile {
                    if (withKotlin || "kotlin-runtime.jar" != it.name && "kotlin-reflect.jar" != it.name) {
                        jars.add(it)
                    }
                }
            }
        }
        return Collections.emptySet()
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

    @NotNull
    Collection<File> getJarFiles() {
        return jarFiles
    }

    @NotNull
    String getFqn(@NotNull String productName) {
        String fqn = productName + version
        if (withKotlin) {
            fqn += "-withKotlin"
        }

        return fqn
    }

    @Override
    boolean equals(Object o) {
        if (this == o) return true
        if (o == null || getClass() != o.getClass()) return false

        ProductDependency that = (ProductDependency) o

        if (withKotlin != that.withKotlin) return false
        if (version != that.version) return false
        if (buildNumber != that.buildNumber) return false
        //noinspection SimplifiableIfStatement
        if (classes != that.classes) return false
        return jarFiles == that.jarFiles
    }

    @Override
    int hashCode() {
        int result = version.hashCode()
        result = 31 * result + buildNumber.hashCode()
        result = 31 * result + classes.hashCode()
        result = 31 * result + jarFiles.hashCode()
        result = 31 * result + (withKotlin ? 1 : 0)
        return result
    }
}
