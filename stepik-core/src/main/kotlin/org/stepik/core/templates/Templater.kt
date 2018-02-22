package org.stepik.core.templates

import freemarker.template.Configuration
import org.stepik.core.common.Loggable
import java.io.ByteArrayOutputStream
import java.io.PrintWriter


object Templater : Loggable {
    private val config by lazy {
        initConfig()
    }

    fun processTemplate(templateName: String, map: Map<String, Any?>): String {
        try {
            ByteArrayOutputStream().use {
                val template = config.getTemplate("$templateName.ftl")
                template.process(map, PrintWriter(it))
                return it.toString()
            }
        } catch (e: Exception) {
            logger.warn(e)
        }

        return ""
    }

    private fun initConfig(): Configuration {
        val config = Configuration(Configuration.VERSION_2_3_27)
        config.setClassLoaderForTemplateLoading(Templater::class.java.classLoader, "/templates")
        return config
    }
}
