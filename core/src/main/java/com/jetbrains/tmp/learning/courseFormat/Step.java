package com.jetbrains.tmp.learning.courseFormat;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.steps.BlockView;
import org.stepik.api.objects.steps.queezes.code.CodeOptions;
import org.stepik.api.objects.steps.queezes.code.Limit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class Step implements StudyItem {
    @Expose
    private int position;
    @Expose
    @Nullable
    private String text;
    @Expose
    @Nullable
    private StudyStatus status;
    @Transient
    @Nullable
    private Lesson lesson;
    @Expose
    @Nullable
    private String name;
    @Expose
    private int id;
    @Expose
    @SerializedName("task_files")
    @Nullable
    private Map<String, StepFile> stepFiles;
    @Expose
    @Nullable
    private Map<SupportedLanguages, String> timeLimits;
    @Nullable
    @Expose
    private List<SupportedLanguages> supportedLanguages;
    @Nullable
    @Expose
    private SupportedLanguages currentLang;
    @Transient
    @Nullable
    private String directory;
    @Transient
    @Nullable
    private String path;

    public Step() {}

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
        if (name == null) {
            name = "";
        }
        return name;
    }

    @Override
    public void setName(@Nullable String name) {
        this.name = name;
    }

    @NotNull
    public String getText() {
        if (text == null) {
            text = "";
        }
        return text;
    }

    public void setText(@Nullable final String text) {
        this.text = text;
    }

    @Override
    public void updatePath() {
        path = null;
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

    @Nullable
    @Override
    public Course getCourse() {
        if (lesson == null) {
            return null;
        }
        return lesson.getCourse();
    }

    @Override
    public int getId() {
        return id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
        directory = null;
        updatePath();
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
        if (directory == null) {
            directory = EduNames.STEP + id;
            updatePath();
        }
        return directory;
    }

    @NotNull
    @Override
    public String getPath() {
        if (path == null) {
            if (lesson != null) {
                path = lesson.getPath() + "/" + getDirectory();
            } else {
                path = getDirectory();
            }
        }
        return path;
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

    @SuppressWarnings("WeakerAccess")
    @NotNull
    public Map<SupportedLanguages, String> getTimeLimits() {
        if (timeLimits == null) {
            timeLimits = new HashMap<>();
        }
        return timeLimits;
    }

    public void setTimeLimits(@Nullable Map<SupportedLanguages, String> timeLimits) {
        this.timeLimits = timeLimits;
    }

    @NotNull
    private String getTimeLimit(SupportedLanguages lang) {
        return getTimeLimits().getOrDefault(lang, "");
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
        if (currentLang == null || currentLang == SupportedLanguages.INVALID) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Step step = (Step) o;

        if (position != step.position) return false;
        if (id != step.id) return false;
        if (text != null ? !text.equals(step.text) : step.text != null) return false;
        if (status != step.status) return false;
        if (lesson != null ? !lesson.equals(step.lesson) : step.lesson != null) return false;
        if (name != null ? !name.equals(step.name) : step.name != null) return false;
        if (stepFiles != null ? !stepFiles.equals(step.stepFiles) : step.stepFiles != null) return false;
        if (timeLimits != null ? !timeLimits.equals(step.timeLimits) : step.timeLimits != null) return false;
        //noinspection SimplifiableIfStatement
        if (supportedLanguages != null ?
                !supportedLanguages.equals(step.supportedLanguages) :
                step.supportedLanguages != null) return false;
        return currentLang == step.currentLang;
    }

    @Override
    public int hashCode() {
        int result = position;
        result = 31 * result + (text != null ? text.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        result = 31 * result + (lesson != null ? lesson.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + id;
        result = 31 * result + (stepFiles != null ? stepFiles.hashCode() : 0);
        result = 31 * result + (timeLimits != null ? timeLimits.hashCode() : 0);
        result = 31 * result + (supportedLanguages != null ? supportedLanguages.hashCode() : 0);
        result = 31 * result + (currentLang != null ? currentLang.hashCode() : 0);
        return result;
    }

    public void addLanguages(@NotNull List<SupportedLanguages> languages) {
        getSupportedLanguages().addAll(languages);
    }

    static Step fromStep(org.stepik.api.objects.steps.Step step) {
        Step result = new Step();

        result.setId(step.getId());
        result.setName("step" + step.getId());
        result.setPosition(step.getPosition());
        result.setStatus(StudyStatus.of(step.getStatus()));

        BlockView block = step.getBlock();

        if (block.getName().equals("code")) {
            CodeOptions options = block.getOptions(CodeOptions.class);
            List<SupportedLanguages> languages = new ArrayList<>();
            Map<String, StepFile> stepFiles = new HashMap<>();
            Map<String, String> templates = options.getCodeTemplates();
            templates.entrySet().forEach(entry -> {
                SupportedLanguages language = SupportedLanguages.langOf(entry.getKey());
                languages.add(language);

                StepFile stepFile = new StepFile();
                stepFile.setName(language.getMainFileName());
                stepFile.setText(entry.getValue());
                stepFile.setStep(result);

                stepFiles.put(language.getMainFileName(), stepFile);
            });

            result.setSupportedLanguages(languages);
            result.setStepFiles(stepFiles);

            Map<String, Limit> limits = options.getLimits();

            Map<SupportedLanguages, String> strLimits = new HashMap<>();

            limits.entrySet().forEach(entry -> {
                SupportedLanguages language = SupportedLanguages.langOf(entry.getKey());

                int time = entry.getValue().getTime();
                int memory = entry.getValue().getMemory();

                String limit = time + "s; " + memory + "Mib";
                strLimits.put(language, limit);
            });

            result.setTimeLimits(strLimits);
        }

        result.setText(block.getText());

        return result;
    }
}
