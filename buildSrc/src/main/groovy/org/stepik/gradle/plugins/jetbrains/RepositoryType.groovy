package org.stepik.gradle.plugins.jetbrains

import org.jetbrains.annotations.NotNull

/**
 * @author meanmail
 */
enum RepositoryType {
    MAVEN, IVY, DIRECTORY

    static RepositoryType fromString(@NotNull String name) {
        return valueOf(name.toUpperCase())
    }
}