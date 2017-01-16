package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.lessons.Lesson;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.Steps;
import org.stepik.api.objects.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class LessonNode implements StudyNode {
    private static final Logger logger = Logger.getInstance(LessonNode.class);
    private SectionNode sectionNode;
    private List<StepNode> stepNodes;
    private Lesson data;
    private Unit unit;

    public LessonNode() {
    }

    public LessonNode(
            @NotNull final SectionNode sectionNode,
            @NotNull Lesson data,
            @NotNull Unit unit) {
        this.data = data;
        this.unit = unit;
        init(sectionNode, true, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LessonNode that = (LessonNode) o;

        if (sectionNode != null ? !sectionNode.equals(that.sectionNode) : that.sectionNode != null) return false;
        if (stepNodes != null ? !stepNodes.equals(that.stepNodes) : that.stepNodes != null) return false;
        //noinspection SimplifiableIfStatement
        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        return unit != null ? unit.equals(that.unit) : that.unit == null;
    }

    @Override
    public int hashCode() {
        int result = sectionNode != null ? sectionNode.hashCode() : 0;
        result = 31 * result + (stepNodes != null ? stepNodes.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }

    void init(@NotNull final SectionNode sectionNode, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

            if (indicator != null) {
                indicator.setText("Refresh a lesson: " + getName());
                indicator.setText2("Update steps");
            }

            List<Long> stepsIds = data.getSteps();

            if (stepsIds.size() > 0) {
                Steps steps = stepikApiClient.steps()
                        .get()
                        .id(stepsIds)
                        .execute();

                for (Step step : steps.getSteps()) {
                    StepNode stepNode = getStepById(step.getId());
                    if (stepNode != null) {
                        stepNode.setData(step);
                    } else {
                        StepNode item = new StepNode(this, step);
                        if (item.getType() == StepType.CODE) {
                            getStepNodes().add(item);
                        }
                    }
                }
            }
        } catch (StepikClientException logged) {
            logger.warn("A lesson initialization don't is fully", logged);
        }

        setSectionNode(sectionNode);

        for (StepNode stepNode : getStepNodes()) {
            stepNode.init(this, isRestarted, indicator);
        }
    }

    @Nullable
    private StepNode getStepById(long id) {
        for (StepNode stepNode : getStepNodes()) {
            if (stepNode.getId() == id) {
                return stepNode;
            }
        }
        return null;
    }

    @Transient
    @NotNull
    @Override
    public String getName() {
        return getData().getTitle();
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
    }

    public void addStep(@NotNull final StepNode stepNode) {
        getStepNodes().add(stepNode);
    }

    @Nullable
    public StepNode getStep(@NotNull final String name) {
        int id = EduUtils.parseDirName(name, EduNames.STEP);
        for (StepNode stepNode : getStepNodes()) {
            if (stepNode.getId() == id) {
                return stepNode;
            }
        }
        return null;
    }

    @Transient
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

    @Transient
    @NotNull
    @Override
    public String getDirectory() {
        return EduNames.LESSON + getId();
    }

    @Transient
    @NotNull
    @Override
    public String getPath() {
        if (sectionNode != null) {
            return sectionNode.getPath() + "/" + getDirectory();
        } else {
            return getDirectory();
        }
    }

    @Transient
    public long getId() {
        return getData().getId();
    }

    public void setId(long id) {
        getData().setId(id);
    }

    @Nullable
    @Transient
    public CourseNode getCourse() {
        if (sectionNode == null) {
            return null;
        }
        return sectionNode.getCourseNode();
    }

    @Nullable
    @Transient
    public SectionNode getSectionNode() {
        return sectionNode;
    }

    public void setSectionNode(@Nullable SectionNode sectionNode) {
        this.sectionNode = sectionNode;
    }

    @Transient
    public int getPosition() {
        return getUnit().getPosition();
    }

    public void setPosition(int position) {
        getUnit().setPosition(position);
    }

    @NotNull
    @Override
    public String toString() {
        return "LessonNode {id=" + getId() + ", name='" + getName() + "\'}";
    }

    @Transient
    @Nullable
    public StepNode getLastStep() {
        int stepsCount = getStepNodes().size();
        if (stepsCount == 0) {
            return null;
        }
        return getStepNodes().get(stepsCount - 1);
    }

    @Transient
    @Nullable
    public StepNode getFirstStep() {
        List<StepNode> children = getStepNodes();
        if (children.size() == 0) {
            return null;
        }

        return children.get(0);
    }

    @Transient
    @Nullable
    public StepNode getPrevStep(@NotNull StepNode stepNode) {
        int position = stepNode.getPosition();
        List<StepNode> children = getStepNodes();
        for (int i = children.size() - 1; i >= 0; i--) {
            StepNode item = children.get(i);
            if (item.getPosition() < position) {
                return item;
            }
        }
        return null;
    }

    @Transient
    @Nullable
    public StepNode getNextStep(@NotNull StepNode lesson) {
        int position = lesson.getPosition();
        for (StepNode item : getStepNodes()) {
            if (item.getPosition() > position) {
                return item;
            }
        }
        return null;
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public Lesson getData() {
        if (data == null) {
            data = new Lesson();
        }
        return data;
    }

    @SuppressWarnings("unused")
    public void setData(@Nullable Lesson data) {
        this.data = data;
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public Unit getUnit() {
        if (unit == null) {
            unit = new Unit();
        }
        return unit;
    }

    @SuppressWarnings("unused")
    public void setUnit(@Nullable Unit unit) {
        this.unit = unit;
    }
}
