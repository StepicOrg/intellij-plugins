package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class Lesson implements StudyItem {
    @Transient
    public List<Integer> steps;
    @Transient
    public List<String> tags;
    @Transient
    boolean is_public;
    @Transient
    int position;
    @Transient
    private Section section = null;

    @Expose
    @SerializedName("id")
    private int myId;

    @Expose
    @SerializedName("title")
    private String name;

    @Expose
    @SerializedName("task_list")
    private List<Task> taskList = new ArrayList<>();

    // index is visible to user number of lesson from 1 to lesson number
    private int myIndex = -1;
    @Transient
    @NotNull
    private String directory = "";
    @Transient
    private String path;

    public Lesson() {
    }

    public void initLesson(final Section section, boolean isRestarted) {
        setSection(section);
        for (Task task : taskList) {
            task.initTask(this, isRestarted);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return myIndex;
    }

    public void setIndex(int index) {
        myIndex = index;
        directory = EduNames.LESSON + myIndex;
        updatePath();
    }

    @Override
    public void updatePath() {
        if (path == null) {
            return;
        }

        path = null;

        taskList.forEach(StudyItem::updatePath);
    }

    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        if (taskList == null) {
            this.taskList = new ArrayList<>();
        } else {
            this.taskList = taskList;
        }
    }

    public void addTask(@NotNull final Task task) {
        taskList.add(task);
    }

    public Task getTask(@NotNull final String name) {
        int index = EduUtils.getIndex(name, EduNames.TASK) - 1;
        List<Task> tasks = getTaskList();
        if (!EduUtils.indexIsValid(index, tasks)) {
            return null;
        }
        for (Task task : tasks) {
            if (task.getIndex() - 1 == index) {
                return task;
            }
        }
        return null;
    }

    @Override
    public StudyStatus getStatus() {
        for (Task task : taskList) {
            if (task.getStatus() != StudyStatus.SOLVED) {
                return StudyStatus.UNCHECKED;
            }
        }
        return StudyStatus.SOLVED;
    }

    @NotNull
    @Override
    public String getDirectory() {
        return directory;
    }

    @NotNull
    @Override
    public String getPath() {
        if (path == null) {
            path = section.getPath() + "/" + getDirectory();
        }
        return path;
    }

    public int getId() {
        return myId;
    }

    public void setId(int id) {
        this.myId = id;
    }

    @Transient
    public Section getSection() {
        return section;
    }

    @Transient
    public void setSection(Section section) {
        this.section = section;
    }
}
