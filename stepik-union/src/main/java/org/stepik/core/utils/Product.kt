package org.stepik.core.utils


enum class Product(val product_name: String) {
    UNKNOWN_PRODUCT("Unknown"), IDEA_CE("IntelliJ IDEA Community Edition"), IDEA_ULTIMATE("IntelliJ IDEA"),
    PYCHARM_CE("PyCharm Community Edition"), PYCHARM_PRO("PyCharm");

    override fun toString(): String {
        return product_name
    }

    companion object {

        private val map: Map<String, Product> by lazy {
            values().map { Pair(it.product_name, it) }.toMap()
        }

        fun of(product_name: String?): Product {
            return map[product_name] ?: UNKNOWN_PRODUCT
        }
    }
}
