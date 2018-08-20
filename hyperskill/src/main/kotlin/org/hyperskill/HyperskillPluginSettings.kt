package org.hyperskill

import org.stepik.api.client.StepikApiClient
import org.stepik.api.client.hsUsers
import org.stepik.api.objects.users.User
import org.stepik.core.PluginSettings

// Don't rename to Hyperskill!
class HyperskillPluginSettings : PluginSettings {
    
    override val pluginId = "org.stepik.alt"
    
    override val pluginName = "ALT_plugin"
    
    override val host = "https://hyperskill.org"
    
    override val clientId = "32R5uvxasVvHR9CVqhYQ9FEUVoc79DmrX7cGNsoV"
    
    override fun currentUser(stepikApiClient: StepikApiClient): User {
        val hsUser = stepikApiClient.hsUsers()
                .get()
                .id(0)
                .execute()
                .first()
        val user = User()
        user.isGuest = false
        user.id = hsUser.id
        user.setFirstName(hsUser.fullname)
        return user
    }
}
