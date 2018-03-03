package org.stepik.gradle.plugins.idea

import org.stepik.gradle.plugins.jetbrains.tasks.BaseRunTask


open class RunIdeaTask : BaseRunTask() {

    override fun getLibs() = arrayOf(
            "idea_rt.jar",
            "idea.jar",
            "bootstrap.jar",
            "extensions.jar",
            "util.jar",
            "openapi.jar",
            "trove4j.jar",
            "jdom.jar",
            "log4j.jar"
    )

    override val platformPrefix: String?
        get() {
            if (extension?.productType == "IC") {
                return "Idea"
            }
            return null
        }
}
