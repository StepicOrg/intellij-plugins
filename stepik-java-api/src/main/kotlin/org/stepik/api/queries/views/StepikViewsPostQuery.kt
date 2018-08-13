package org.stepik.api.queries.views

import org.stepik.api.actions.StepikAbstractAction
import org.stepik.api.objects.views.Views
import org.stepik.api.objects.views.ViewsPost
import org.stepik.api.queries.StepikAbstractPostQuery

class StepikViewsPostQuery(stepikAction: StepikAbstractAction) :
        StepikAbstractPostQuery<Views>(stepikAction, Views::class.java) {
    
    private val views = ViewsPost()
    
    fun step(id: Long): StepikViewsPostQuery {
        views.view.step = id
        return this
    }
    
    fun assignment(assignment: Long?): StepikViewsPostQuery {
        views.view.assignment = assignment
        return this
    }
    
    override fun getBody(): String {
        return jsonConverter.toJson(views, false)
    }
    
    override fun getUrl(): String {
        return stepikAction.stepikApiClient.host + "/api/views"
    }
}
