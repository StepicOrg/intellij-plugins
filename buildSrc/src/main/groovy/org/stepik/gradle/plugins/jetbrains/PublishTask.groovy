package org.stepik.gradle.plugins.jetbrains

import com.intellij.structure.plugin.PluginManager
import org.gradle.api.internal.ConventionTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskExecutionException
import org.gradle.api.tasks.bundling.Zip
import org.gradle.util.CollectionUtils
import org.jetbrains.intellij.pluginRepository.PluginRepositoryInstance
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class PublishTask extends ConventionTask {
    private static final Logger logger = LoggerFactory.getLogger(BasePlugin)

    private ProductPluginExtension extension
    private BasePlugin plugin
    private String host = "http://plugins.jetbrains.com"

    PublishTask() {
        enabled = !project.gradle.startParameter.offline
    }

    void setExtension(ProductPluginExtension extension) {
        this.extension = extension
    }

    void setPlugin(BasePlugin plugin) {
        this.plugin = plugin
    }

    @Input
    String getHost() {
        host
    }

    @InputFile
    File getDistributionFile() {
        def buildPluginTask = project.tasks.findByName(plugin.buildPluginTaskName) as Zip
        def distributionFile = buildPluginTask?.archivePath
        distributionFile?.exists() ? distributionFile : null
    }

    void setDistributionFile(Object distributionFile) {
        this.distributionFile = distributionFile
    }

    @Input
    String getUsername() {
        Utils.toString(extension.publish.username)
    }

    @Input
    String getPassword() {
        Utils.toString(extension.publish.password)
    }

    @Input
    @Optional
    String[] getChannels() {
        CollectionUtils.stringize(extension.publish.channels.collect {
            it instanceof Closure ? (it as Closure).call() : it
        }.flatten())
    }

    @SuppressWarnings("GroovyUnusedDeclaration")
    @TaskAction
    protected void publishPlugin() {
        def channels = getChannels()
        if (!channels || channels.length == 0) {
            channels = ['default']
        }

        def host = getHost()
        def distributionFile = getDistributionFile()
        String pluginId = PluginManager.instance.createPlugin(distributionFile).plugin.pluginId
        for (String channel : channels) {
            logger.info("Uploading plugin ${pluginId} from $distributionFile.absolutePath to $host, channel: $channel")
            try {
                def repoClient = new PluginRepositoryInstance(host, getUsername(), getPassword())
                repoClient.uploadPlugin(pluginId, distributionFile, channel && 'default' != channel ? channel : '')
                logger.info("Uploaded successfully")
            } catch (exception) {
                throw new TaskExecutionException(this, new RuntimeException("Failed to upload plugin", exception))
            }
        }
    }
}
