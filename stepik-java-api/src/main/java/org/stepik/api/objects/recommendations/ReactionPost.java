package org.stepik.api.objects.recommendations;

/**
 * @author meanmail
 */
public class ReactionPost {
    private int reaction;
    private String lesson;
    private String user;

    public int getReaction() {
        return reaction;
    }

    public void setReaction(int reaction) {
        this.reaction = reaction;
    }

    public long getLesson() {
        if (lesson == null) {
            lesson = "0";
        }
        try {
            return Long.valueOf(lesson);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setLesson(long lesson) {
        this.lesson = String.valueOf(lesson);
    }

    public long getUser() {
        if (user == null) {
            user = "0";
        }
        try {
            return Long.valueOf(user);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public void setUser(long user) {
        this.user = String.valueOf(user);
    }
}
