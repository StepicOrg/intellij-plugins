package org.stepik.api.objects

import com.google.gson.annotations.SerializedName

data class Meta(
        var page: Int = 1,
        
        @SerializedName("has_next")
        var hasNext: Boolean = false,
        
        @SerializedName("has_previous")
        var hasPrevious: Boolean = false
)
