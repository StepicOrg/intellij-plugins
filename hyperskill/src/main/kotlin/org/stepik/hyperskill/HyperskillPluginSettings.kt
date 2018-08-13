package org.stepik.hyperskill

import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.users.User
import org.stepik.core.PluginSettings

// Don't rename to Hyperskill!
class HyperskillPluginSettings : PluginSettings {
    
    override val pluginId = "org.stepik.alt"
    
    override val pluginName = "ALT_plugin"
    
    override val host = "https://hyperskill.org"
    
    override val clientId = "32R5uvxasVvHR9CVqhYQ9FEUVoc79DmrX7cGNsoV"
    
    override fun currentUser(stepikApiClient: StepikApiClient): User {
        val user = stepikApiClient.hsUsers()
                .get()
                .id(0)
                .execute()
                .first()
        user.isGuest = false
        return user
    }
}
