package org.stepik.api.objects.stepiks;

import org.stepik.api.objects.users.User;

import java.util.List;

/**
 * @author meanmail
 */
public class Stepiks {
    private List<Stepik> stepics;
    private List<User> users;
    private List<Profile> profiles;

    public User getUser() {
        if (users!= null && users.size() > 0) {
            return users.get(0);
        }

        return new User();
    }
}
