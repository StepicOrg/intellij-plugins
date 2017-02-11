package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.Steps;

import java.util.ArrayList;
import java.util.List;

import static com.jetbrains.tmp.learning.stepik.StepikConnectorLogin.authAndGetStepikApiClient;

public class LessonNode extends Node<StepNode, CompoundUnitLesson> {
    private static final Logger logger = Logger.getInstance(LessonNode.class);
    private List<StepNode> stepNodes;
    private CompoundUnitLesson data;
    private long courseId;

    public LessonNode() {
    }

    public LessonNode(@NotNull final SectionNode parent, @NotNull CompoundUnitLesson data) {
        super(parent, data);
    }

    protected void init(@Nullable StudyNode parent, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

            if (indicator != null) {
                indicator.setText("Refresh a lesson: " + getName());
                indicator.setText2("Update steps");
            }

            courseId = 0;

            List<Long> stepsIds = getData().getLesson().getSteps();

            if (stepsIds.size() > 0) {
                Steps steps = stepikApiClient.steps()
                        .get()
                        .id(stepsIds)
                        .execute();

                for (Step step : steps.getSteps()) {
                    StepNode stepNode = getChildById(step.getId());
                    if (stepNode != null) {
                        stepNode.setData(step);
                    } else {
                        StepNode item = new StepNode(this, step);
                        if (item.getType() == StepType.CODE) {
                            getStepNodes().add(item);
                        }
                    }
                }

                clearMapNodes();
                sortChildren();
            }
        } catch (StepikClientException logged) {
            logger.warn("A lesson initialization don't is fully", logged);
        }

        setParent(parent);

        for (StepNode stepNode : getStepNodes()) {
            stepNode.init(this, isRestarted, indicator);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return getData().getLesson().getTitle();
    }

    @NotNull
    public List<StepNode> getStepNodes() {
        if (stepNodes == null) {
            stepNodes = new ArrayList<>();
        }
        return stepNodes;
    }

    @SuppressWarnings("unused")
    public void setStepNodes(@Nullable List<StepNode> stepNodes) {
        this.stepNodes = stepNodes;
        sortChildren();
        clearMapNodes();
    }

    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (StepNode stepNode : getStepNodes()) {
            if (stepNode.getStatus() != StudyStatus.SOLVED) {
                return StudyStatus.UNCHECKED;
            }
        }
        return StudyStatus.SOLVED;
    }

    public long getId() {
        return getData().getLesson().getId();
    }

    public void setId(long id) {
        getData().getLesson().setId(id);
    }

    @Override
    public long getCourseId() {
        StudyNode parent = getParent();
        if (parent != null) {
            return parent.getCourseId();
        }

        if (courseId != 0) {
            return courseId;
        }

        int sectionId = getData().getUnit().getSection();
        if (sectionId == 0) {
            return 0;
        }

        try {
            StepikApiClient stepikApiClient = authAndGetStepikApiClient();

            Sections sections = stepikApiClient.sections()
                    .get()
                    .id(sectionId)
                    .execute();
            if (sections.isEmpty()) {
                return 0;
            }
            courseId = sections.getItems().get(0).getCourse();
            return courseId;
        } catch (StepikClientException ignored) {
        }
        return 0;
    }

    public int getPosition() {
        return getData().getUnit().getPosition();
    }

    @NotNull
    @Override
    public String toString() {
        return "LessonNode {id=" + getId() + ", name='" + getName() + "\'}";
    }

    @Override
    public List<StepNode> getChildren() {
        return getStepNodes();
    }

    @NotNull
    @Override
    public CompoundUnitLesson getData() {
        if (data == null) {
            data = new CompoundUnitLesson();
        }
        return data;
    }

    @Override
    public void setData(@Nullable CompoundUnitLesson data) {
        this.data = data;
    }

    @NotNull
    @Override
    String getDirectoryPrefix() {
        return EduNames.LESSON;
    }
}
