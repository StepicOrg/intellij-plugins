package org.stepik.plugin.actions;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.StepNode;

import java.util.Optional;

class ActionUtils {
    private static final Logger logger = Logger.getInstance(ActionUtils.class);

    static boolean checkLangSettings(StepNode stepNode, Project project) {
        String srcPath = String.join("/", stepNode.getPath(), EduNames.SRC);
        VirtualFile src = project.getBaseDir().findFileByRelativePath(srcPath);
        if (src == null) {
            logger.warn("Can't find VF for: " + srcPath);
            return false;
        }

        Optional<SupportedLanguages> lang = Optional.of(stepNode.getCurrentLang());
        if (src.findChild(stepNode.getCurrentLang().getMainFileName()) == null) {
            lang = stepNode.getSupportedLanguages()
                    .stream()
                    .filter(x -> src.findChild(x.getMainFileName()) != null)
                    .findFirst();
        }

        if (!lang.isPresent()) {
            logger.warn("Lang settings is broken. Please create new project.");
            return false;
        }
        stepNode.setCurrentLang(lang.get());
        return true;
    }
}
