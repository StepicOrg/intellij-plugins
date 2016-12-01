package org.stepik.gradle.plugins.jetbrains.dependency;

import groovy.transform.ToString;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * @author meanmail
 */
@ToString(includeNames = true, includeFields = true, ignoreNulls = true)
public class ProductDependency implements Serializable {
    @NotNull
    private final String version;
    @NotNull
    private final String buildNumber;
    @NotNull
    private final File classes;
    @NotNull
    private final Collection<File> jarFiles = collectJarFiles();
    private final boolean withKotlin;

    ProductDependency(
            @NotNull String version,
            @NotNull String buildNumber,
            @NotNull File classes,
            boolean withKotlin) {
        this.version = version;
        this.buildNumber = buildNumber;
        this.classes = classes;
        this.withKotlin = withKotlin;
    }

    @NotNull
    private Collection<File> collectJarFiles() {
        File lib = new File(classes, "lib");
        if (lib.exists()) {
            File[] jars = lib.listFiles(file -> file.getName().toLowerCase().endsWith(".jar"));

            if (jars != null) {
                return Arrays.asList(jars);
            }
        }

        return Collections.emptyList();
    }

    @NotNull
    String getVersion() {
        return version;
    }

    @NotNull
    public String getBuildNumber() {
        return buildNumber;
    }

    @NotNull
    public File getClasses() {
        return classes;
    }

    @NotNull
    Collection<File> getJarFiles() {
        return jarFiles;
    }

    @NotNull
    String getFqn(@NotNull String productName) {
        String fqn = productName + version;
        if (withKotlin) {
            fqn += "-withKotlin";
        }

        return fqn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ProductDependency that = (ProductDependency) o;

        if (withKotlin != that.withKotlin) return false;
        if (!version.equals(that.version)) return false;
        if (!buildNumber.equals(that.buildNumber)) return false;
        //noinspection SimplifiableIfStatement
        if (!classes.equals(that.classes)) return false;
        return jarFiles.equals(that.jarFiles);
    }

    @Override
    public int hashCode() {
        int result = version.hashCode();
        result = 31 * result + buildNumber.hashCode();
        result = 31 * result + classes.hashCode();
        result = 31 * result + jarFiles.hashCode();
        result = 31 * result + (withKotlin ? 1 : 0);
        return result;
    }
}
