package com.jetbrains.tmp.learning.courseFormat;

import org.jetbrains.annotations.NotNull;

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
}
