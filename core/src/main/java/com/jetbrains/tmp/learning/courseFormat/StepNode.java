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
import java.util.List;
import java.util.Map;

import static com.jetbrains.tmp.learning.SupportedLanguages.INVALID;

public class StepNode implements StudyNode {
    private StudyStatus status;
    private LessonNode lessonNode;
    private List<SupportedLanguages> supportedLanguages;
    private SupportedLanguages currentLang;
    private Step data;

    public StepNode() {}

    public StepNode(@NotNull final LessonNode lessonNode, @NotNull Step data) {
        this.data = data;
        init(lessonNode, true, null);
    }

    void init(@NotNull final LessonNode lessonNode, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        if (indicator != null) {
            indicator.setText("Refresh a step: " + getName());
            indicator.setText2("");
        }

        supportedLanguages = null;
        setLessonNode(lessonNode);

        if (isRestarted) {
            status = StudyStatus.UNCHECKED;
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

    @Transient
    @NotNull
    public String getTemplate(@NotNull SupportedLanguages language) {
        Map<String, String> templates = getData().getBlock().getOptions().getCodeTemplates();
        return templates.getOrDefault(language.getName(), "");
    }

    @Transient
    @NotNull
    public String getCurrentTemplate() {
        return getTemplate(getCurrentLang());
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
    @Override
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

    @NotNull
    @Transient
    private Map<String, Limit> getLimits() {
        return getData().getBlock().getOptions().getLimits();
    }

    @NotNull
    @Transient
    public Limit getLimit() {
        return getLimits().getOrDefault(getCurrentLang().getName(), new Limit());
    }

    @Transient
    @NotNull
    public List<SupportedLanguages> getSupportedLanguages() {
        if (supportedLanguages == null) {
            supportedLanguages = new ArrayList<>();

            BlockView block = data.getBlock();

            if (getType() == StepType.CODE) {
                BlockViewOptions options = block.getOptions();

                Map<String, String> templates = options.getCodeTemplates();
                templates.keySet().forEach(key -> {
                    SupportedLanguages language = SupportedLanguages.langOf(key);

                    if (language != INVALID && !supportedLanguages.contains(language)) {
                        supportedLanguages.add(language);
                    }
                });
            }
        }
        return supportedLanguages;
    }

    @SuppressWarnings("unused")
    public void setSupportedLanguages(@Nullable List<SupportedLanguages> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    @NotNull
    public SupportedLanguages getCurrentLang() {
        List<SupportedLanguages> languages = getSupportedLanguages();
        if (currentLang == null || currentLang == INVALID || !languages.contains(currentLang)) {
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
            return INVALID;
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
    StepType getType() {
        return StepType.of(data.getBlock().getName());
    }

    public boolean isStepFile(@NotNull String fileName) {
        return getCurrentLang().getMainFileName().equals(fileName);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepNode stepNode = (StepNode) o;

        if (status != stepNode.status) return false;
        //noinspection SimplifiableIfStatement
        if (currentLang != stepNode.currentLang) return false;
        return data != null ? data.equals(stepNode.data) : stepNode.data == null;
    }

    @Override
    public int hashCode() {
        int result = status != null ? status.hashCode() : 0;
        result = 31 * result + (currentLang != null ? currentLang.hashCode() : 0);
        result = 31 * result + (data != null ? data.hashCode() : 0);
        return result;
    }
}
