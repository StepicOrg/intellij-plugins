package org.stepik.api.objects.users;

import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Users extends ObjectsContainer {
    private List<User> users;

    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    @Override
    protected List getItems() {
        return getUsers();
    }
}
