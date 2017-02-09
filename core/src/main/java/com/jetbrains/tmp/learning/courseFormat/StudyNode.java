package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
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

    @Transient
    @Nullable
    StudyNode getParent();

    @Nullable
    StudyNode getPrevChild(@Nullable StudyNode current);

    @Nullable
    StudyNode getNextChild(@Nullable StudyNode current);

    boolean isLeaf();

    long getCourseId();

    @Nullable
    StudyNode getChildById(long id);
}
