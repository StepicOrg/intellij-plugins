package org.stepik.api.objects.users;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.ObjectsContainer;

import java.util.ArrayList;
import java.util.List;


public class HSUsers extends ObjectsContainer<HSUser> {
    private List<HSUser> users;

    @NotNull
    public List<HSUser> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    @NotNull
    @Override
    public List<HSUser> getItems() {
        return getUsers();
    }

    @NotNull
    @Override
    public Class<HSUser> getItemClass() {
        return HSUser.class;
    }
}
