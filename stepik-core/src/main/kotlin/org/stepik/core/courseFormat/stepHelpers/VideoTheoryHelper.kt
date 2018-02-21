package org.stepik.core.courseFormat.stepHelpers

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import org.stepik.api.objects.steps.VideoUrl
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.utils.PluginUtils


class VideoTheoryHelper(project: Project, stepNode: StepNode) : StepHelper(project, stepNode) {
    private var quality: Int = 0
    private var urls: List<VideoUrl>? = null

    val url: String?
        get() {
            if (urls == null) {
                urls = videoUrls
            }
            val urls = urls!!

            if (urls.isEmpty()) {
                return null
            }

            val index = (urls.size - 1 downTo 1).firstOrNull {
                urls[it].quality <= getQuality()
            } ?: 0

            val url = urls[index]
            setQuality(url.quality)
            return url.url
        }

    val videoUrls: List<VideoUrl>
        get() {
            return data.block.video.urls.sortedBy { it.quality }
        }

    fun hasContent(): Boolean {
        return data.block.video.urls.isNotEmpty()
    }

    fun getQuality(): Int {
        if (quality == 0) {
            quality = PropertiesComponent.getInstance()
                    .getValue(VIDEO_QUALITY_PROPERTY_NAME, "0").toInt()
        }

        return quality
    }

    private fun setQuality(quality: Int) {
        this.quality = quality
        PropertiesComponent.getInstance().setValue(VIDEO_QUALITY_PROPERTY_NAME, quality.toString())
    }

    companion object {
        const val VIDEO_QUALITY_PROPERTY_NAME = "${PluginUtils.PLUGIN_ID}.VIDEO_QUALITY"
    }
}
