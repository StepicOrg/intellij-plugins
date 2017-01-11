package com.jetbrains.tmp.learning;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.courseFormat.Course;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of class which contains all the information
 * about study in context of current project
 */

@State(name = "StepikStudySettings", storages = @Storage("stepik_study_project.xml"))
public class StepikProjectManager implements PersistentStateComponent<Element>, DumbAware {
    private static final Logger logger = Logger.getInstance(StepikProjectManager.class);
    private static final int CURRENT_VERSION = 1;
    private final Project project;
    private Course course;
    private boolean showHint = true;
    private int createdBy;
    private int updatedBy;
    @NotNull
    private SupportedLanguages defaultLang = SupportedLanguages.INVALID;
    private int version = CURRENT_VERSION;

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

    public static StepikProjectManager getDefaultInstance() {
        Project defaultProject = ProjectManager.getInstance().getDefaultProject();
        return StepikProjectManager.getInstance(defaultProject);
    }

    public static List<StepikProjectManager> getOpenedInstances() {
        Project[] openedProjects = ProjectManager.getInstance().getOpenProjects();

        ArrayList<StepikProjectManager> openedInstances = new ArrayList<>();

        for (Project project : openedProjects) {
            StepikProjectManager instance = getInstance(project);
            if (instance.course != null) {
                openedInstances.add(instance);
            }
        }

        return openedInstances;
    }

    @Nullable
    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    @Nullable
    @Override
    public Element getState() {
        if (course == null) {
            return null;
        }
        Element el = new Element("stepikProjectManager");
        Element courseElement = new Element(StudySerializationUtils.Xml.MAIN_ELEMENT);
        XmlSerializer.serializeInto(this, courseElement);
        el.addContent(courseElement);
        return el;
    }

    @Override
    public void loadState(Element state) {
        try {
            int version = StudySerializationUtils.Xml.getVersion(state);

            //noinspection StatementWithEmptyBody
            switch (version) {
                //uncomment for future versions
                //case 1:
                //state = StudySerializationUtils.Xml.convertToSecondVersion(state);
            }
            XmlSerializer.deserializeInto(this, state.getChild(StudySerializationUtils.Xml.MAIN_ELEMENT));
            this.version = CURRENT_VERSION;
            if (course != null) {
                course.initCourse(true);
            }
        } catch (StudySerializationUtils.StudyUnrecognizedFormatException e) {
            logger.warn("Failed deserialization StepikProjectManager \n" + e.getMessage());
        }
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

    public int getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(int createdBy) {
        this.createdBy = createdBy;
    }

    public int getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(int updatedBy) {
        this.updatedBy = updatedBy;
        if (createdBy == 0) {
            createdBy = updatedBy;
        }
    }
}
