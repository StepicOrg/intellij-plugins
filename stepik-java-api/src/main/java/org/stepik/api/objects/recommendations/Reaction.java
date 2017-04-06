package org.stepik.api.objects.recommendations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class Reaction {
    private int reaction;
    private long lesson;
    private long user;
    private String time;

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }

    public long getLesson() {
        return lesson;
    }

    public void setLesson(long lesson) {
        this.lesson = lesson;
    }

    public long getUser() {
        return user;
    }

    public void setUser(long user) {
        this.user = user;
    }

    @NotNull
    public String getTime() {
        if (time == null) {
            time = "";
        }
        return time;
    }

    public void setTime(@Nullable String time) {
        this.time = time;
    }
}
