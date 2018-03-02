package org.stepik.gradle.plugins.clion

import org.stepik.gradle.plugins.jetbrains.BaseRunTask


open class RunCLionTask : BaseRunTask() {

    override fun getLibs() = arrayOf(
            "bootstrap.jar",
            "extensions.jar",
            "util.jar",
            "trove4j.jar",
            "jdom.jar",
            "log4j.jar",
            "jna.jar"
    )

    override val platformPrefix = "CLion"
}
