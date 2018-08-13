package org.stepik.api.queries

enum class Order {
    ASC,
    DESC;
    
    override fun toString(): String {
        return name.toLowerCase()
    }
}
