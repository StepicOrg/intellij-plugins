package org.stepik.api.queries.lessons;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.lessons.Lessons;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikLessonsGetQuery extends StepikAbstractGetQuery<StepikLessonsGetQuery, Lessons> {
    public StepikLessonsGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Lessons.class);
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
    public StepikLessonsGetQuery language(@NotNull String value) {
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

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.LESSONS;
    }
}
