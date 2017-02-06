package com.jetbrains.tmp.learning;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.stepik.StepikConnectorLogin;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.client.StepikApiClient;
import org.stepik.api.exceptions.StepikClientException;
import org.stepik.api.objects.courses.Courses;

import java.util.UUID;

/**
 * Implementation of class which contains all the information
 * about study in context of current project
 */

@State(name = "StepikStudySettings", storages = @Storage("stepik_study_project.xml"))
public class StepikProjectManager implements PersistentStateComponent<Element>, DumbAware {
    private static final Logger logger = Logger.getInstance(StepikProjectManager.class);
    private static final int CURRENT_VERSION = 3;
    private final Project project;
    private CourseNode courseNode;
    private boolean showHint = false;
    private long createdBy;
    private SupportedLanguages defaultLang = SupportedLanguages.INVALID;
    private int version = CURRENT_VERSION;
    private String uuid;

    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public StepikProjectManager(@Nullable Project project) {
        this.project = project;
    }

    @SuppressWarnings("WeakerAccess")
    public StepikProjectManager() {
        this(null);
    }

    public static StepikProjectManager getInstance(@NotNull final Project project) {
        return ServiceManager.getService(project, StepikProjectManager.class);
    }

    public static boolean isStepikProject(@Nullable Project project) {
        return project != null && getInstance(project).getCourseNode() != null;
    }

    @Nullable
    public CourseNode getCourseNode() {
        return courseNode;
    }

    public void setCourseNode(@Nullable CourseNode courseNode) {
        this.courseNode = courseNode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        StepikProjectManager that = (StepikProjectManager) o;

        if (showHint != that.showHint) return false;
        if (createdBy != that.createdBy) return false;
        if (version != that.version) return false;
        if (project != null ? !project.equals(that.project) : that.project != null) return false;
        if (courseNode != null ? !courseNode.equals(that.courseNode) : that.courseNode != null) return false;
        //noinspection SimplifiableIfStatement
        if (defaultLang != that.defaultLang) return false;
        return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;
    }

    @Override
    public int hashCode() {
        int result = project != null ? project.hashCode() : 0;
        result = 31 * result + (courseNode != null ? courseNode.hashCode() : 0);
        result = 31 * result + (showHint ? 1 : 0);
        result = 31 * result + (int) (createdBy ^ (createdBy >>> 32));
        result = 31 * result + (defaultLang != null ? defaultLang.hashCode() : 0);
        result = 31 * result + version;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }

    @Nullable
    @Override
    public Element getState() {
        if (courseNode == null) {
            return null;
        }
        Element el = new Element("stepikProjectManager");
        Element courseElement = new Element(StudySerializationUtils.MAIN_ELEMENT);
        XmlSerializer.serializeInto(this, courseElement);
        el.addContent(courseElement);
        logger.info("Getting the StepikProjectManager state");
        return el;
    }

    @Override
    public void loadState(Element state) {
        logger.info("Start load the StepikProjectManager state");
        try {
            int version = StudySerializationUtils.getVersion(state);

            switch (version) {
                case 1:
                    state = StudySerializationUtils.convertToSecondVersion(state);
                case 2:
                    state = StudySerializationUtils.convertToThirdVersion(state);
                    //uncomment for future versions
                    //case 3:
                    //state = StudySerializationUtils.convertToFourthVersion(state);
            }

            XmlSerializer.deserializeInto(this, state.getChild(StudySerializationUtils.MAIN_ELEMENT));
            this.version = CURRENT_VERSION;
        } catch (StudyUnrecognizedFormatException e) {
            logger.warn("Failed deserialization StepikProjectManager \n" + e.getMessage() + "\n" + project);
            return;
        }

        refreshCourse();
        logger.info("The StepikProjectManager state loaded");
    }

    private void refreshCourse() {
        if (courseNode == null) {
            return;
        }

        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(() -> {
                    ProgressIndicator indicator = ProgressManager.getInstance()
                            .getProgressIndicator();
                    indicator.setIndeterminate(true);
                    try {
                        StepikApiClient stepikApiClient = StepikConnectorLogin.authAndGetStepikApiClient();
                        Courses courses = stepikApiClient.courses()
                                .get()
                                .id(courseNode.getId())
                                .execute();
                        if (!courses.isEmpty()) {
                            courseNode.setData(courses.getCourses().get(0));
                        }
                    } catch (StepikClientException logged) {
                        logger.warn("A course initialization don't is fully", logged);
                    }
                    courseNode.init(false, indicator);
                }, "Refreshing Course", true, project);
    }

    @NotNull
    public SupportedLanguages getDefaultLang() {
        return defaultLang;
    }

    public void setDefaultLang(@NotNull SupportedLanguages defaultLang) {
        this.defaultLang = defaultLang;
    }

    public boolean getShowHint() {
        return showHint;
    }

    public void setShowHint(boolean showHint) {
        this.showHint = showHint;
    }

    @SuppressWarnings("unused")
    @Transient
    public Project getProject() {
        return project;
    }

    @SuppressWarnings("unused")
    public int getVersion() {
        return version;
    }

    @SuppressWarnings("unused")
    public void setVersion(int version) {
        this.version = version;
    }

    @SuppressWarnings("unused")
    public long getCreatedBy() {
        return createdBy;
    }

    @SuppressWarnings("unused")
    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    public String getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    @SuppressWarnings("unused")
    public void setUuid(String uuid) {
        this.uuid = uuid;
    }
}
