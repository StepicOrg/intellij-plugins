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

    void setStatus(@Nullable StudyStatus status);

    void updateParentStatus();

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

    @Nullable
    C getChildByPosition(int position);

    List<C> getChildren();

    @Nullable
    D getData();

    void setData(@Nullable D data);

    void init(
            @Nullable final StudyNode parent,
            boolean isRestarted,
            @Nullable ProgressIndicator indicator);

    default void init(@Nullable ProgressIndicator indicator) {
        init(null, false, indicator);
    }

    boolean canBeLeaf();

    void reloadData(@NotNull ProgressIndicator indicator);

    boolean getWasDeleted();

    void setWasDeleted(boolean wasDeleted);
}
