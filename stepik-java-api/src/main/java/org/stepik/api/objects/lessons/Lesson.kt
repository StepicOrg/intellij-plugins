package org.stepik.api.objects.lessons

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import org.stepik.api.client.serialization.DefaultAsEmptyStringAdapter
import org.stepik.api.client.serialization.DefaultAsEmptyStringArrayAdapter
import org.stepik.api.client.serialization.DefaultAsEmptyStringStringMapAdapter
import org.stepik.api.objects.StudyObjectWithDiscussions
import org.stepik.api.urls.Urls


data class Lesson(
        @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
        var steps: List<Long> = emptyList(),

        @JsonAdapter(DefaultAsEmptyStringStringMapAdapter::class, nullSafe = false)
        var actions: Map<String, String> = emptyMap(),

        @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
        var tags: List<Int> = emptyList(),

        @SerializedName("required_tags")
        @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
        var requiredTags: List<Int> = emptyList(),

        @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
        var playlists: List<Any> = emptyList(),

        @SerializedName("is_prime")
        var isPrime: Boolean = false,

        @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
        var subscriptions: List<String> = emptyList(),

        @SerializedName("viewed_by")
        var viewedBy: Int = 0,

        @SerializedName("passed_by")
        var passedBy: Int = 0,

        @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
        var dependencies: List<String> = emptyList(),

        @JsonAdapter(DefaultAsEmptyStringArrayAdapter::class, nullSafe = false)
        var followers: List<String> = emptyList(),

        @SerializedName("time_to_complete")
        var timeToComplete: Int = 0,

        @SerializedName("cover_url")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var coverUrl: String = "",

        @SerializedName("is_comments_enabled")
        var isCommentsEnabled: Boolean = true,

        var owner: Int = 0,

        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var language: String = "",

        @SerializedName("is_featured")
        var isFeatured: Boolean = false,

        @SerializedName("is_public")
        var isPublic: Boolean = false,

        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var slug: String = "",

        @SerializedName("learners_group")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var learnersGroup: String = "",

        @SerializedName("testers_group")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var testersGroup: String = "",

        @SerializedName("moderators_group")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var moderatorsGroup: String = "",

        @SerializedName("teachers_group")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var teachersGroup: String = "",

        @SerializedName("admins_group")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var adminsGroup: String = "",

        @SerializedName("epic_count")
        var epicCount: Int = 0,

        @SerializedName("abuse_count")
        var abuseCount: Int = 0,

        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var vote: String = "",

        @SerializedName("lti_consumer_key")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var ltiConsumerKey: String = "",

        @SerializedName("lti_secret_key")
        @JsonAdapter(DefaultAsEmptyStringAdapter::class, nullSafe = false)
        var ltiSecretKey: String = ""
) : StudyObjectWithDiscussions() {
    override var description: String
        get() = "Lesson ${Urls.STEPIK_URL}/lesson/$id"
        set(value) {}

}
