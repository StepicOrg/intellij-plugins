package org.stepik.api.queries.lessons

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.lessons.Lessons
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikLessonsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikLessonsGetQuery, Lessons>(stepikAction, Lessons::class.java) {
    
    fun page(page: Int): StepikLessonsGetQuery {
        addParam("page", page)
        return this
    }
    
    fun isFeatured(value: Boolean): StepikLessonsGetQuery {
        addParam("is_featured", value)
        return this
    }
    
    fun isPrime(value: Boolean): StepikLessonsGetQuery {
        addParam("is_prime", value)
        return this
    }
    
    fun tag(value: Int): StepikLessonsGetQuery {
        addParam("tag", value)
        return this
    }
    
    fun language(value: String): StepikLessonsGetQuery {
        addParam("language", value)
        return this
    }
    
    fun owner(value: Int): StepikLessonsGetQuery {
        addParam("owner", value)
        return this
    }
    
    fun course(value: Int): StepikLessonsGetQuery {
        addParam("course", value)
        return this
    }
    
    override val url: String = "${stepikAction.stepikApiClient.host}/api/lessons"
    
}
