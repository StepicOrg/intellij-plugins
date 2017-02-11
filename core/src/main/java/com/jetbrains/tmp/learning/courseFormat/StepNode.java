package com.jetbrains.tmp.learning.courseFormat;

import com.intellij.openapi.progress.ProgressIndicator;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.steps.BlockView;
import org.stepik.api.objects.steps.BlockViewOptions;
import org.stepik.api.objects.steps.Limit;
import org.stepik.api.objects.steps.Sample;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.units.Units;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static com.jetbrains.tmp.learning.SupportedLanguages.INVALID;
import static com.jetbrains.tmp.learning.stepik.StepikConnectorLogin.authAndGetStepikApiClient;

public class StepNode extends Node<StudyNode, Step> {
    private StudyStatus status;
    private List<SupportedLanguages> supportedLanguages;
    private SupportedLanguages currentLang;
    private Step data;
    private long courseId;

    public StepNode() {}

    public StepNode(@NotNull final LessonNode parent, @NotNull Step data) {
        super(parent, data);
    }

    protected void init(@Nullable StudyNode parent, boolean isRestarted, @Nullable ProgressIndicator indicator) {
        if (indicator != null) {
            indicator.setText("Refresh a step: " + getName());
            indicator.setText2("");
        }

        supportedLanguages = null;
        courseId = 0;
        setParent(parent);

        if (isRestarted) {
            status = StudyStatus.UNCHECKED;
        }
    }

    @NotNull
    public String getName() {
        return EduNames.STEP + getData().getPosition();
    }

    @NotNull
    public String getText() {
        return getData().getBlock().getText();
    }

    @NotNull
    public String getTemplate(@NotNull SupportedLanguages language) {
        Map<String, String> templates = getData().getBlock().getOptions().getCodeTemplates();
        return templates.getOrDefault(language.getName(), "");
    }

    @NotNull
    public String getCurrentTemplate() {
        return getTemplate(getCurrentLang());
    }

    @Override
    public List<StudyNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public long getId() {
        return getData().getId();
    }

    public void setId(long id) {
        getData().setId(id);
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

        int lessonId = getData().getLesson();
        if (lessonId == 0) {
            return 0;
        }

        try {
            StepikApiClient stepikApiClient = authAndGetStepikApiClient();

            Units units = stepikApiClient.units()
                    .get()
                    .lesson(lessonId)
                    .execute();
            if (units.isEmpty()) {
                return 0;
            }

            LessonNode lessonNode = new LessonNode();
            lessonNode.getData().setUnit(units.getItems().get(0));

            courseId = lessonNode.getCourseId();
            return courseId;
        } catch (StepikClientException ignored) {
        }
        return 0;
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

    public int getPosition() {
        return getData().getPosition();
    }

    @NotNull
    private Map<String, Limit> getLimits() {
        return getData().getBlock().getOptions().getLimits();
    }

    @NotNull
    public Limit getLimit() {
        return getLimits().getOrDefault(getCurrentLang().getName(), new Limit());
    }

    @NotNull
    public List<SupportedLanguages> getSupportedLanguages() {
        if (supportedLanguages == null) {
            supportedLanguages = new ArrayList<>();

            BlockView block = getData().getBlock();

            if (getType() == StepType.CODE) {
                BlockViewOptions options = block.getOptions();

                Map<String, String> templates = options.getCodeTemplates();
                templates.keySet().forEach(key -> {
                    SupportedLanguages language = SupportedLanguages.langOfName(key);

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

    @NotNull
    private SupportedLanguages getFirstSupportLang() {
        List<SupportedLanguages> languages = getSupportedLanguages();
        if (languages.isEmpty()) {
            return INVALID;
        } else {
            return languages.get(0);
        }
    }

    @NotNull
    @Override
    public Step getData() {
        if (data == null) {
            data = new Step();
        }
        return data;
    }

    @Override
    public void setData(@Nullable Step data) {
        this.data = data;
    }

    @NotNull
    public List<Sample> getSamples() {
        if (StepType.of(getData().getBlock().getName()) == StepType.CODE) {
            return getData().getBlock().getOptions().getSamples();
        }
        return new ArrayList<>();
    }

    StepType getType() {
        return StepType.of(getData().getBlock().getName());
    }

    public boolean isStepFile(@NotNull String fileName) {
        return (EduNames.SRC + "/" + getCurrentLang().getMainFileName()).equals(fileName);
    }

    @NotNull
    @Override
    String getDirectoryPrefix() {
        return EduNames.STEP;
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
