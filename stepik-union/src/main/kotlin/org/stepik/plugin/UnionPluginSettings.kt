package org.stepik.plugin

import org.stepik.api.client.StepikApiClient
import org.stepik.api.objects.users.User
import org.stepik.core.PluginSettings

class UnionPluginSettings : PluginSettings {
    
    override val pluginId = "org.stepik.plugin.union"
    
    override val pluginName = "S_Union"
    
    override val host = "https://stepik.org"
    
    override val clientId = "IexnxCQMMPkEanIsjlbQM4iFlZeJTqoVSbYP30AB"
    
    override fun currentUser(stepikApiClient: StepikApiClient): User {
        return stepikApiClient.stepiks()
                .currentUser
    }
}
