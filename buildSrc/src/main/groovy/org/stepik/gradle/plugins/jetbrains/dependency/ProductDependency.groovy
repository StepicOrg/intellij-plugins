package org.stepik.gradle.plugins.jetbrains.dependency

import groovy.transform.ToString
import org.gradle.api.artifacts.Dependency
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
    private final String productName

    ProductDependency(
            @NotNull String productName,
            @NotNull String version,
            @NotNull String buildNumber,
            @NotNull File classes,
            boolean withKotlin) {
        this.productName = productName
        this.version = version
        this.buildNumber = buildNumber
        this.classes = classes
        this.withKotlin = withKotlin
        this.jarFiles = collectJarFiles()
    }

    @NotNull
    private Collection<File> collectJarFiles() {
        Set<File> jars
        if (classes.isDirectory()) {
            File path = new File(classes, "lib")
            jars = jarsSet(path)

            path = new File(classes, "plugins")
            jars.addAll(jarsSet(path))
            return jars
        }
        return Collections.emptySet()
    }

    private Collection<File> jarsSet(File path) {
        List<File> list = new ArrayList<>()

        path.eachFileRecurse() { file ->
            if (!file.getName().endsWith(".jar")) {
                return false
            }

            if (withKotlin || !(file.name in ["kotlin-runtime.jar", "kotlin-reflect.jar"])) {
                list.add(file)
            }
        }

        return list
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
        if (o.class == Dependency.class) {
            return contentEquals(o as Dependency)
        }

        if (this.is(o)) return true
        if (o == null || this.class != o.class) return false

        ProductDependency that = o as ProductDependency

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

    boolean contentEquals(Dependency dependency) {
        return dependency.getGroup() == "com.jetbrains" && dependency.getName() == productName && dependency.getVersion() == version
    }
}
