package org.stepik.api.objects.recommendations;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.AbstractObject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author meanmail
 */
public class Recommendation extends AbstractObject {
    private long lesson;
    private List<String> reasons;

    public long getLesson() {
        return lesson;
    }

    public void setLesson(long lesson) {
        this.lesson = lesson;
    }

    @NotNull
    public List<String> getReasons() {
        if (reasons == null) {
            reasons = new ArrayList<>();
        }
        return reasons;
    }

    public void setReasons(@Nullable List<String> reasons) {
        this.reasons = reasons;
    }
}
