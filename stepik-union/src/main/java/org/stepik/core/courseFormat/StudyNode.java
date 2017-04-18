package org.stepik.core.courseFormat;

import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
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

    long getCourseId(@NotNull StepikApiClient stepikApiClient);

    @Nullable
    C getChildById(long id);

    @Nullable
    StudyNode<?, ?> getChildByClassAndId(@NotNull Class<? extends StudyObject> clazz, long id);

    @Nullable
    C getChildByPosition(int position);

    List<C> getChildren();

    @Nullable
    D getData();

    void setData(@Nullable D data);

    void init(@NotNull Project project, @NotNull StepikApiClient stepikApiClient, @Nullable final StudyNode parent);

    default void init(@NotNull Project project, @NotNull StepikApiClient stepikApiClient) {
        init(project, stepikApiClient, null);
    }

    void reloadData(@NotNull Project project, @NotNull StepikApiClient stepikApiClient);

    boolean getWasDeleted();

    void setWasDeleted(boolean wasDeleted);

    boolean isUnknownStatus();

    void setRawStatus(@Nullable StudyStatus status);

    void resetStatus();

    void passed();

    void setProject(@NotNull Project project);
}
