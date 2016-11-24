package com.jetbrains.tmp.learning;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.annotations.Transient;
import com.jetbrains.tmp.learning.courseFormat.*;
import com.jetbrains.tmp.learning.stepik.StepikUser;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import org.jdom.Element;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Implementation of class which contains all the information
 * about study in context of current project
 */

@State(name = "StepikStudySettings", storages = @Storage("stepik_study_project.xml"))
public class StudyTaskManager implements PersistentStateComponent<Element>, DumbAware {
    private static final Logger logger = Logger.getInstance(StudyTaskManager.class);
    public static final int CURRENT_VERSION = 4;
    private StepikUser myUser = new StepikUser();
    private Course myCourse;
    public int VERSION = CURRENT_VERSION;

    private LangManager langManager = new LangManager();

    public boolean myShouldUseJavaFx = StudyUtils.hasJavaFx();
    private StudyToolWindow.StudyToolWindowMode myToolWindowMode = StudyToolWindow.StudyToolWindowMode.TEXT;
    private boolean myTurnEditingMode = false;
    private boolean showHint = true;

    private String defaultLang;

    @Transient
    private final Project myProject;

    public StudyTaskManager(Project project) {
        myProject = project;
    }

    public StudyTaskManager() {
        this(null);
    }

    public void setCourse(Course course) {
        myCourse = course;
    }

    @Nullable
    public Course getCourse() {
        return myCourse;
    }

    @Nullable
    @Override
    public Element getState() {
        if (myCourse == null || myUser.getEmail().isEmpty()) {
            return null;
        }
        Element el = new Element("stepikTaskManager");
        Element courseElement = new Element(StudySerializationUtils.Xml.MAIN_ELEMENT);
        XmlSerializer.serializeInto(this, courseElement);
        el.addContent(courseElement);
        return el;
    }

    @Override
    public void loadState(Element state) {
        try {
            int version = StudySerializationUtils.Xml.getVersion(state);
            if (version == -1) {
                logger.error("StudyTaskManager doesn't contain any version:\n" + state.getValue());
                return;
            }
            switch (version) {
                case 1:
                    state = StudySerializationUtils.Xml.convertToSecondVersion(state);
                case 2:
                    state = StudySerializationUtils.Xml.convertToThirdVersion(state, myProject);
                case 3:
                    state = StudySerializationUtils.Xml.convertToForthVersion(state, myProject);
                    //uncomment for future versions
                    //case 4:
                    //state = StudySerializationUtils.Xml.convertToFifthVersion(state, myProject);
            }
            XmlSerializer.deserializeInto(this, state.getChild(StudySerializationUtils.Xml.MAIN_ELEMENT));
            VERSION = CURRENT_VERSION;
            if (myCourse != null) {
                myCourse.initCourse(true);
                if (version != VERSION) {
                    String updatedCoursePath = FileUtil.join(PathManager.getConfigPath(),
                            "courses",
                            Integer.toString(myCourse.getId()));
                    if (new File(updatedCoursePath).exists()) {
                        myCourse.setCourseDirectory(updatedCoursePath);
                    }
                }
            }
        } catch (StudySerializationUtils.StudyUnrecognizedFormatException e) {
            logger.error("Unexpected course format:\n", new XMLOutputter().outputString(state));
        }
    }

    public static StudyTaskManager getInstance(@NotNull final Project project) {
        return ServiceManager.getService(project, StudyTaskManager.class);
    }

    public boolean shouldUseJavaFx() {
        return myShouldUseJavaFx;
    }

    public void setShouldUseJavaFx(boolean shouldUseJavaFx) {
        this.myShouldUseJavaFx = shouldUseJavaFx;
    }

    public StudyToolWindow.StudyToolWindowMode getToolWindowMode() {
        return myToolWindowMode;
    }

    public void setToolWindowMode(StudyToolWindow.StudyToolWindowMode toolWindowMode) {
        myToolWindowMode = toolWindowMode;
    }

    public boolean isTurnEditingMode() {
        return myTurnEditingMode;
    }

    public void setTurnEditingMode(boolean turnEditingMode) {
        myTurnEditingMode = turnEditingMode;
    }

    @NotNull
    public StepikUser getUser() {
        return myUser;
    }

    public void setUser(@NotNull final StepikUser user) {
        myUser = user;
    }

    public void setDefaultLang(String defaultLang) {
        this.defaultLang = defaultLang;
    }

    @Nullable
    public String getDefaultLang() {
        return defaultLang;
    }

    public LangManager getLangManager() {
        return langManager;
    }

    public void setLangManager(LangManager langManager) {
        this.langManager = langManager;
    }

    public boolean getShowHint() {
        return showHint;
    }

    public void setShowHint(boolean showHint) {
        this.showHint = showHint;
    }
}