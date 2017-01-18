package com.jetbrains.tmp.learning;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileAdapter;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.containers.hash.HashMap;
import com.jetbrains.tmp.learning.actions.StudyActionWithShortcut;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.CourseNode;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import com.jetbrains.tmp.learning.courseFormat.StepNode;
import com.jetbrains.tmp.learning.editor.StudyEditorFactoryListener;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import com.jetbrains.tmp.learning.ui.StudyToolWindowFactory;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class StudyProjectComponent implements ProjectComponent {
    private static final Logger logger = Logger.getInstance(StudyProjectComponent.class.getName());
    private final Project project;
    private final Map<Keymap, List<Pair<String, String>>> deletedShortcuts = new HashMap<>();
    private FileCreatedByUserListener listener;

    private StudyProjectComponent(@NotNull final Project project) {
        this.project = project;
    }

    public static StudyProjectComponent getInstance(@NotNull final Project project) {
        final Module module = ModuleManager.getInstance(project).getModules()[0];
        return module.getComponent(StudyProjectComponent.class);
    }

    @Override
    public void projectOpened() {
        final CourseNode courseNode = StepikProjectManager.getInstance(project).getCourseNode();
        // Check if user has javafx lib in his JDK. Now bundled JDK doesn't have this lib inside.
        if (StudyUtils.hasJavaFx()) {
            Platform.setImplicitExit(false);
        }

        registerStudyToolWindow(courseNode);
        ApplicationManager.getApplication().invokeLater(
                (DumbAwareRunnable) () -> ApplicationManager.getApplication()
                        .runWriteAction((DumbAwareRunnable) () -> {
                            if (courseNode != null) {
                                UISettings uiSettings = UISettings.getInstance();
                                if (uiSettings != null) {
                                    uiSettings.HIDE_TOOL_STRIPES = false;
                                    uiSettings.fireUISettingsChanged();
                                }
                                logger.info("register Shortcuts");
                                registerShortcuts();
                            }
                        }));
    }

    public void registerStudyToolWindow(@Nullable final CourseNode courseNode) {
        if (courseNode != null) {
            final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
            registerToolWindows(toolWindowManager);
            final ToolWindow studyToolWindow =
                    toolWindowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
            if (studyToolWindow != null) {
                studyToolWindow.show(null);
                StudyUtils.initToolWindows(project);
            }
        }
    }

    private void registerShortcuts() {
        StudyToolWindow window = StudyUtils.getStudyToolWindow(project);
        if (window != null) {
            List<AnAction> actionsOnToolbar = window.getActions(true);
            if (actionsOnToolbar != null) {
                actionsOnToolbar.stream()
                        .filter(action -> action instanceof StudyActionWithShortcut)
                        .map(action -> (StudyActionWithShortcut) action)
                        .forEach(action -> {
                            String id = action.getActionId();
                            String[] shortcuts = action.getShortcuts();
                            if (shortcuts != null) {
                                addShortcut(id, shortcuts);
                            }
                        });
            } else {
                logger.warn("Actions on toolbar are nulls");
            }
        }
    }

    private void registerToolWindows(@NotNull final ToolWindowManager toolWindowManager) {
        final ToolWindow toolWindow = toolWindowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
        if (toolWindow == null) {
            toolWindowManager.registerToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW,
                    true,
                    ToolWindowAnchor.RIGHT,
                    project,
                    true);
        }
    }

    private void addShortcut(@NotNull final String actionIdString, @NotNull final String[] shortcuts) {
        KeymapManagerEx keymapManager = KeymapManagerEx.getInstanceEx();
        for (Keymap keymap : keymapManager.getAllKeymaps()) {
            List<Pair<String, String>> pairs = deletedShortcuts.computeIfAbsent(keymap, k -> new ArrayList<>());
            for (String shortcutString : shortcuts) {
                Shortcut studyActionShortcut = new KeyboardShortcut(KeyStroke.getKeyStroke(shortcutString), null);
                String[] actionsIds = keymap.getActionIds(studyActionShortcut);
                for (String actionId : actionsIds) {
                    pairs.add(Pair.create(actionId, shortcutString));
                    keymap.removeShortcut(actionId, studyActionShortcut);
                }
                keymap.addShortcut(actionIdString, studyActionShortcut);
            }
        }
    }

    @Override
    public void projectClosed() {
        final CourseNode courseNode = StepikProjectManager.getInstance(project).getCourseNode();
        if (courseNode != null) {
            final ToolWindow toolWindow = ToolWindowManager.getInstance(project)
                    .getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
            if (toolWindow != null) {
                toolWindow.getContentManager().removeAllContents(false);
            }
            KeymapManagerEx keymapManager = KeymapManagerEx.getInstanceEx();
            for (Keymap keymap : keymapManager.getAllKeymaps()) {
                List<Pair<String, String>> pairs = deletedShortcuts.get(keymap);
                if (pairs != null && !pairs.isEmpty()) {
                    for (Pair<String, String> actionShortcut : pairs) {
                        keymap.addShortcut(actionShortcut.first,
                                new KeyboardShortcut(KeyStroke.getKeyStroke(actionShortcut.second), null));
                    }
                }
            }
        }
        listener = null;
    }

    @Override
    public void initComponent() {
        EditorFactory.getInstance().addEditorFactoryListener(new StudyEditorFactoryListener(), project);
        ActionManager.getInstance().addAnActionListener(new AnActionListener() {
            @Override
            public void beforeActionPerformed(AnAction action, DataContext dataContext, AnActionEvent event) {
                AnAction[] newGroupActions = ((ActionGroup) ActionManager.getInstance()
                        .getAction("NewGroup")).getChildren(null);
                for (AnAction newAction : newGroupActions) {
                    if (newAction == action) {
                        listener = new FileCreatedByUserListener();
                        VirtualFileManager.getInstance().addVirtualFileListener(listener);
                        break;
                    }
                }
            }

            @Override
            public void afterActionPerformed(AnAction action, DataContext dataContext, AnActionEvent event) {
                AnAction[] newGroupActions = ((ActionGroup) ActionManager.getInstance()
                        .getAction("NewGroup")).getChildren(null);
                for (AnAction newAction : newGroupActions) {
                    if (newAction == action) {
                        VirtualFileManager.getInstance().removeVirtualFileListener(listener);
                    }
                }
            }

            @Override
            public void beforeEditorTyping(char c, DataContext dataContext) {

            }
        });
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return StepikProjectManager.class.getSimpleName();
    }

    private class FileCreatedByUserListener extends VirtualFileAdapter {
        @Override
        public void fileCreated(@NotNull VirtualFileEvent event) {
            if (project.isDisposed()) {
                return;
            }
            final VirtualFile createdFile = event.getFile();
            final VirtualFile stepDir = createdFile.getParent();

            if (stepDir != null && stepDir.getName().contains(EduNames.STEP)) {
                final CourseNode courseNode = StepikProjectManager.getInstance(project).getCourseNode();
                if (courseNode == null) {
                    return;
                }
                int stepId = EduUtils.parseDirName(stepDir.getName(), EduNames.STEP);
                final StepNode stepNode = courseNode.getStepById(stepId);
                if (stepNode == null) {
                    return;
                }
                final StepFile stepFile = new StepFile();
                stepFile.init(stepNode);
                final String name = createdFile.getName();
                stepFile.setName(name);
                stepNode.getStepFiles().put(name, stepFile);
            }
        }
    }
}
