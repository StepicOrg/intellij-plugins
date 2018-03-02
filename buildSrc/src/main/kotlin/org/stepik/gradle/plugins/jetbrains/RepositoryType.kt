package org.stepik.gradle.plugins.jetbrains


enum class RepositoryType {
    MAVEN, IVY, DIRECTORY;

    companion object {
        fun fromString(name: String): RepositoryType {
            return valueOf(name.toUpperCase())
        }
    }
}
