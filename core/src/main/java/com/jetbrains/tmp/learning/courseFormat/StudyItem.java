package com.jetbrains.tmp.learning.courseFormat;

import org.jetbrains.annotations.NotNull;

public interface StudyItem {
    String getName();

    void setName(String name);

    int getIndex();

    void setIndex(int index);

    StudyStatus getStatus();

    @NotNull
    String getDirectory();
}
