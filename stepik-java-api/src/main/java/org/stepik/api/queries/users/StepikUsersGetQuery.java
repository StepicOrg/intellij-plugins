package org.stepik.api.queries.users;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.users.Users;
import org.stepik.api.queries.Order;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

/**
 * @author meanmail
 */
public class StepikUsersGetQuery extends StepikAbstractGetQuery<StepikUsersGetQuery, Users> {
    public StepikUsersGetQuery(@NotNull StepikAbstractAction stepikAction) {
        super(stepikAction, Users.class);
    }

    @NotNull
    public StepikUsersGetQuery page(int page) {
        addParam("page", page);
        return this;
    }

    @NotNull
    public StepikUsersGetQuery alias(@NotNull String value) {
        addParam("alias", value);
        return this;
    }

    @NotNull
    public StepikUsersGetQuery order(@NotNull Order value) {
        addParam("order", value.toString());
        return this;
    }

    @NotNull
    @Override
    protected String getUrl() {
        return Urls.USERS;
    }
}
