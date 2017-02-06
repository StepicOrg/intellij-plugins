package com.jetbrains.tmp.learning.courseFormat;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface StudyNode {
    @NotNull
    String getName();

    int getPosition();

    @NotNull
    StudyStatus getStatus();

    @NotNull
    String getDirectory();

    @NotNull
    String getPath();

    long getId();

    @Nullable
    CourseNode getCourse();
}
