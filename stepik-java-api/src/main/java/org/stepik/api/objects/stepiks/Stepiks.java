package org.stepik.api.objects.stepiks;

import org.stepik.api.objects.users.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Stepiks {
    private List<Stepik> stepics;
    private List<User> users;
    private List<Profile> profiles;

    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public User getUser() {
        if (users != null && users.size() > 0) {
            return users.get(0);
        }

        return new User();
    }

    public List<Stepik> getStepics() {
        if (stepics == null) {
            stepics = new ArrayList<>();
        }
        return stepics;
    }

    public void setStepics(List<Stepik> stepics) {
        this.stepics = stepics;
    }

    public List<Profile> getProfiles() {
        if (profiles == null) {
            profiles = new ArrayList<>();
        }
        return profiles;
    }

    public void setProfiles(List<Profile> profiles) {
        this.profiles = profiles;
    }
}
