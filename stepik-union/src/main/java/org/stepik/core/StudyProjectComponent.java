package org.stepik.core;

import com.intellij.ide.ui.UISettings;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.KeyboardShortcut;
import com.intellij.openapi.actionSystem.Shortcut;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.keymap.Keymap;
import com.intellij.openapi.keymap.ex.KeymapManagerEx;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.util.containers.hash.HashMap;
import javafx.application.Platform;
import org.jetbrains.annotations.NotNull;
import org.stepik.core.actions.StudyActionWithShortcut;
import org.stepik.core.courseFormat.StudyNode;
import org.stepik.core.metrics.Metrics;
import org.stepik.core.ui.StudyToolWindow;
import org.stepik.core.ui.StudyToolWindowFactory;
import org.stepik.plugin.actions.navigation.StudyNavigator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.stepik.core.metrics.MetricsStatus.SUCCESSFUL;

public class StudyProjectComponent implements ProjectComponent {
    private static final Logger logger = Logger.getInstance(StudyProjectComponent.class.getName());
    private static final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Project project;
    private final Map<Keymap, List<Pair<String, String>>> deletedShortcuts = new HashMap<>();

    private StudyProjectComponent(@NotNull final Project project) {
        this.project = project;
    }

    public static StudyProjectComponent getInstance(@NotNull final Project project) {
        final Module module = ModuleManager.getInstance(project).getModules()[0];
        return module.getComponent(StudyProjectComponent.class);
    }

    @Override
    public void projectOpened() {
        if (!StepikProjectManager.isStepikProject(project)) {
            return;
        }

        Platform.setImplicitExit(false);

        registerStudyToolWindow();
        ApplicationManager.getApplication().invokeLater(
                (DumbAwareRunnable) () -> ApplicationManager.getApplication()
                        .runWriteAction((DumbAwareRunnable) () -> {
                            UISettings uiSettings = UISettings.getInstance();
                            if (uiSettings != null) {
                                uiSettings.HIDE_TOOL_STRIPES = false;
                                uiSettings.fireUISettingsChanged();
                            }
                            logger.info("register Shortcuts");
                            registerShortcuts();
                        }));
        Metrics.openProject(project, SUCCESSFUL);

        executor.execute(() -> {
            StepikProjectManager projectManager = StepikProjectManager.getInstance(project);
            if (projectManager == null) {
                return;
            }

            StudyNode root = projectManager.getProjectRoot();

            StudyNode<?, ?> selected = projectManager.getSelected();
            if (root != null) {
                if (projectManager.isAdaptive()) {
                    StudyNode<?, ?> recommendation = StudyUtils.getRecommendation(root);
                    if (recommendation == null) {
                        selected = null;
                    } else if (selected == null || selected.getParent() != recommendation.getParent()) {
                        selected = recommendation;
                    }
                }

                if (selected == null) {
                    selected = StudyNavigator.nextLeaf(root);
                }

                projectManager.setSelected(selected);
            }
        });
    }

    public void registerStudyToolWindow() {
        if (!StepikProjectManager.isStepikProject(project)) {
            return;
        }
        final ToolWindowManager toolWindowManager = ToolWindowManager.getInstance(project);
        registerToolWindows(toolWindowManager);
        final ToolWindow studyToolWindow =
                toolWindowManager.getToolWindow(StudyToolWindowFactory.STUDY_TOOL_WINDOW);
        if (studyToolWindow != null) {
            studyToolWindow.show(null);
            StudyUtils.initToolWindows(project);
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
        if (!StepikProjectManager.isStepikProject(project)) {
            return;
        }

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

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return StepikProjectManager.class.getSimpleName();
    }
}
