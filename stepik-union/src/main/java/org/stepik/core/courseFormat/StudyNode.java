package org.stepik.core.courseFormat;

import com.intellij.openapi.project.Project;
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

    void init(@NotNull Project project, @Nullable final StudyNode parent);

    default void init(@NotNull Project project) {
        init(project, null);
    }

    boolean canBeLeaf();

    void reloadData(@NotNull Project project);

    boolean getWasDeleted();

    void setWasDeleted(boolean wasDeleted);

    boolean isUnknownStatus();

    void setRawStatus(@Nullable StudyStatus status);

    void passed();

    @Nullable
    Project getProject();

    void setProject(@NotNull Project project);
}
