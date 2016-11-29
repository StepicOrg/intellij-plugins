package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.core.EduNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Task implements StudyItem {
    private int myIndex;
    private int position;
    private String text;
    private StudyStatus myStatus = StudyStatus.Unchecked;

    @Transient
    private Lesson myLesson;

    @Expose
    private String name;
    @Expose
    private int stepId;
    @Expose
    @SerializedName("task_files")
    public Map<String, TaskFile> taskFiles = new HashMap<>();
    @Expose
    private Map<String, String> timeLimits = new HashMap<>();
    @Expose
    private Set<String> supportedLanguages = new HashSet<>();
    @Expose
    private String currentLang;
    @Transient
    @NotNull
    private String directory = "";
    @Transient
    private String path;

    public Task() {}

    void initTask(final Lesson lesson, boolean isRestarted) {
        setLesson(lesson);
        if (!isRestarted) myStatus = StudyStatus.Unchecked;
        for (TaskFile taskFile : getTaskFiles().values()) {
            taskFile.initTaskFile(this);
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }

    public int getIndex() {
        return myIndex;
    }

    public void setIndex(int index) {
        myIndex = index;
        directory = EduNames.TASK + myIndex;
        updatePath();
    }

    @Override
    public void updatePath() {
        path = null;
    }

    public Map<String, TaskFile> getTaskFiles() {
        return taskFiles;
    }

    public boolean isTaskFile(@NotNull final String fileName) {
        return taskFiles.get(fileName) != null;
    }

    @Nullable
    public TaskFile getFile(@NotNull final String fileName) {
        return taskFiles.get(fileName);
    }

    @Transient
    public Lesson getLesson() {
        return myLesson;
    }

    @Transient
    public void setLesson(Lesson lesson) {
        myLesson = lesson;
    }

    @Nullable
    public VirtualFile getTaskDir(@NotNull final Project project) {
        VirtualFile courseDir = project.getBaseDir();
        if (courseDir != null) {
            VirtualFile lessonDir = courseDir.findChild(myLesson.getDirectory());
            if (lessonDir != null) {
                return lessonDir.findChild(getDirectory());
            }
        }
        return null;
    }

    @NotNull
    public String getTaskText(@NotNull final Project project) {
        if (!StringUtil.isEmptyOrSpaces(text)) return text;
        final VirtualFile taskDir = getTaskDir(project);
        if (taskDir != null) {
            final VirtualFile file = StudyUtils.findTaskDescriptionVirtualFile(taskDir);
            if (file == null) return "";
            final Document document = FileDocumentManager.getInstance().getDocument(file);
            if (document != null) {
                return document.getImmutableCharSequence().toString();
            }
        }

        return "";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (myIndex != task.myIndex) return false;
        if (name != null ? !name.equals(task.name) : task.name != null) return false;
        if (taskFiles != null ? !taskFiles.equals(task.taskFiles) : task.taskFiles != null) return false;
        return text != null ? !text.equals(task.text) : task.text != null;

    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + myIndex;
        result = 31 * result + (taskFiles != null ? taskFiles.hashCode() : 0);
        result = 31 * result + (text != null ? text.hashCode() : 0);
        return result;
    }

    public void setStepId(int stepId) {
        this.stepId = stepId;
    }

    public int getStepId() {
        return stepId;
    }

    @Override
    public StudyStatus getStatus() {
        return myStatus;
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
            path = myLesson.getPath() + "/" + getDirectory();
        }
        return path;
    }

    public void setStatus(StudyStatus status) {
        myStatus = status;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public String getDescription() {
        return text + getTimeLimit(currentLang);
    }

    public Map<String, String> getTimeLimits() {
        if (timeLimits == null) timeLimits = new HashMap<>();
        return timeLimits;
    }

    public void setTimeLimits(Map<String, String> timeLimits) {
        this.timeLimits = timeLimits;
    }

    @NotNull
    private String getTimeLimit(@NotNull String lang) {
        if (timeLimits == null) return "";
        return timeLimits.getOrDefault(lang, "");
    }

    public void addLang(String lang) {
        supportedLanguages.add(lang);
    }

    public Set<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(Set<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public String getCurrentLang() {
        if (!supportedLanguages.contains(currentLang)) {
            currentLang = getPopularLang();
        }
        return currentLang;
    }

    public void setCurrentLang(String currentLang) {
        this.currentLang = currentLang;
    }

    @Nullable
    private String getPopularLang() {
        for (String lang : supportedLanguages)
            if (supportedLanguages.contains(lang))
                return lang;
        return null;
    }
}
