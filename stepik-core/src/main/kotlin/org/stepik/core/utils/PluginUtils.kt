package org.stepik.core.utils

import com.intellij.ide.plugins.PluginManager
import com.intellij.openapi.application.ApplicationInfo
import com.intellij.openapi.extensions.PluginId


object PluginUtils {
    const val PLUGIN_ID = "org.stepik.plugin.union"
    private const val DEFAULT_PLUGIN_VERSION = "unknown"
    private var currentProduct: Product? = null
    private var currentProductVersion: String? = null
    private var currentProductGroup: ProductGroup? = null

    val version: String
        get() {
            val pluginId = PluginId.getId(PLUGIN_ID)
            val plugin = PluginManager.getPlugin(pluginId)
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

    fun isCurrent(group: ProductGroup): Boolean {
        if (currentProductGroup == null) {
            currentProductGroup = ProductGroup.of(getCurrentProduct())
        }

        return currentProductGroup == group
    }
}
