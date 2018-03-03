package org.stepik.gradle.plugins.pycharm

import org.stepik.gradle.plugins.common.tasks.BaseRunTask


open class RunPyCharmTask : BaseRunTask() {

    override fun getLibs() = arrayOf(
            "bootstrap.jar",
            "extensions.jar",
            "util.jar",
            "trove4j.jar",
            "jdom.jar",
            "log4j.jar",
            "jna.jar"
    )

    override val platformPrefix = "PyCharmCore"
}
