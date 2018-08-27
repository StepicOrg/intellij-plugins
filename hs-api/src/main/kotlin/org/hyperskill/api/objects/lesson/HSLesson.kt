package org.hyperskill.api.objects.lesson

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyStringAdapter
import org.stepik.api.objects.StudyObject
import org.stepik.api.objects.users.HSUser

data class HSLesson(
        @SerializedName("time_to_complete")
        var timeToComplete: Int = 0,
        
        var owner: HSUser = HSUser(),
        
        @SerializedName("epic_count")
        var epicCount: Int = 0,
        
        @SerializedName("abuse_count")
        var abuseCount: Int = 0,
        
        @SerializedName("vote_id")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var voteId: String = "",
        
        @SerializedName("stepik_id")
        var stepikId: Long = 0,
        
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var type: String = "",
        
        var mode: Int = 0,
        
        @SerializedName("is_recent")
        var isRecent: Boolean = false,
        
        @SerializedName("is_passed")
        var isPassed: Boolean = false

) : StudyObject() {
    
    override var description: String
        get() = "Lesson /learn/lesson/$id"
        set(value) {}
    
}
