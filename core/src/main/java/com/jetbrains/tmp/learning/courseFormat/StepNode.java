package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.steps.BlockView;
import org.stepik.api.objects.steps.BlockViewOptions;
import org.stepik.api.objects.steps.Limit;
import org.stepik.api.objects.steps.Sample;
import org.stepik.api.objects.steps.Step;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StepNode implements StudyNode {
    private StudyStatus status;
    private LessonNode lessonNode;
    private Map<String, StepFile> stepFiles;
    private Map<SupportedLanguages, Limit> limits;
    private List<SupportedLanguages> supportedLanguages;
    private SupportedLanguages currentLang;
    private Step data;

    public StepNode() {}

    public StepNode(@NotNull final LessonNode lessonNode, @NotNull Step data) {
        this.data = data;
        init(lessonNode, true, null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepNode stepNode = (StepNode) o;

        if (status != stepNode.status) return false;
        if (lessonNode != null ? !lessonNode.equals(stepNode.lessonNode) : stepNode.lessonNode != null) return false;
        if (stepFiles != null ? !stepFiles.equals(stepNode.stepFiles) : stepNode.stepFiles != null) return false;
        if (limits != null ? !limits.equals(stepNode.limits) : stepNode.limits != null) return false;
        if (supportedLanguages != null ?
                !supportedLanguages.equals(stepNode.supportedLanguages) :
                stepNode.supportedLanguages != null) return false;
        //noinspection SimplifiableIfStatement
        if (currentLang != stepNode.currentLang) return false;
        return data != null ? data.equals(stepNode.data) : stepNode.data == null;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (lessonNode != null ? lessonNode.hashCode() : 0);
        result = 31 * result + (stepFiles != null ? stepFiles.hashCode() : 0);
        result = 31 * result + (limits != null ? limits.hashCode() : 0);
        result = 31 * result + (supportedLanguages != null ? supportedLanguages.hashCode() : 0);
        result = 31 * result + (currentLang != null ? currentLang.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }

    void init(@NotNull final LessonNode lessonNode, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        if (indicator != null) {
            indicator.setText("Refresh a step: " + getName());
            indicator.setText2("");
        }
        BlockView block = data.getBlock();

        if (getType() == StepType.CODE) {
            BlockViewOptions options = block.getOptions();
            List<SupportedLanguages> languages = new ArrayList<>();
            Map<String, StepFile> stepFiles = new HashMap<>();
            Map<String, String> templates = options.getCodeTemplates();
            templates.entrySet().forEach(entry -> {
                SupportedLanguages language = SupportedLanguages.langOf(entry.getKey());
                languages.add(language);

                StepFile stepFile = new StepFile();
                stepFile.setName(language.getMainFileName());
                stepFile.setText(entry.getValue());
                stepFile.setStepNode(this);

                stepFiles.put(language.getMainFileName(), stepFile);
            });

            setSupportedLanguages(languages);
            setStepFiles(stepFiles);
            Map<SupportedLanguages, Limit> limits = new HashMap<>();
            options.getLimits().entrySet()
                    .forEach(entry -> limits.put(SupportedLanguages.langOf(entry.getKey()), entry.getValue()));
            setLimits(limits);
        }

        setLessonNode(lessonNode);

        if (isRestarted) {
            status = StudyStatus.UNCHECKED;
        }

        for (StepFile stepFile : getStepFiles().values()) {
            stepFile.init(this);
        }
    }

    @Transient
    @NotNull
    public String getName() {
        return EduNames.STEP + getData().getPosition();
    }

    @Transient
    @NotNull
    public String getText() {
        return getData().getBlock().getText();
    }

    @NotNull
    public Map<String, StepFile> getStepFiles() {
        if (stepFiles == null) {
            stepFiles = new HashMap<>();
        }
        return stepFiles;
    }

    @SuppressWarnings("unused")
    public void setStepFiles(@Nullable Map<String, StepFile> stepFiles) {
        this.stepFiles = stepFiles;
    }

    @Nullable
    public StepFile getFile(@NotNull final String fileName) {
        return getStepFiles().get(fileName);
    }

    @Nullable
    @Transient
    public LessonNode getLessonNode() {
        return lessonNode;
    }

    public void setLessonNode(@Nullable LessonNode lessonNode) {
        this.lessonNode = lessonNode;
    }

    @Transient
    @Nullable
    public CourseNode getCourse() {
        if (lessonNode == null) {
            return null;
        }
        return lessonNode.getCourse();
    }

    @Transient
    @Override
    public long getId() {
        return getData().getId();
    }

    public void setId(long id) {
        getData().setId(id);
    }

    @Override
    @NotNull
    public StudyStatus getStatus() {
        if (status == null) {
            status = StudyStatus.UNCHECKED;
        }
        return status;
    }

    public void setStatus(@Nullable StudyStatus status) {
        this.status = status;
    }

    @Transient
    @NotNull
    @Override
    public String getDirectory() {
        return EduNames.STEP + getId();
    }

    @Transient
    @NotNull
    @Override
    public String getPath() {
        if (lessonNode != null) {
            return lessonNode.getPath() + "/" + getDirectory();
        } else {
            return getDirectory();
        }
    }

    @Transient
    public int getPosition() {
        return getData().getPosition();
    }

    public void setPosition(int position) {
        getData().setPosition(position);
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public Map<SupportedLanguages, Limit> getLimits() {
        if (limits == null) {
            limits = new HashMap<>();
        }
        return limits;
    }

    @SuppressWarnings({"WeakerAccess", "unused"})
    public void setLimits(@Nullable Map<SupportedLanguages, Limit> limits) {
        this.limits = limits;
    }

    @NotNull
    @Transient
    public Limit getLimit() {
        return getLimits().getOrDefault(getCurrentLang(), new Limit());
    }

    @NotNull
    public List<SupportedLanguages> getSupportedLanguages() {
        if (supportedLanguages == null) {
            supportedLanguages = new ArrayList<>();
        }
        return supportedLanguages;
    }

    @SuppressWarnings("unused")
    public void setSupportedLanguages(@Nullable List<SupportedLanguages> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    @NotNull
    public SupportedLanguages getCurrentLang() {
        if (supportedLanguages == null) {
            currentLang = SupportedLanguages.INVALID;
        } else if (currentLang == null || currentLang == SupportedLanguages.INVALID || !supportedLanguages.contains(
                currentLang)) {
            currentLang = getFirstSupportLang();
        }
        return currentLang;
    }

    public void setCurrentLang(@Nullable SupportedLanguages currentLang) {
        this.currentLang = currentLang;
    }

    @Transient
    @NotNull
    private SupportedLanguages getFirstSupportLang() {
        List<SupportedLanguages> languages = getSupportedLanguages();
        if (languages.isEmpty()) {
            return SupportedLanguages.INVALID;
        } else {
            return languages.get(0);
        }
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public Step getData() {
        if (data == null) {
            data = new Step();
        }
        return data;
    }

    @SuppressWarnings("unused")
    public void setData(@Nullable Step data) {
        this.data = data;
    }

    @Transient
    @NotNull
    public List<Sample> getSamples() {
        if (StepType.of(data.getBlock().getName()) == StepType.CODE) {
            return getData().getBlock().getOptions().getSamples();
        }
        return new ArrayList<>();
    }

    @Transient
    public StepType getType() {
        return StepType.of(data.getBlock().getName());
    }
}
