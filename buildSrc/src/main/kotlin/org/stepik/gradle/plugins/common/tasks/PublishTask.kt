package org.stepik.gradle.plugins.common.tasks

import com.intellij.structure.plugin.PluginCreationFail
import com.intellij.structure.plugin.PluginCreationSuccess
import com.intellij.structure.plugin.PluginManager
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.tasks.bundling.Zip
import org.jetbrains.intellij.pluginRepository.PluginRepositoryInstance
import org.slf4j.LoggerFactory
import org.stepik.gradle.plugins.common.BasePlugin
import org.stepik.gradle.plugins.common.ProductPluginExtension
import java.io.File

open class PublishTask : ConventionTask() {
    private val logger = LoggerFactory.getLogger(BasePlugin::class.java)

    var extension: ProductPluginExtension? = null

    var plugin: BasePlugin? = null

    @get:Input
    var host = "https://plugins.jetbrains.com"

    init {
        enabled = !project.gradle.startParameter.isOffline
    }

    @InputFile
    fun getDistributionFile(): File? {
        val buildPluginTaskName = plugin?.buildPluginTaskName ?: return null
        val buildPluginTask = project.tasks.findByName(buildPluginTaskName) as Zip
        val distributionFile = buildPluginTask.archivePath
        return if (distributionFile.exists()) distributionFile else null
    }

    @Input
    fun getUsername(): String {
        return extension?.publish?.username ?: ""
    }

    @Input
    fun getPassword(): String {
        return extension?.publish?.password ?: ""
    }

    @Input
    @Optional
    fun getChannels(): Array<String> {
        return extension?.publish?.channels ?: emptyArray()
    }

    @TaskAction
    fun publishPlugin() {
        var channels = getChannels()
        if (channels.isEmpty()) {
            channels = arrayOf("")
        }

        val distributionFile = getDistributionFile()
                ?: throw TaskExecutionException(this, Exception("Distribution file is not found"))
        val pluginCreationResult = PluginManager.getInstance().createPlugin(distributionFile)
        if (pluginCreationResult is PluginCreationFail) {
            logger.warn("Don't create plugin: $pluginCreationResult")
            return
        }

        if (pluginCreationResult is PluginCreationSuccess) {
            val plugin = pluginCreationResult.plugin
            val pluginId = plugin.pluginId
            for (channel in channels) {
                logger.info("Uploading plugin $pluginId from ${distributionFile.absolutePath} to $host, channel: $channel")
                try {
                    val repoClient = PluginRepositoryInstance(host, getUsername(), getPassword())
                    repoClient.uploadPlugin(pluginId, distributionFile, channel)
                    logger.info("Uploaded successfully")
                } catch (exception: Exception) {
                    throw TaskExecutionException(this, RuntimeException("Failed to upload plugin", exception))
                }
            }
        }
    }
}
