package org.stepik.core

import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.users.User

interface PluginSettings {
    
    val pluginId: String
    
    val pluginName: String
    
    val host: String
    
    val clientId: String
    
    fun currentUser(stepikApiClient: StepikApiClient): User
}
