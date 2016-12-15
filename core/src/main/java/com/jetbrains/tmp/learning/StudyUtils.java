package com.jetbrains.tmp.learning;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.ex.FileEditorManagerEx;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.content.Content;
import com.intellij.util.ObjectUtils;
import com.intellij.util.TimeoutUtil;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import com.jetbrains.tmp.learning.ui.StudyToolWindowFactory;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.stepik.core.utils.ProjectFilesUtils;

import javax.swing.*;
import java.awt.*;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StudyUtils {
    private StudyUtils() {
    }

    private static final Logger logger = Logger.getInstance(StudyUtils.class.getName());
    private static final String EMPTY_TASK_TEXT = "Please, open any task to see task description";
    private static final String ourPrefix = "<html><head><script type=\"text/x-mathjax-config\">\n" +
            "            MathJax.Hub.Config({\n" +
            "                tex2jax: {\n" +
            "                    inlineMath: [ ['$','$'], [\"\\\\(\",\"\\\\)\"] ],\n" +
            "                    displayMath: [ ['$$','$$'], [\"\\\\[\",\"\\\\]\"] ],\n" +
            "                    processEscapes: true,\n" +
            "                    processEnvironments: true\n" +
            "                },\n" +
            "                displayAlign: 'center',\n" +
            "                \"HTML-CSS\": {\n" +
            "                    styles: {'#mydiv': {\"font-size\": %s}},\n" +
            "                    preferredFont: null,\n" +
            "                    linebreaks: { automatic: true }\n" +
            "                }\n" +
            "            });\n" +
            "</script><script type=\"text/javascript\"\n" +
            " src=\"http://cdn.mathjax.org/mathjax/latest/MathJax.js?config=TeX-AMS_HTML-full\">\n" +
            " </script></head><body><div id=\"mydiv\">";

    private static final String ourPostfix = "</div></body></html>";

    public static void closeSilently(@Nullable final Closeable stream) {
        if (stream != null) {
            try {
                stream.close();
            } catch (IOException e) {
                // close silently
            }
        }
    }

    @Nullable
    public static <T> T getFirst(@NotNull final Iterable<T> container) {
        Iterator<T> iterator = container.iterator();
        if (!iterator.hasNext()) {
            return null;
        }
        return iterator.next();
    }

    static boolean indexIsValid(int index, @NotNull final Collection collection) {
        int size = collection.size();
        return index >= 0 && index < size;
    }

    public static void updateAction(@NotNull final AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        presentation.setEnabled(false);
        final Project project = e.getProject();
        if (project != null) {
            final StudyEditor studyEditor = getSelectedStudyEditor(project);
            if (studyEditor != null) {
                presentation.setEnabledAndVisible(true);
            }
        }
    }

    public static void updateToolWindows(@NotNull final Project project) {
        final StudyToolWindow studyToolWindow = getStudyToolWindow(project);
        if (studyToolWindow != null) {
            String taskText = getTaskText(project);
            if (taskText != null) {
                studyToolWindow.setTaskText(taskText, null, project);
            } else {
                logger.warn("Task text is null");
            }
            studyToolWindow.updateCourseProgress(project);
        }
    }

    static void initToolWindows(@NotNull final Project project) {
        final ToolWindowManager windowManager = ToolWindowManager.getInstance(project);
        windowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW)
                .getContentManager()
                .removeAllContents(false);
        StudyToolWindowFactory factory = new StudyToolWindowFactory();
        factory.createToolWindowContent(project, windowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW));

    }

    @Nullable
    public static StudyToolWindow getStudyToolWindow(@NotNull final Project project) {
        ToolWindow toolWindow = ToolWindowManager.getInstance(project)
                .getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
        if (toolWindow != null) {
            Content[] contents = toolWindow.getContentManager().getContents();
            for (Content content : contents) {
                JComponent component = content.getComponent();
                if (component != null && component instanceof StudyToolWindow) {
                    return (StudyToolWindow) component;
                }
            }
        }
        return null;
    }

    /**
     * shows pop up in the center of "check task" button in study editor
     */
    public static void showCheckPopUp(@NotNull final Project project, @NotNull final Balloon balloon) {
        final StudyEditor studyEditor = getSelectedStudyEditor(project);
        assert studyEditor != null;

        balloon.show(computeLocation(studyEditor.getEditor()), Balloon.Position.above);
        Disposer.register(project, balloon);
    }

    public static RelativePoint computeLocation(Editor editor) {

        final Rectangle visibleRect = editor.getComponent().getVisibleRect();
        Point point = new Point(visibleRect.x + visibleRect.width + 10,
                visibleRect.y + 10);
        return new RelativePoint(editor.getComponent(), point);
    }

    @Nullable
    public static TaskFile getTaskFile(@NotNull final Project project, @NotNull final VirtualFile file) {
        final Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course == null) {
            return null;
        }
        VirtualFile taskDir = file.getParent();
        if (taskDir == null) {
            return null;
        }
        //need this because of multi-module generation
        if (EduNames.SRC.equals(taskDir.getName())) {
            taskDir = taskDir.getParent();
            if (taskDir == null) {
                return null;
            }
        }
        final String taskDirName = taskDir.getName();
        if (taskDirName.contains(EduNames.TASK)) {
            final VirtualFile lessonDir = taskDir.getParent();
            if (lessonDir != null) {
                Lesson lesson = course.getLessonByDirName(lessonDir.getName());
                if (lesson == null) {
                    return null;
                }
                int taskIndex = EduUtils.getIndex(taskDirName, EduNames.TASK) - 1;
                final List<Task> tasks = lesson.getTaskList();
                if (!indexIsValid(taskIndex, tasks)) {
                    return null;
                }
                final Task task = tasks.get(taskIndex);
                return task.getFile(file.getName());
            }
        }
        return null;
    }

    @Nullable
    public static StudyEditor getSelectedStudyEditor(@NotNull final Project project) {
        try {
            final FileEditor fileEditor = FileEditorManagerEx.getInstanceEx(project).getSplitters().getCurrentWindow().
                    getSelectedEditor().getSelectedEditorWithProvider().getFirst();
            if (fileEditor instanceof StudyEditor) {
                return (StudyEditor) fileEditor;
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    @Nullable
    public static Editor getSelectedEditor(@NotNull final Project project) {
        final StudyEditor studyEditor = getSelectedStudyEditor(project);
        if (studyEditor != null) {
            return studyEditor.getEditor();
        }
        return null;
    }

    @Nullable
    @Contract("null -> null")
    static String getTaskTextFromTask(@Nullable final Task task) {
        if (task == null) {
            return null;
        }
        String text = task.getText();
        if (text != null) {
            return getTextWithStepLink(task);
        }
        return null;
    }

    @NotNull
    private static String getTextWithStepLink(Task task) {
        StringBuilder stringBuilder = new StringBuilder();

        if (task.getLesson().getId() > 0) {
            stringBuilder.append("<a href=\"https://stepik.org/lesson/")
                    .append(task.getLesson().getId())
                    .append("/step/")
                    .append(task.getPosition())
                    .append("\">View step on Stepik.org</a>");
        } else {
            stringBuilder.append("<b>Create project for this course again to see the link to the step.</b>");
        }

        if (!task.getText().startsWith("<p>") && !task.getText().startsWith("<h")) {
            stringBuilder.append("<br><br>");
        }

        stringBuilder.append(task.getDescription());
        return stringBuilder.toString();
    }

    @Nullable
    public static StudyPluginConfigurator getConfigurator(@NotNull final Project project) {
        StudyPluginConfigurator[] extensions = StudyPluginConfigurator.EP_NAME.getExtensions();
        for (StudyPluginConfigurator extension : extensions) {
            if (extension.accept(project)) {
                return extension;
            }
        }
        return null;
    }

    @Nullable
    public static String getTaskText(@NotNull final Project project) {
        final Task task = getSelectedTask(project);
        if (task != null) {
            return getTaskTextFromTask(task);
        }
        return EMPTY_TASK_TEXT;
    }

    private static String getRelativePath(@NotNull Project project, @NotNull VirtualFile item) {
        String path = item.getPath();
        String basePath = project.getBasePath();

        if (basePath == null) {
            return path;
        }

        return ProjectFilesUtils.getRelativePath(basePath, path);
    }

    @Nullable
    public static Task getSelectedTask(@NotNull Project project) {
        VirtualFile[] files = FileEditorManager.getInstance(project).getSelectedFiles();
        if (files.length == 0) {
            return null;
        }

        return getTask(project, files[0]);
    }

    @Nullable
    public static TaskFile getSelectedTaskFile(@NotNull Project project) {
        VirtualFile[] files = FileEditorManager.getInstance(project).getSelectedFiles();
        TaskFile taskFile = null;
        for (VirtualFile file : files) {
            taskFile = getTaskFile(project, file);
            if (taskFile != null) {
                break;
            }
        }
        return taskFile;
    }

    @Nullable
    public static Task getCurrentTask(@NotNull final Project project) {
        final TaskFile taskFile = getSelectedTaskFile(project);
        return taskFile != null ? taskFile.getTask() : null;
    }

    @Nullable
    public static Project getStudyProject() {
        Project studyProject = null;
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        for (Project project : openProjects) {
            if (StudyTaskManager.getInstance(project).getCourse() != null) {
                studyProject = project;
            }
        }
        if (studyProject == null) {
            logger.info("return default project");
            return ProjectManager.getInstance().getDefaultProject();
        }
        logger.info("return regular project");
        return studyProject;
    }

    @NotNull
    public static File getCourseDirectory(@NotNull Project project, Course course) {
        return new File(StepikProjectGenerator.OUR_COURSES_DIR, Integer.toString(course.getId()));
    }

    public static boolean hasJavaFx() {
        try {
            Class.forName("javafx.application.Platform");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static Pattern taskPathPattern;

    @Nullable
    public static Task getTask(@NotNull Project project, @NotNull VirtualFile taskVF) {
        String path = getRelativePath(project, taskVF);
        if (taskPathPattern == null) {
            taskPathPattern = Pattern.compile("^(section[0-9]+)/(lesson[0-9]+)/(task[0-9]+)/src/.*");
        }
        Matcher matcher = taskPathPattern.matcher(path);
        if (matcher.matches()) {
            Course course = StudyTaskManager.getInstance(project).getCourse();
            if (course == null) {
                return null;
            }
            Lesson lesson = course.getLessonByDirName(matcher.group(2));
            if (lesson == null) {
                return null;
            }
            return lesson.getTask(matcher.group(3));
        }
        return null;
    }

    // supposed to be called under progress
    @Nullable
    public static <T> T execCancelable(@NotNull final Callable<T> callable) {
        final Future<T> future = ApplicationManager.getApplication().executeOnPooledThread(callable);

        while (!future.isCancelled() && !future.isDone()) {
            ProgressManager.checkCanceled();
            TimeoutUtil.sleep(500);
        }
        T result = null;
        try {
            result = future.get();
        } catch (InterruptedException | ExecutionException e) {
            logger.warn(e.getMessage());
        }
        return result;
    }

    @Nullable
    public static Task getTaskFromSelectedEditor(Project project) {
        final StudyEditor editor = getSelectedStudyEditor(project);
        Task task = null;
        if (editor != null) {
            final TaskFile file = editor.getTaskFile();
            task = file.getTask();
        }
        return task;
    }

    @Nullable
    public static VirtualFile findTaskDescriptionVirtualFile(@NotNull final VirtualFile parent) {
        return ObjectUtils.chooseNotNull(parent.findChild(EduNames.TASK_HTML), parent.findChild(EduNames.TASK_MD));
    }

    @Nullable
    static File createTaskDescriptionFile(@NotNull final File parent) {
        if (new File(parent, EduNames.TASK_HTML).exists()) {
            return new File(parent, EduNames.TASK_HTML);
        } else {
            return new File(parent, EduNames.TASK_MD);
        }
    }

    @Nullable
    public static Document getDocument(String basePath, int lessonIndex, int taskIndex, String fileName) {
        String taskPath = FileUtil.join(basePath, EduNames.LESSON + lessonIndex, EduNames.TASK + taskIndex);
        VirtualFile taskFile = LocalFileSystem.getInstance().findFileByPath(FileUtil.join(taskPath, fileName));
        if (taskFile == null) {
            taskFile = LocalFileSystem.getInstance().findFileByPath(FileUtil.join(taskPath, EduNames.SRC, fileName));
        }
        if (taskFile == null) {
            return null;
        }
        return FileDocumentManager.getInstance().getDocument(taskFile);
    }

    public static void showErrorPopupOnToolbar(@NotNull Project project) {
        final Balloon balloon =
                JBPopupFactory.getInstance()
                        .createHtmlTextBalloonBuilder("Couldn't post your reaction", MessageType.ERROR, null)
                        .createBalloon();
        showCheckPopUp(project, balloon);
    }
}
