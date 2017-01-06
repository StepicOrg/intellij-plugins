package org.stepik.api.queries.courses;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.courses.Courses;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

import java.util.List;

/**
 * @author meanmail
 */
public class StepikCoursesGetQuery extends StepikAbstractGetQuery<Courses> {
    public StepikCoursesGetQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Courses.class);
    }

    public StepikCoursesGetQuery id(Integer... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikCoursesGetQuery id(List<Integer> values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikCoursesGetQuery page(int page) {
        addParam("page", page);
        return this;
    }

    public StepikCoursesGetQuery isFeatured(boolean value) {
        addParam("is_featured", value);
        return this;
    }

    public StepikCoursesGetQuery tag(int value) {
        addParam("tag", value);
        return this;
    }

    public StepikCoursesGetQuery language(String value) {
        addParam("language", value);
        return this;
    }

    public StepikCoursesGetQuery owner(int value) {
        addParam("owner", value);
        return this;
    }

    public StepikCoursesGetQuery isIdeaCompatible(boolean value) {
        addParam("is_idea_compatible", value);
        return this;
    }

    public StepikCoursesGetQuery enrolled(boolean value) {
        addParam("enrolled", value);
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.COURSES;
    }
}
