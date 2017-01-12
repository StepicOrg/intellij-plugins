package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.lessons.Lesson;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.Steps;
import org.stepik.api.objects.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class LessonNode implements StudyNode {
    @Nullable
    private SectionNode sectionNode;
    @Nullable
    private List<StepNode> stepNodes;
    private Lesson data;
    private Unit unit;

    public LessonNode() {
    }

    public LessonNode(@NotNull Lesson data, Unit unit) {
        this.data = data;
        this.unit = unit;

        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

        List<Long> stepsIds = data.getSteps();
        Steps steps = stepikApiClient.steps()
                .get()
                .id(stepsIds)
                .execute();

        ArrayList<StepNode> stepNodeList = new ArrayList<>();
        for (Step step : steps.getSteps()) {
            StepNode item = new StepNode(step);
            if (item.getType() == StepType.CODE) {
                stepNodeList.add(item);
            }
        }

        setStepNodes(stepNodeList);
    }

    void initLesson(@Nullable final SectionNode sectionNode, boolean isRestarted) {
        setSectionNode(sectionNode);
        for (StepNode stepNode : getStepNodes()) {
            stepNode.initStep(this, isRestarted);
        }
    }

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

    @NotNull
    @Override
    public String getDirectory() {
        return EduNames.LESSON + getId();
    }

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

    @Transient
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

    @Transient
    public void setSectionNode(@Nullable SectionNode sectionNode) {
        this.sectionNode = sectionNode;
    }

    @Transient
    public int getPosition() {
        return getUnit().getPosition();
    }

    @Transient
    public void setPosition(int position) {
        getUnit().setPosition(position);
    }

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

    public Lesson getData() {
        if (data == null) {
            data = new Lesson();
        }
        return data;
    }

    public void setData(Lesson data) {
        this.data = data;
    }

    public Unit getUnit() {
        if (unit == null) {
            unit = new Unit();
        }
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}
