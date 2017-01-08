package com.jetbrains.tmp.learning.courseFormat;

import org.jetbrains.annotations.NotNull;

public interface StudyItem {
    @NotNull
    String getName();

    int getPosition();

    @NotNull
    StudyStatus getStatus();

    @NotNull
    String getDirectory();

    @NotNull
    String getPath();

    int getId();
}
