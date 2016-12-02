package org.stepik.plugin.actions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Task;

import java.util.Optional;

class ActionUtils {
    private static final Logger logger = Logger.getInstance(ActionUtils.class.getName());
    static boolean checkLangSettings(Task task, Project project) {

        String srcPath = String.join("/", task.getPath(), EduNames.SRC);
        VirtualFile src = project.getBaseDir().findFileByRelativePath(srcPath);
        if (src == null) {
            logger.warn("Can't find VF for: " + srcPath);
            return false;
        }

        Optional<SupportedLanguages> lang = Optional.of(task.getCurrentLang());
        if (src.findChild(lang.get().getMainFileName()) == null) {
            lang = task.getSupportedLanguages()
                    .stream()
                    .filter(x -> src.findChild(x.getMainFileName()) != null)
                    .findFirst();
        }

        if (!lang.isPresent()) {
            logger.warn("Lang settings is broken. Please create new project.");
            return false;
        }
        task.setCurrentLang(lang.get());
        return true;
    }
}
