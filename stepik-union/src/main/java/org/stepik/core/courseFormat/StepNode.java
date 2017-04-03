package org.stepik.core.courseFormat;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
import org.stepik.api.objects.steps.BlockView;
import org.stepik.api.objects.steps.BlockViewOptions;
import org.stepik.api.objects.steps.Limit;
import org.stepik.api.objects.steps.Sample;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.Steps;
import org.stepik.api.objects.units.Units;
import org.stepik.core.SupportedLanguages;
import org.stepik.core.core.EduNames;
import org.stepik.core.courseFormat.stepHelpers.ChoiceQuizNodeHelper;
import org.stepik.core.courseFormat.stepHelpers.CodeHelper;
import org.stepik.core.courseFormat.stepHelpers.DatasetQuizNodeHelper;
import org.stepik.core.courseFormat.stepHelpers.MatchingQuizNodeHelper;
import org.stepik.core.courseFormat.stepHelpers.NumberQuizNodeHelper;
import org.stepik.core.courseFormat.stepHelpers.QuizHelper;
import org.stepik.core.courseFormat.stepHelpers.SortingQuizNodeHelper;
import org.stepik.core.courseFormat.stepHelpers.StepHelper;
import org.stepik.core.courseFormat.stepHelpers.StringQuizNodeHelper;
import org.stepik.core.courseFormat.stepHelpers.TableQuizNodeHelper;
import org.stepik.core.courseFormat.stepHelpers.TextHelper;
import org.stepik.core.courseFormat.stepHelpers.VideoStepNodeHelper;
import org.stepik.core.stepik.StepikConnectorLogin;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.stepik.core.SupportedLanguages.INVALID;
import static org.stepik.core.stepik.StepikConnectorLogin.authAndGetStepikApiClient;

public class StepNode extends Node<Step, StepNode, Step, StepNode> {
    private static final Logger logger = Logger.getInstance(StepNode.class);
    private List<SupportedLanguages> supportedLanguages;
    private SupportedLanguages currentLang;
    private long courseId;
    @XStreamOmitField
    private Long assignment;

    public StepNode() {}

    public StepNode(@NotNull Project project, @NotNull Step data) {
        super(project, data);
    }

    @Override
    public void init(@NotNull Project project, @Nullable StudyNode parent) {
        supportedLanguages = null;
        courseId = 0;

        super.init(project, parent);
    }

    @Override
    protected void loadData(long id) {
        try {
            StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
            Steps steps = stepikApiClient.steps()
                    .get()
                    .id(id)
                    .execute();

            Step step;
            if (!steps.isEmpty()) {
                step = steps.getSteps().get(0);
            } else {
                step = new Step();
                step.setId(id);
            }
            setData(step);
        } catch (StepikClientException logged) {
            logger.warn(String.format("Failed step lesson data id=%d", id), logged);
        }
    }

    @Override
    protected Class<StepNode> getChildClass() {
        return StepNode.class;
    }

    @Override
    protected Class<Step> getChildDataClass() {
        return Step.class;
    }

    @NotNull
    public String getText() {
        Step data = getData();
        return data != null ? data.getBlock().getText() : "";
    }

    @NotNull
    public String getTemplate(@NotNull SupportedLanguages language) {
        Map<String, String> templates;
        Step data = getData();
        if (data == null) {
            return "";
        }
        templates = getData().getBlock().getOptions().getCodeTemplates();
        return templates.getOrDefault(language.getName(), "");
    }

    @NotNull
    public String getCurrentTemplate() {
        return getTemplate(getCurrentLang());
    }

    @Override
    public List<StepNode> getChildren() {
        return Collections.emptyList();
    }

    @Override
    protected List<Step> getChildDataList() {
        return Collections.emptyList();
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

        Step data = getData();
        if (data == null) {
            return 0;
        }

        int lessonId = data.getLesson();
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
            CompoundUnitLesson lessonData = lessonNode.getData();
            if (lessonData != null) {
                lessonData.setUnit(units.getItems().get(0));
            }

            courseId = lessonNode.getCourseId();
            return courseId;
        } catch (StepikClientException ignored) {
        }
        return 0;
    }

    @NotNull
    private Map<String, Limit> getLimits() {
        Step data = getData();
        if (data == null) {
            return Collections.emptyMap();
        }
        return data.getBlock().getOptions().getLimits();
    }

    @NotNull
    public Limit getLimit() {
        return getLimits().getOrDefault(getCurrentLang().getName(), new Limit());
    }

    @NotNull
    public List<SupportedLanguages> getSupportedLanguages() {
        if (supportedLanguages == null) {
            supportedLanguages = new ArrayList<>();

            BlockView block;
            Step data = getData();
            if (data == null) {
                return supportedLanguages;
            }

            block = data.getBlock();

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

    @Override
    protected Class<Step> getDataClass() {
        return Step.class;
    }

    @NotNull
    public List<Sample> getSamples() {
        Step data = getData();
        if (data != null && getType() == StepType.CODE) {
            return data.getBlock().getOptions().getSamples();
        }

        return Collections.emptyList();
    }

    @NotNull
    public StepType getType() {
        Step data = getData();
        if (data == null) {
            return StepType.UNKNOWN;
        }
        return StepType.of(data.getBlock().getName());
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
    public boolean canBeLeaf() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        StepNode stepNode = (StepNode) o;

        if (courseId != stepNode.courseId) return false;
        //noinspection SimplifiableIfStatement
        if (supportedLanguages != null ?
                !supportedLanguages.equals(stepNode.supportedLanguages) :
                stepNode.supportedLanguages != null) return false;
        return currentLang == stepNode.currentLang;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (supportedLanguages != null ? supportedLanguages.hashCode() : 0);
        result = 31 * result + (currentLang != null ? currentLang.hashCode() : 0);
        result = 31 * result + (int) (courseId ^ (courseId >>> 32));
        return result;
    }

    @NotNull
    public VideoStepNodeHelper asVideoStep(@NotNull Project project) {
        return new VideoStepNodeHelper(project, this);
    }

    @NotNull
    public ChoiceQuizNodeHelper asChoiceStep(@NotNull Project project) {
        return new ChoiceQuizNodeHelper(project, this);
    }

    @NotNull
    public StringQuizNodeHelper asStringStep(@NotNull Project project) {
        return new StringQuizNodeHelper(project, this);
    }

    @NotNull
    public SortingQuizNodeHelper asSortingStep(@NotNull Project project) {
        return new SortingQuizNodeHelper(project, this);
    }

    @NotNull
    public MatchingQuizNodeHelper asMatchingStep(@NotNull Project project) {
        return new MatchingQuizNodeHelper(project, this);
    }

    @NotNull
    public NumberQuizNodeHelper asNumberStep(@NotNull Project project) {
        return new NumberQuizNodeHelper(project, this);
    }

    @NotNull
    public DatasetQuizNodeHelper asDatasetStep(@NotNull Project project) {
        return new DatasetQuizNodeHelper(project, this);
    }

    @NotNull
    public TableQuizNodeHelper asTableStep(@NotNull Project project) {
        return new TableQuizNodeHelper(project, this);
    }

    public Long getAssignment() {
        if (assignment == null) {
            StudyNode parent = getParent();
            if (parent != null && parent instanceof LessonNode) {
                LessonNode lesson = (LessonNode) parent;
                CompoundUnitLesson data = lesson.getData();
                if (data != null) {
                    List<Long> steps = data.getLesson().getSteps();
                    steps.sort(Long::compareTo);
                    int index;
                    if ((index = steps.indexOf(getId())) != -1) {
                        List<Long> assignments = data.getUnit().getAssignments();
                        if (index < assignments.size()) {
                            assignment = assignments.get(index);
                        }
                    }
                }
            }
        }
        return assignment;
    }

    public QuizHelper asQuizHelper(@NotNull Project project) {
        return new QuizHelper(project, this);
    }

    public StepHelper asStepHelper(@NotNull Project project) {
        return new StepHelper(project, this);
    }

    public TextHelper asTextHelper(@NotNull Project project) {
        return new TextHelper(project, this);
    }

    public CodeHelper asCodeHelper(@NotNull Project project) {
        return new CodeHelper(project, this);
    }
}
