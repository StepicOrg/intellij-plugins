package org.stepik.api.queries.users;

import org.stepik.api.actions.StepikAbstractAction;
import org.stepik.api.objects.users.Users;
import org.stepik.api.queries.Order;
import org.stepik.api.queries.StepikAbstractGetQuery;
import org.stepik.api.urls.Urls;

import java.util.List;

/**
 * @author meanmail
 */
public class StepikUsersGetQuery extends StepikAbstractGetQuery<Users> {
    public StepikUsersGetQuery(StepikAbstractAction stepikAction) {
        super(stepikAction, Users.class);
    }

    public StepikUsersGetQuery id(Integer... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikUsersGetQuery id(int... values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikUsersGetQuery id(List<Integer> values) {
        addParam("ids[]", values);
        return this;
    }

    public StepikUsersGetQuery page(int page) {
        addParam("page", page);
        return this;
    }


    public StepikUsersGetQuery alias(String value) {
        addParam("alias", value);
        return this;
    }


    public StepikUsersGetQuery order(Order value) {
        addParam("order", value.toString());
        return this;
    }

    @Override
    protected String getUrl() {
        return Urls.USERS;
    }
}
