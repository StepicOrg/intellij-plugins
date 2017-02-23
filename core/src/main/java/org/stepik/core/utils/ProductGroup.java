package org.stepik.core.utils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public enum ProductGroup {
    UNKNOWN_GROUP, IDEA(Product.IDEA_CE, Product.IDEA_ULTIMATE), PYCHARM(Product.PYCHARM_CE, Product.PYCHARM_PRO);

    private static Map<Product, ProductGroup> map;
    private final Product[] products;

    ProductGroup(Product... products) {
        this.products = products;
    }

    public static ProductGroup of(Product product) {
        initMap();
        return map.getOrDefault(product, UNKNOWN_GROUP);
    }

    private static void initMap() {
        if (map == null) {
            map = new HashMap<>();

            Arrays.stream(values())
                    .forEach(productGroup -> Arrays.stream(productGroup.products)
                            .forEach(product -> map.put(product, productGroup)));
        }
    }

    @Override
    public String toString() {
        return String.format("%s %s", name(), Arrays.toString(products));
    }
}
