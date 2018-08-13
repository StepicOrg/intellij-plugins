package org.stepik.api.auth

enum class GrantTypes {
    
    REFRESH_TOKEN,
    PASSWORD,
    AUTHORIZATION_CODE;
    
    override fun toString(): String {
        return name.toLowerCase()
    }
}
