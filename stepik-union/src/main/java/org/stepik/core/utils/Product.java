package org.stepik.core.utils;

import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * @author meanmail
 */
public enum Product {
    UNKNOWN_PRODUCT("Unknown"), IDEA_CE("IntelliJ IDEA Community Edition"), IDEA_ULTIMATE("IntelliJ IDEA"),
    PYCHARM_CE("PyCharm Community Edition"), PYCHARM_PRO("PyCharm");

    private static Map<String, Product> map;
    private final String name;


    Product(String name) {
        this.name = name;
    }

    public static Product of(@Nullable String name) {
        initMap();
        return map.getOrDefault(name, UNKNOWN_PRODUCT);
    }

    private static void initMap() {
        if (map == null) {
            map = new HashMap<>();
            Arrays.stream(values()).forEach(product -> map.put(product.getName(), product));
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getName();
    }
}
