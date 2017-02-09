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
import org.stepik.api.objects.sections.Sections;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.Steps;
import org.stepik.api.objects.units.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.jetbrains.tmp.learning.stepik.StepikConnectorLogin.authAndGetStepikApiClient;

public class LessonNode extends Node<StepNode> {
    private static final Logger logger = Logger.getInstance(LessonNode.class);
    private List<StepNode> stepNodes;
    private Lesson data;
    private Unit unit;
    private Map<Long, StepNode> mapStepNodes;
    private long courseId;

    public LessonNode() {
    }

    public LessonNode(
            @NotNull final SectionNode parent,
            @NotNull Lesson data,
            @NotNull Unit unit) {
        this.data = data;
        this.unit = unit;
        init(parent, true, null);
    }

    void init(@NotNull final SectionNode parent, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

            if (indicator != null) {
                indicator.setText("Refresh a lesson: " + getName());
                indicator.setText2("Update steps");
            }

            courseId = 0;

            List<Long> stepsIds = getData().getSteps();

            if (stepsIds.size() > 0) {
                Steps steps = stepikApiClient.steps()
                        .get()
                        .id(stepsIds)
                        .execute();

                Map<Long, StepNode> nodeMap = getMapStepNodes();

                for (Step step : steps.getSteps()) {
                    StepNode stepNode = nodeMap.get(step.getId());
                    if (stepNode != null) {
                        stepNode.setData(step);
                    } else {
                        StepNode item = new StepNode(this, step);
                        if (item.getType() == StepType.CODE) {
                            getStepNodes().add(item);
                        }
                    }
                }

                clearNodeMap();
            }
        } catch (StepikClientException logged) {
            logger.warn("A lesson initialization don't is fully", logged);
        }

        setParent(parent);

        for (StepNode stepNode : getStepNodes()) {
            stepNode.init(this, isRestarted, indicator);
        }
    }

    private void clearNodeMap() {
        mapStepNodes = null;
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
        clearNodeMap();
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
    public long getId() {
        return getData().getId();
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

        int sectionId = getUnit().getSection();
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

    public void setId(long id) {
        getData().setId(id);
    }

    @Transient
    public int getPosition() {
        return getUnit().getPosition();
    }

    @NotNull
    @Override
    public String toString() {
        return "LessonNode {id=" + getId() + ", name='" + getName() + "\'}";
    }

    @Override
    protected List<StepNode> getChildren() {
        return getStepNodes();
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

    @SuppressWarnings("unused,WeakerAccess")
    public void setUnit(@Nullable Unit unit) {
        this.unit = unit;
    }

    @Transient
    private Map<Long, StepNode> getMapStepNodes() {
        if (mapStepNodes == null) {
            mapStepNodes = new HashMap<>();
            getStepNodes().forEach(stepNode -> mapStepNodes.put(stepNode.getId(), stepNode));
        }
        return mapStepNodes;
    }

    @Nullable
    StepNode getStepById(long id) {
        return getMapStepNodes().get(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        LessonNode that = (LessonNode) o;

        //noinspection SimplifiableIfStatement
        if (data != null ? !data.equals(that.data) : that.data != null) return false;
        return unit != null ? unit.equals(that.unit) : that.unit == null;
    }

    @Override
    public int hashCode() {
        int result = data != null ? data.hashCode() : 0;
        result = 31 * result + (unit != null ? unit.hashCode() : 0);
        return result;
    }
}
