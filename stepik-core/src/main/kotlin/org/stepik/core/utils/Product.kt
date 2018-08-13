package org.stepik.core.utils

enum class Product(val product_name: String) {
    UNKNOWN_PRODUCT("Unknown"),
    IDEA_CE("IntelliJ IDEA Community Edition"),
    IDEA_ULTIMATE("IntelliJ IDEA"),
    PYCHARM_CE("PyCharm Community Edition"),
    PYCHARM_PRO("PyCharm");
    
    override fun toString() = product_name
    
    companion object {
        
        private val map: Map<String, Product> by lazy {
            values().associateBy { it.product_name }
        }
        
        fun of(product_name: String?) = map.getOrDefault(product_name, UNKNOWN_PRODUCT)
    }
}
