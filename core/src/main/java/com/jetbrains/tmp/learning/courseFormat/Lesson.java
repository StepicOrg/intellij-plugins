package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.objects.steps.Steps;
import org.stepik.api.objects.units.Unit;

import java.util.ArrayList;
import java.util.List;

public class Lesson implements StudyItem {
    @Nullable
    private Section section;
    @Nullable
    private List<Step> steps;
    private org.stepik.api.objects.lessons.Lesson data;
    private Unit unit;

    public Lesson() {
    }

    public Lesson(@NotNull org.stepik.api.objects.lessons.Lesson data, Unit unit) {
        this.data = data;
        this.unit = unit;

        StepikApiClient stepikApiClient = StepikConnectorLogin.getStepikApiClient();

        List<Integer> stepsIds = data.getSteps();
        Steps steps = stepikApiClient.steps()
                .get()
                .id(stepsIds)
                .execute();

        ArrayList<Step> stepList = new ArrayList<>();
        for (org.stepik.api.objects.steps.Step step : steps.getSteps()) {
            Step item = new Step(step);
            if (item.getType() == StepType.CODE) {
                stepList.add(item);
            }
        }

        setSteps(stepList);
    }

    void initLesson(@Nullable final Section section, boolean isRestarted) {
        setSection(section);
        for (Step step : getSteps()) {
            step.initStep(this, isRestarted);
        }
    }

    @NotNull
    @Override
    public String getName() {
        return getData().getTitle();
    }

    @NotNull
    public List<Step> getSteps() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        return steps;
    }

    @SuppressWarnings("unused")
    public void setSteps(@Nullable List<Step> steps) {
        this.steps = steps;
    }

    public void addStep(@NotNull final Step step) {
        getSteps().add(step);
    }

    @Nullable
    public Step getStep(@NotNull final String name) {
        int id = EduUtils.parseDirName(name, EduNames.STEP);
        for (Step step : getSteps()) {
            if (step.getId() == id) {
                return step;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (Step step : getSteps()) {
            if (step.getStatus() != StudyStatus.SOLVED) {
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
        if (section != null) {
            return section.getPath() + "/" + getDirectory();
        } else {
            return getDirectory();
        }
    }

    @Transient
    public int getId() {
        return getData().getId();
    }

    @Transient
    public void setId(int id) {
        getData().setId(id);
    }

    @Nullable
    @Transient
    public Course getCourse() {
        if (section == null) {
            return null;
        }
        return section.getCourse();
    }

    @Nullable
    @Transient
    public Section getSection() {
        return section;
    }

    @Transient
    public void setSection(@Nullable Section section) {
        this.section = section;
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
        return "Lesson {id=" + getId() + ", name='" + getName() + "\'}";
    }

    @Transient
    @Nullable
    public Step getLastStep() {
        int stepsCount = getSteps().size();
        if (stepsCount == 0) {
            return null;
        }
        return getSteps().get(stepsCount - 1);
    }

    @Transient
    @Nullable
    public Step getFirstStep() {
        List<Step> children = getSteps();
        if (children.size() == 0) {
            return null;
        }

        return children.get(0);
    }

    @Transient
    @Nullable
    public Step getPrevStep(@NotNull Step step) {
        int position = step.getPosition();
        List<Step> children = getSteps();
        for (int i = children.size() - 1; i >= 0; i--) {
            Step item = children.get(i);
            if (item.getPosition() < position) {
                return item;
            }
        }
        return null;
    }

    @Transient
    @Nullable
    public Step getNextStep(@NotNull Step lesson) {
        int position = lesson.getPosition();
        for (Step item : getSteps()) {
            if (item.getPosition() > position) {
                return item;
            }
        }
        return null;
    }

    public org.stepik.api.objects.lessons.Lesson getData() {
        if (data == null) {
            data = new org.stepik.api.objects.lessons.Lesson();
        }
        return data;
    }

    public void setData(org.stepik.api.objects.lessons.Lesson data) {
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
