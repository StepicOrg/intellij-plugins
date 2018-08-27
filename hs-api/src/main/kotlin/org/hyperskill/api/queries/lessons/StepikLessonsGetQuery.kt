package org.hyperskill.api.queries.lessons

import org.hyperskill.api.objects.lesson.HSLessons
import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.queries.StepikAbstractGetQuery

class HSLessonsGetQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractGetQuery<HSLessonsGetQuery, HSLessons>(stepikAction, HSLessons::class.java) {
    
    fun page(page: Int): HSLessonsGetQuery {
        addParam("page", page)
        return this
    }
    
    override val url = "${stepikAction.stepikApiClient.host}/api/lessons"
    
}
