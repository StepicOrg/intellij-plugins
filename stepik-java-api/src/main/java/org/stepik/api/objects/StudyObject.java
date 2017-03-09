package org.stepik.api.objects;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author meanmail
 */
public class StudyObject extends AbstractObject {
    @NotNull
    public String getTitle() {
        return "";
    }

    public void setTitle(@Nullable String title) {
    }

    public boolean isAdaptive() {
        return false;
    }

    @NotNull
    public String getDescription() {
        return "";
    }

    public void setDescription(@Nullable String description) {
    }

    public int getPosition() {
        return 0;
    }

    @Nullable
    public String getProgress() {
        return null;
    }
}
