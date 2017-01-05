package org.stepik.api.queries.courses;

import com.sun.istack.internal.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.courses.Courses;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

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

    @NotNull
    public StepikCoursesGetQuery page(int page) {
        addParam("page", page);
        return this;
    }

    @NotNull
    public StepikCoursesGetQuery isFeatured(boolean value) {
        addParam("is_featured", value);
        return this;
    }

    @NotNull
    public StepikCoursesGetQuery tag(int value) {
        addParam("tag", value);
        return this;
    }

    @NotNull
    public StepikCoursesGetQuery language(String value) {
        addParam("language", value);
        return this;
    }

    @NotNull
    public StepikCoursesGetQuery owner(int value) {
        addParam("owner", value);
        return this;
    }

    @NotNull
    public StepikCoursesGetQuery isIdeaCompatible(boolean value) {
        addParam("is_idea_compatible", value);
        return this;
    }

    @NotNull
    public StepikCoursesGetQuery enrolled(boolean value) {
        addParam("enrolled", value);
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.COURSES;
    }
}
