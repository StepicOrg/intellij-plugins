package org.stepik.api.objects.auth

import com.google.gson.annotations.SerializedName

class TokenInfo {
    @SerializedName("access_token")
    var accessToken: String? = null
    @SerializedName("token_type")
    var tokenType: String? = null
    @SerializedName("expires_in")
    var expiresIn: Int = 0
    @SerializedName("refresh_token")
    var refreshToken: String? = null
    var scope: String? = null
}
