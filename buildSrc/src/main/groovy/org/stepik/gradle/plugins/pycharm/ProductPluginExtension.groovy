package org.stepik.gradle.plugins.pycharm

import org.stepik.gradle.plugins.pycharm.dependency.ProductDependency

/**
 * Configuration options for the {@link PyCharmPlugin}.
 */
@SuppressWarnings("GroovyUnusedDeclaration")
class ProductPluginExtension {
    String idePath
    String version
    String type
    String pluginName
    String sandboxDirectory
    String repository

    ProductDependency dependency
    private final Map<String, Object> systemProperties = new HashMap<>()

    String getType() {
        return type
    }

    String getVersion() {
        return version.startsWith('CE-') || version.startsWith('IC-')  ? version.substring(3) : version
    }

    Map<String, Object> getSystemProperties() {
        systemProperties
    }

    void setSystemProperties(Map<String, ?> properties) {
        systemProperties.clear()
        systemProperties.putAll(properties)
    }

    ProductPluginExtension systemProperties(Map<String, ?> properties) {
        systemProperties.putAll(properties)
        this
    }

    ProductPluginExtension systemProperty(String name, Object value) {
        systemProperties.put(name, value)
        this
    }
}
