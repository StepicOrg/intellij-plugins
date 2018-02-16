package org.stepik.core.courseFormat.stepHelpers

import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.project.Project
import org.stepik.api.objects.steps.VideoUrl
import org.stepik.core.courseFormat.StepNode
import org.stepik.core.utils.PluginUtils
import java.util.Collections.emptyList


class VideoTheoryHelper(project: Project, stepNode: StepNode) : StepHelper(project, stepNode) {
    private var quality: Int = 0
    private var urls: List<VideoUrl>? = null

    val url: String?
        get() {
            if (urls == null) {
                urls = videoUrls
            }

            if (urls!!.isEmpty()) {
                return null
            }

            for (i in urls!!.size - 1 downTo 1) {
                if (urls!![i].quality <= getQuality()) {
                    setQuality(urls!![i].quality)
                    return urls!![i].url
                }
            }

            val firstUrl = urls!![0]
            setQuality(firstUrl.quality)
            return firstUrl.url
        }

    val videoUrls: List<VideoUrl>
        get() {
            val data = stepNode.data ?: return emptyList()

            val video = data.block.video
            val urls = video.urls
            if (urls.isEmpty()) {
                return emptyList()
            }

            urls.sortBy { it.quality }
            return urls
        }

    fun hasContent(): Boolean {
        val data = stepNode.data ?: return false

        val video = data.block.video
        val urls = video.urls
        return !urls.isEmpty()
    }

    fun getQuality(): Int {
        if (quality == 0) {
            quality = Integer.parseInt(PropertiesComponent.getInstance()
                    .getValue(VIDEO_QUALITY_PROPERTY_NAME, 0.toString()))
        }

        return quality
    }

    private fun setQuality(quality: Int) {
        this.quality = quality
        PropertiesComponent.getInstance().setValue(VIDEO_QUALITY_PROPERTY_NAME, quality.toString())
    }

    companion object {
        const val VIDEO_QUALITY_PROPERTY_NAME = PluginUtils.PLUGIN_ID + ".VIDEO_QUALITY"
    }
}
