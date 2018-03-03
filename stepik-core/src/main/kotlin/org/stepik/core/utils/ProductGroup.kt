package org.stepik.core.utils


enum class ProductGroup constructor(vararg val products: Product) {
    UNKNOWN_GROUP,
    IDEA(Product.IDEA_CE, Product.IDEA_ULTIMATE),
    PYCHARM(Product.PYCHARM_CE, Product.PYCHARM_PRO);

    override fun toString() = "$name $products"

    companion object {

        private val map: Map<Product, ProductGroup> by lazy {
            values().map { group ->
                group.products.map { it to group }
            }.flatten().toMap()
        }

        fun of(product: Product) = map.getOrDefault(product, UNKNOWN_GROUP)
    }
}
