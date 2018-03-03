package org.stepik.gradle.plugins.common

import org.stepik.gradle.plugins.common.tasks.BaseRunTask

interface ProductSettings {

    val extensionName: String

    val productName: String

    val productType: String

    val productGroup: String

    val runTaskClass: Class<out BaseRunTask>

    val repositoryType: RepositoryType

    val repositoryTemplate: String

}
