package org.stepik.api.objects.users;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Users extends ObjectsContainer {
    private List<User> users;

    @NotNull
    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    @NotNull
    @Override
    protected List getItems() {
        return getUsers();
    }
}
