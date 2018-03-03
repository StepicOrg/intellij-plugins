package org.stepik.gradle.plugins.common

class IdeVersion(private vararg val components: Int) {


    val baselineVersion: Int
        get() {
            return this.components[0]
        }

    val build: Int
        get() {
            return components[1]
        }

    companion object {

        private const val BUILD_NUMBER = "__BUILD_NUMBER__"
        private const val STAR = "*"
        private const val SNAPSHOT = "SNAPSHOT"
        private const val FALLBACK_VERSION = "999.SNAPSHOT"
        private const val SNAPSHOT_VALUE = Integer.MAX_VALUE

        fun fromString(version: String): IdeVersion? {
            if (version.isBlank()) {
                return null
            }

            if (BUILD_NUMBER == version || SNAPSHOT == version) {
                val fallback = fromString(FALLBACK_VERSION) ?: return null
                return IdeVersion(*fallback.components)
            }

            var code = version
            val productSeparator = version.lastIndexOf('-') //some products have multiple parts, e.g. "FB-IC-143.157"

            if (productSeparator > 0) {
                code = version.substring(productSeparator + 1)
            }

            val baselineVersionSeparator = code.indexOf('.')

            if (baselineVersionSeparator > 0) {
                val baselineVersionString = code.substring(0, baselineVersionSeparator)
                if (baselineVersionString.isBlank()) {
                    return null
                }

                val components = code.split(".")

                val intComponents = components.map {
                    val comp = parseBuildNumber(it) ?: return null
                    if (comp == SNAPSHOT_VALUE) {
                        return@map null
                    }
                    return@map comp
                }.takeWhile { it != null }.filterNotNull().toIntArray()

                return IdeVersion(*intComponents)

            } else {
                val buildNumber = parseBuildNumber(code) ?: return null

                if (buildNumber <= 2000) {
                    // it's probably a baseline, not a build number
                    return IdeVersion(buildNumber, 0)
                }

                val baselineVersion = getBaseLineForHistoricBuilds(buildNumber)
                return IdeVersion(baselineVersion, buildNumber)
            }
        }

        private fun parseBuildNumber(code: String): Int? {
            if (code == SNAPSHOT || code == STAR || code == BUILD_NUMBER) {
                return SNAPSHOT_VALUE
            }
            return try {
                code.toInt()
            } catch (ignored: NumberFormatException) {
                null
            }
        }

        // See http://www.jetbrains.net/confluence/display/IDEADEV/Build+Number+Ranges for historic build ranges
        private fun getBaseLineForHistoricBuilds(bn: Int): Int {
            return when {
                bn >= 10000 -> 88 // Maia, 9x builds
                bn >= 9500 -> 85 // 8.1 builds
                bn >= 9100 -> 81 // 8.0.x builds
                bn >= 8000 -> 80 // 8.0, including pre-release builds
                bn >= 7500 -> 75 // 7.0.2+
                bn >= 7200 -> 72 // 7.0 final
                bn >= 6900 -> 69 // 7.0 pre-M2
                bn >= 6500 -> 65 // 7.0 pre-M1
                bn >= 6000 -> 60 // 6.0.2+
                bn >= 5000 -> 55 // 6.0 branch, including all 6.0 EAP builds
                bn >= 4000 -> 50 // 5.1 branch
                else -> 40
            }
        }
    }
}
