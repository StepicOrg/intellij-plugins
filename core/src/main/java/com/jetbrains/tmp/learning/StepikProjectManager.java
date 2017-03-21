package com.jetbrains.tmp.learning;

import com.google.gson.internal.LinkedTreeMap;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.LessonNode;
import com.jetbrains.tmp.learning.courseFormat.SectionNode;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.courseFormat.StudyNode;
import com.jetbrains.tmp.learning.serialization.SampleConverter;
import com.jetbrains.tmp.learning.serialization.StudySerializationUtils;
import com.jetbrains.tmp.learning.serialization.StudyUnrecognizedFormatException;
import com.jetbrains.tmp.learning.serialization.SupportedLanguagesConverter;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.XStreamException;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.DOMBuilder;
import org.jdom.output.XMLOutputter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.steps.Limit;
import org.stepik.api.objects.steps.Sample;
import org.stepik.api.objects.steps.Step;
import org.stepik.api.objects.steps.VideoUrl;
import org.stepik.api.objects.users.User;
import org.stepik.plugin.projectWizard.idea.SandboxModuleBuilder;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.UUID;

import static com.jetbrains.tmp.learning.SupportedLanguages.INVALID;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.stepik.core.utils.ProjectFilesUtils.getOrCreateSrcDirectory;

@State(name = "StepikStudySettings", storages = @Storage("stepik_study_project.xml"))
public class StepikProjectManager implements PersistentStateComponent<Element>, DumbAware {
    private static final int CURRENT_VERSION = 4;
    private static final Logger logger = Logger.getInstance(StepikProjectManager.class);
    @XStreamOmitField
    private static XStream xStream;
    @XStreamOmitField
    private static XMLOutputter outputter;
    @XStreamOmitField
    private static DocumentBuilderFactory factory;
    @XStreamOmitField
    private static DocumentBuilder builder;
    @XStreamOmitField
    private static DOMBuilder domBuilder;
    @XStreamOmitField
    private final Project project;
    private StudyNode<?, ?> root;
    private StudyNode<?, ?> selected;
    private boolean showHint = false;
    private long createdBy;
    private SupportedLanguages defaultLang = INVALID;
    private int version = CURRENT_VERSION;
    private String uuid;

    public StepikProjectManager(@Nullable Project project) {
        this.project = project;
    }

    public StepikProjectManager() {
        this(null);
    }

    @Nullable
    public static StepikProjectManager getInstance(@NotNull final Project project) {
        return ServiceManager.getService(project, StepikProjectManager.class);
    }

    @Nullable
    public static StudyNode getProjectRoot(@NotNull final Project project) {
        StepikProjectManager instance = getInstance(project);

        if (instance == null) {
            return null;
        }

        return instance.getProjectRoot();
    }

    public static boolean isStepikProject(@Nullable Project project) {
        if (project == null) {
            return false;
        }
        StepikProjectManager instance = getInstance(project);
        return instance != null && instance.getProjectRoot() != null;
    }

    private static String elementToXml(@NotNull Element state) {
        if (outputter == null) {
            outputter = new XMLOutputter();
        }
        return outputter.outputString(state.getChild(StudySerializationUtils.MAIN_ELEMENT));
    }

    @NotNull
    public static XStream getXStream() {
        if (xStream == null) {
            xStream = new XStream(new DomDriver());
            xStream.alias("StepikProjectManager", StepikProjectManager.class);
            xStream.alias("CourseNode", CourseNode.class);
            xStream.alias("SectionNode", SectionNode.class);
            xStream.alias("LessonNode", LessonNode.class);
            xStream.alias("StepNode", StepNode.class);
            xStream.alias("Limit", Limit.class);
            xStream.alias("SupportedLanguages", SupportedLanguages.class);
            xStream.alias("VideoUrl", VideoUrl.class);
            xStream.alias("LinkedTreeMap", LinkedTreeMap.class);
            xStream.alias("Sample", Sample.class);
            xStream.alias("Course", Course.class);
            xStream.alias("Section", Section.class);
            xStream.alias("CompoundUnitLesson", CompoundUnitLesson.class);
            xStream.alias("Step", Step.class);
            xStream.alias("User", User.class);
            xStream.autodetectAnnotations(true);
            xStream.setClassLoader(StepikProjectManager.class.getClassLoader());
            xStream.registerConverter(new SupportedLanguagesConverter());
            xStream.registerConverter(new SampleConverter());
            xStream.ignoreUnknownElements();
            xStream.setMode(XStream.ID_REFERENCES);
        }

        return xStream;
    }

    @NotNull
    private static Element toElement(ByteArrayOutputStream out)
            throws ParserConfigurationException, SAXException, IOException {
        if (factory == null) {
            factory = DocumentBuilderFactory.newInstance();
            factory.setValidating(false);
            builder = factory.newDocumentBuilder();
            domBuilder = new DOMBuilder();
        }
        try (ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray())) {
            Document doc = builder.parse(in);
            org.jdom.Document document = domBuilder.build(doc);

            Element root = document.getRootElement();
            document.removeContent(root);

            Element element = new Element("element");
            element.addContent(root);
            return element;
        }
    }

    public static void setSelected(@NotNull Project project, @Nullable StudyNode studyNode, boolean force) {
        StepikProjectManager instance = getInstance(project);
        if (instance != null) {
            instance.setSelected(studyNode, force);
        }
    }

    public static void setSelected(@NotNull Project project, @Nullable StudyNode studyNode) {
        setSelected(project, studyNode, false);
    }

    public static void updateSelection(@NotNull Project project) {
        StepikProjectManager instance = getInstance(project);
        if (instance != null) {
            instance.updateSelection();
        }
    }

    @Nullable
    public static StudyNode<?, ?> getSelected(@Nullable Project project) {
        if (project == null) {
            return null;
        }
        StepikProjectManager instance = getInstance(project);
        if (instance != null) {
            return instance.getSelected();
        }

        return null;
    }

    public StudyNode<?, ?> getSelected() {
        return selected;
    }

    public void setSelected(StudyNode<?, ?> selected) {
        setSelected(selected, false);
    }

    public void setSelected(StudyNode<?, ?> selected, boolean force) {
        this.selected = selected;
        if (project != null) {
            StudyToolWindow toolWindow = StudyUtils.getStudyToolWindow(project);
            if (toolWindow != null) {
                ApplicationManager.getApplication().invokeLater(() -> toolWindow.setStepNode(selected, force));
            }
        }
    }

    public void updateSelection() {
        setSelected(selected, true);
    }

    public void setRootNode(@Nullable StudyNode root) {
        this.root = root;
    }

    @Nullable
    @Override
    public Element getState() {
        if (getProjectRoot() == null) {
            return null;
        }
        try (ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            getXStream().toXML(this, new OutputStreamWriter(out, UTF_8));
            Element el = toElement(out);
            logger.info("Getting the StepikProjectManager state");

            return el;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            logger.warn("Failed getting the StepikProjectManager state", e);
        }

        return null;
    }

    @Override
    public void loadState(Element state) {
        try {
            logger.info("Start load the StepikProjectManager state");
            int version = StudySerializationUtils.getVersion(state);

            switch (version) {
                case 1:
                    state = StudySerializationUtils.convertToSecondVersion(state);
                case 2:
                    state = StudySerializationUtils.convertToThirdVersion(state);
                case 3:
                    state = StudySerializationUtils.convertToFourthVersion(state);
                    //uncomment for future versions
                    //case 4:
                    //state = StudySerializationUtils.convertToFifthVersion(state);
            }

            String xml = elementToXml(state);
            XStream xs = getXStream();
            xs.fromXML(xml, this);

            this.version = CURRENT_VERSION;
            refreshCourse();
            logger.info("The StepikProjectManager state loaded");
        } catch (XStreamException | StudyUnrecognizedFormatException e) {
            logger.warn("Failed deserialization StepikProjectManager \n" + e.getMessage() + "\n" + project);
        }
    }

    private void refreshCourse() {
        if (getProjectRoot() == null) {
            return;
        }

        ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(() -> {
                    ProgressIndicator indicator = ProgressManager.getInstance()
                            .getProgressIndicator();
                    indicator.setIndeterminate(true);
                    root.reloadData(indicator);
                }, "Refreshing Course", true, project);
        ApplicationManager.getApplication().invokeLater(() -> {
            repairProjectFiles(root);
            VirtualFileManager.getInstance().syncRefresh();

            VirtualFile projectDir = project != null ? project.getBaseDir() : null;
            if (projectDir != null && projectDir.findChild(EduNames.SANDBOX_DIR) == null) {
                ModifiableModuleModel model = ModuleManager.getInstance(project).getModifiableModel();

                ApplicationManager.getApplication().runWriteAction(() -> {
                    try {
                        new SandboxModuleBuilder(projectDir.getPath()).createModule(model);
                        model.commit();
                    } catch (IOException | ConfigurationException | JDOMException | ModuleWithNameAlreadyExists e) {
                        logger.warn("Failed repair Sandbox", e);
                    }
                });
            }
        });
    }

    private void repairProjectFiles(@NotNull StudyNode<?, ?> node) {
        if (project != null) {
            if (node instanceof StepNode) {
                getOrCreateSrcDirectory(project, (StepNode) node, false);
            }
            node.getChildren().forEach(this::repairProjectFiles);
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

    public Project getProject() {
        return project;
    }

    public int getVersion() {
        return version;
    }

    public void setCreatedBy(long createdBy) {
        this.createdBy = createdBy;
    }

    @NotNull
    public String getUuid() {
        if (uuid == null) {
            uuid = UUID.randomUUID().toString();
        }
        return uuid;
    }

    @Nullable
    public StudyNode getProjectRoot() {
        return root;
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
        if (root != null ? !root.equals(that.root) : that.root != null) return false;
        //noinspection SimplifiableIfStatement
        if (defaultLang != that.defaultLang) return false;
        return uuid != null ? uuid.equals(that.uuid) : that.uuid == null;
    }

    @Override
    public int hashCode() {
        int result = project != null ? project.hashCode() : 0;
        result = 31 * result + (root != null ? root.hashCode() : 0);
        result = 31 * result + (showHint ? 1 : 0);
        result = 31 * result + (int) (createdBy ^ (createdBy >>> 32));
        result = 31 * result + (defaultLang != null ? defaultLang.hashCode() : 0);
        result = 31 * result + version;
        result = 31 * result + (uuid != null ? uuid.hashCode() : 0);
        return result;
    }
}
