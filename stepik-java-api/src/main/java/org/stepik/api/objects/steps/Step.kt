package org.stepik.api.objects.steps

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyStringAdapter
import org.stepik.api.client.serialization.DefaultAsEmptyStringArrayAdapter
import org.stepik.api.client.serialization.DefaultAsEmptyStringStringMapAdapter
import org.stepik.api.objects.StudyObjectWithDiscussions
import java.util.Collections.emptyList
import java.util.Collections.emptyMap

data class Step(
        var lesson: Int = 0,
        
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var status: String = "",
        
        var block: BlockView? = null,
        
        @JsonAdapter(DefaultAsEmptyStringStringMapAdapter::class, nullSafe = false)
        var actions: MutableMap<String, String> = emptyMap(),
        
        @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
        var subscriptions: List<String> = emptyList(),
        
        var instruction: Int = 0,
        
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var session: String = "",
        
        @SerializedName("instruction_type")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var instructionType: String? = "",
        
        @SerializedName("viewed_by")
        var viewedBy: Int = 0,
        
        @SerializedName("passed_by")
        var passedBy: Int = 0,
        
        @SerializedName("correct_ratio")
        var correctRatio: Double? = null,
        
        var worth: Int? = null,
        
        @SerializedName("is_solutions_unlocked")
        var isSolutionsUnlocked: Boolean = false,
        
        @SerializedName("solutions_unlocked_attempts")
        var solutionsUnlockedAttempts: Int = 3,
        
        @SerializedName("has_submissions_restrictions")
        var isHasSubmissionsRestrictions: Boolean = false,
        
        @SerializedName("max_submissions_count")
        var maxSubmissionsCount: Int = 3,
        
        var variation: Int = 1,
        
        @SerializedName("variations_count")
        var variationsCount: Int = 1
) : StudyObjectWithDiscussions() {
    
    override var title: String
        get() = "step$position"
        set(value) {}
    
    override var description: String
        get() = "$title in /lesson/$lesson"
        set(value) {}
    
}
