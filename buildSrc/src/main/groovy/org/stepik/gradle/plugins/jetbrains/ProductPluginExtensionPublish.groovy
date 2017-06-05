package org.stepik.gradle.plugins.jetbrains

class ProductPluginExtensionPublish {
    String username
    String password
    String[] channels

    def username(String username) {
        this.username = username
    }

    def password(String password) {
        this.password = password
    }

    def setChannel(String channel) {
        this.channels = [channel]
    }

    def channel(String channel) {
        channels(channel)
    }

    def channels(String... channels) {
        this.channels = channels
    }

    def setChannels(String... channels) {
        this.channels = channels
    }
}
