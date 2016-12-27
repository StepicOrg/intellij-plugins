package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class Lesson implements StudyItem {
    @Transient
    @Nullable
    private List<Integer> steps;
    @Transient
    @Nullable
    private Section section;
    @Expose
    private int position = -1;
    @Expose
    private int id;
    @Expose
    @Nullable
    @SerializedName("title")
    private String name;
    @Expose
    @Nullable
    @SerializedName("step_list")
    private List<Step> stepList;
    @Transient
    @Nullable
    private String directory;
    @Transient
    @Nullable
    private String path;

    public Lesson() {
    }

    void initLesson(@Nullable final Section section, boolean isRestarted) {
        setSection(section);
        for (Step step : getStepList()) {
            step.initStep(this, isRestarted);
        }
    }

    @NotNull
    @Override
    public String getName() {
        if (name == null) {
            name = "";
        }
        return name;
    }

    @Override
    public void setName(@Nullable String name) {
        this.name = name;
    }

    @Override
    public void updatePath() {
        path = null;

        getStepList().forEach(StudyItem::updatePath);
    }

    @NotNull
    public List<Step> getStepList() {
        if (stepList == null) {
            stepList = new ArrayList<>();
        }
        return stepList;
    }

    @SuppressWarnings("unused")
    public void setStepList(@Nullable List<Step> stepList) {
        this.stepList = stepList;
    }

    public void addStep(@NotNull final Step step) {
        getStepList().add(step);
    }

    @Nullable
    public Step getStep(@NotNull final String name) {
        int id = EduUtils.parseDirName(name, EduNames.STEP);
        for (Step step : getStepList()) {
            if (step.getId() == id) {
                return step;
            }
        }
        return null;
    }

    @NotNull
    @Override
    public StudyStatus getStatus() {
        for (Step step : getStepList()) {
            if (step.getStatus() != StudyStatus.SOLVED) {
                return StudyStatus.UNCHECKED;
            }
        }
        return StudyStatus.SOLVED;
    }

    @NotNull
    @Override
    public String getDirectory() {
        if (directory == null) {
            directory = EduNames.LESSON + id;
            updatePath();
        }
        return directory;
    }

    @NotNull
    @Override
    public String getPath() {
        if (path == null) {
            if (section != null) {
                path = section.getPath() + "/" + getDirectory();
            } else {
                path = getDirectory();
            }
        }
        return path;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        directory = null;
        updatePath();
    }

    @Nullable
    @Override
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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    @Override
    public String toString() {
        return "Lesson {id=" + id + ", name='" + name + "\'}";
    }

    @NotNull
    public List<Integer> getSteps() {
        if (steps == null) {
            steps = new ArrayList<>();
        }
        return steps;
    }

    @SuppressWarnings("unused")
    public void setSteps(@Nullable ArrayList<Integer> steps) {
        this.steps = steps;
    }

    @Transient
    @Nullable
    public Step getLastStep() {
        int stepsCount = getStepList().size();
        if (stepsCount == 0) {
            return null;
        }
        return getStepList().get(stepsCount - 1);
    }

    @Transient
    @Nullable
    public Step getFirstStep() {
        List<Step> children = getStepList();
        if (children.size() == 0) {
            return null;
        }

        return children.get(0);
    }

    @Transient
    @Nullable
    public Step getPrevStep(@NotNull Step step) {
        int position = step.getPosition();
        List<Step> children = getStepList();
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
        for (Step item : getStepList()) {
            if (item.getPosition() > position) {
                return item;
            }
        }
        return null;
    }
}
