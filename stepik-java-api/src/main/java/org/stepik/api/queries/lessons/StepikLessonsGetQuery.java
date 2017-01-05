package org.stepik.api.queries.lessons;

import com.sun.istack.internal.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.actions.StepikLessonsAction;
import org.stepik.api.objects.lessons.Lessons;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.queries.courses.StepikCoursesGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikLessonsGetQuery extends StepikAbstractGetQuery<Lessons> {
    public StepikLessonsGetQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Lessons.class);
    }

    public StepikLessonsGetQuery id(Integer... values) {
        addParam("ids[]", values);
        return this;
    }

    @NotNull
    public StepikLessonsGetQuery page(int page) {
        addParam("page", page);
        return this;
    }

    @NotNull
    public StepikLessonsGetQuery isFeatured(boolean value) {
        addParam("is_featured", value);
        return this;
    }

    @NotNull
    public StepikLessonsGetQuery isPrime(boolean value) {
        addParam("is_prime", value);
        return this;
    }

    @NotNull
    public StepikLessonsGetQuery tag(int value) {
        addParam("tag", value);
        return this;
    }

    @NotNull
    public StepikLessonsGetQuery language(String value) {
        addParam("language", value);
        return this;
    }

    @NotNull
    public StepikLessonsGetQuery owner(int value) {
        addParam("owner", value);
        return this;
    }

    @NotNull
    public StepikLessonsGetQuery course(int value) {
        addParam("course", value);
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.LESSONS;
    }
}
