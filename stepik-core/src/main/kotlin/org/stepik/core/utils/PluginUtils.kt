package org.stepik.core.utils

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.extensions.PluginId


private const val DEFAULT_PLUGIN_VERSION = "unknown"
private var currentProduct: Product? = null
private var currentProductVersion: String? = null
private var currentProductGroup: ProductGroup? = null

fun version(pluginId: String): String {
    val myPluginId = PluginId.getId(pluginId)
    val plugin = PluginManager.getPlugin(myPluginId)
    return if (plugin != null) plugin.version else DEFAULT_PLUGIN_VERSION
}

fun getCurrentProduct(): Product {
    if (currentProduct == null) {
        val name = ApplicationInfo.getInstance().versionName
        currentProduct = Product.of(name)
    }

    return currentProduct!!
}

fun getCurrentProductVersion(): String {
    if (currentProductVersion == null) {
        currentProductVersion = ApplicationInfo.getInstance().build.toString()
    }

    return currentProductVersion!!
}

fun ProductGroup.isCurrent(): Boolean {
    if (currentProductGroup == null) {
        currentProductGroup = ProductGroup.of(getCurrentProduct())
    }

    return currentProductGroup == this
}
