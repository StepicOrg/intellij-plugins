package org.stepik.api.objects.stepiks;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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

    @NotNull
    public List<User> getUsers() {
        if (users == null) {
            users = new ArrayList<>();
        }
        return users;
    }

    public void setUsers(@Nullable List<User> users) {
        this.users = users;
    }

    @NotNull
    public User getUser() {
        if (users != null && users.size() > 0) {
            return users.get(0);
        }

        return new User();
    }

    @NotNull
    public List<Stepik> getStepics() {
        if (stepics == null) {
            stepics = new ArrayList<>();
        }
        return stepics;
    }

    public void setStepics(@Nullable List<Stepik> stepics) {
        this.stepics = stepics;
    }

    @NotNull
    public List<Profile> getProfiles() {
        if (profiles == null) {
            profiles = new ArrayList<>();
        }
        return profiles;
    }

    public void setProfiles(@Nullable List<Profile> profiles) {
        this.profiles = profiles;
    }
}
