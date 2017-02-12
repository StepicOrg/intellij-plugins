package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.progress.ProgressIndicator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.StudyObject;

import java.util.List;

public interface StudyNode<D extends StudyObject, C extends StudyNode> {
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
    StudyNode getParent();

    void setParent(@Nullable StudyNode parent);

    @Nullable
    StudyNode getPrevChild(@Nullable StudyNode current);

    @Nullable
    StudyNode getNextChild(@Nullable StudyNode current);

    boolean isLeaf();

    long getCourseId();

    @Nullable
    C getChildById(long id);

    List<C> getChildren();

    @NotNull
    D getData() throws IllegalAccessException, InstantiationException;

    void setData(@Nullable D data);

    void init(
            @Nullable final StudyNode parent,
            boolean isRestarted,
            @Nullable ProgressIndicator indicator);

    default void init(boolean isRestarted, @Nullable ProgressIndicator indicator) {
        init(null, isRestarted, indicator);
    }

    boolean canBeLeaf();

    void reloadData(boolean isRestarted, @NotNull ProgressIndicator indicator);
}
