package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.steps.BlockView;
import org.stepik.api.objects.steps.BlockViewOptions;
import org.stepik.api.objects.steps.Limit;
import org.stepik.api.objects.steps.Sample;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Step implements StudyItem {
    @Nullable
    private StudyStatus status;
    @Nullable
    private Lesson lesson;
    @Nullable
    private Map<String, StepFile> stepFiles;
    @Nullable
    private Map<SupportedLanguages, Limit> timeLimits;
    @Nullable
    private List<SupportedLanguages> supportedLanguages;
    @Nullable
    private SupportedLanguages currentLang;
    private StepType type;
    private org.stepik.api.objects.steps.Step data;

    public Step() {}

    public Step(org.stepik.api.objects.steps.Step data) {
        this.data = data;

        setStatus(StudyStatus.UNCHECKED);

        BlockView block = data.getBlock();

        setType(StepType.of(block.getName()));
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
                stepFile.setStep(this);

                stepFiles.put(language.getMainFileName(), stepFile);
            });

            setSupportedLanguages(languages);
            setStepFiles(stepFiles);
            Map<SupportedLanguages, Limit> limits = new HashMap<>();
            options.getLimits().entrySet()
                    .forEach(entry -> limits.put(SupportedLanguages.langOf(entry.getKey()), entry.getValue()));
            setTimeLimits(limits);
        }
    }

    void initStep(@Nullable final Lesson lesson, boolean isRestarted) {
        setLesson(lesson);
        if (!isRestarted) {
            status = StudyStatus.UNCHECKED;
        }
        for (StepFile stepFile : getStepFiles().values()) {
            stepFile.initStepFile(this);
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
    public Lesson getLesson() {
        return lesson;
    }

    @Transient
    public void setLesson(@Nullable Lesson lesson) {
        this.lesson = lesson;
    }

    @Transient
    @Nullable
    public Course getCourse() {
        if (lesson == null) {
            return null;
        }
        return lesson.getCourse();
    }

    @Transient
    @Override
    public int getId() {
        return getData().getId();
    }

    @Transient
    public void setId(int id) {
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

    @NotNull
    @Override
    public String getDirectory() {
        return EduNames.STEP + getId();
    }

    @NotNull
    @Override
    public String getPath() {
        if (lesson != null) {
            return lesson.getPath() + "/" + getDirectory();
        } else {
            return getDirectory();
        }
    }

    @Transient
    public int getPosition() {
        return getData().getPosition();
    }

    @Transient
    public void setPosition(int position) {
        getData().setPosition(position);
    }

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public Map<SupportedLanguages, Limit> getTimeLimits() {
        if (timeLimits == null) {
            timeLimits = new HashMap<>();
        }
        return timeLimits;
    }

    public void setTimeLimits(@Nullable Map<SupportedLanguages, Limit> timeLimits) {
        this.timeLimits = timeLimits;
    }

    @Transient
    public Limit getLimits() {
        return getTimeLimits().getOrDefault(getCurrentLang(), new Limit());
    }

    @NotNull
    public List<SupportedLanguages> getSupportedLanguages() {
        if (supportedLanguages == null) {
            supportedLanguages = new ArrayList<>();
        }
        return supportedLanguages;
    }

    @SuppressWarnings("unused")
    public void setSupportedLanguages(@NotNull List<SupportedLanguages> supportedLanguages) {
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

    @NotNull
    private SupportedLanguages getFirstSupportLang() {
        List<SupportedLanguages> languages = getSupportedLanguages();
        if (languages.isEmpty()) {
            return SupportedLanguages.INVALID;
        } else {
            return languages.get(0);
        }
    }

    public void addLanguages(@NotNull List<SupportedLanguages> languages) {
        getSupportedLanguages().addAll(languages);
    }

    public StepType getType() {
        return type;
    }

    public void setType(StepType type) {
        this.type = type;
    }

    public org.stepik.api.objects.steps.Step getData() {
        if (data == null) {
            data = new org.stepik.api.objects.steps.Step();
        }
        return data;
    }

    public void setData(org.stepik.api.objects.steps.Step data) {
        this.data = data;
    }

    public List<Sample> getSamples() {
        if (type == StepType.CODE) {
            return getData().getBlock().getOptions().getSamples();
        }
        return new ArrayList<>();
    }
}
