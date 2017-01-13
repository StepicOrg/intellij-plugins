package org.stepik.api.objects.users;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Users extends ObjectsContainer<User> {
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
    public List<User> getItems() {
        return getUsers();
    }

    @NotNull
    @Override
    public Class<User> getItemClass() {
        return User.class;
    }
}
