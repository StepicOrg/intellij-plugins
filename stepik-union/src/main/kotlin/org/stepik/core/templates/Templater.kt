package org.stepik.core.templates

import com.intellij.openapi.diagnostic.Logger
import freemarker.template.Configuration
import freemarker.template.TemplateException
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.PrintWriter


object Templater {
    private val logger = Logger.getInstance(Templater::class.java)
    private var config: Configuration? = null

    fun processTemplate(templateName: String, map: Map<String, Any?>): String {
        if (config == null) {
            config = initConfig()
        }

        try {
            ByteArrayOutputStream().use {
                val template = config!!.getTemplate("$templateName.ftl")
                template.process(map, PrintWriter(it))
                return it.toString()
            }
        } catch (e: TemplateException) {
            logger.warn(e)
        } catch (e: IOException) {
            logger.warn(e)
        }

        return ""
    }

    private fun initConfig(): Configuration {
        val config = Configuration(Configuration.VERSION_2_3_23)
        config.setClassLoaderForTemplateLoading(Templater::class.java.classLoader, "/templates")
        return config
    }
}
