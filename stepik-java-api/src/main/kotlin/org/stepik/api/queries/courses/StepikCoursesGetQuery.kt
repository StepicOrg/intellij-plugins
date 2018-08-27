package org.stepik.api.queries.courses

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.courses.Courses
import org.stepik.api.queries.StepikAbstractGetQuery

class StepikCoursesGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<StepikCoursesGetQuery, Courses>(stepikAction, Courses::class.java) {
    
    fun page(page: Int): StepikCoursesGetQuery {
        addParam("page", page)
        return this
    }
    
    fun isFeatured(value: Boolean): StepikCoursesGetQuery {
        addParam("is_featured", value)
        return this
    }
    
    fun tag(value: Int): StepikCoursesGetQuery {
        addParam("tag", value)
        return this
    }
    
    fun language(value: String): StepikCoursesGetQuery {
        addParam("language", value)
        return this
    }
    
    fun owner(value: Int): StepikCoursesGetQuery {
        addParam("owner", value)
        return this
    }
    
    fun isIdeaCompatible(value: Boolean): StepikCoursesGetQuery {
        addParam("is_idea_compatible", value)
        return this
    }
    
    fun enrolled(value: Boolean): StepikCoursesGetQuery {
        addParam("enrolled", value)
        return this
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/courses"
    
}
