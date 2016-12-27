package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StudyItem {
    @NotNull
    String getName();

    void setName(@Nullable String name);

    int getPosition();

    void setPosition(int position);

    @NotNull
    StudyStatus getStatus();

    @NotNull
    String getDirectory();

    @NotNull
    String getPath();

    void updatePath();

    int getId();

    void setId(int id);

    @Nullable
    @Transient
    Course getCourse();
}
