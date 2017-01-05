package org.stepik.api.objects.users;

import org.stepik.api.objects.ObjectsContainer;

import java.util.List;

/**
 * @author meanmail
 */
public class Users extends ObjectsContainer{
    private List<User> users;

    public int getCount() {
        if (users == null) {
            return 0;
        }

        return users.size();
    }

    public List<User> getUsers() {
        return users;
    }
}
