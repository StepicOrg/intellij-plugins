package org.stepik.plugin.projectWizard.idea;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.module.ModifiableModuleModel;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleWithNameAlreadyExists;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.StepikProjectManager;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.core.EduUtils;
import com.jetbrains.tmp.learning.courseFormat.Step;
import com.jetbrains.tmp.learning.courseFormat.StepFile;
import org.apache.commons.codec.binary.Base64;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Map;

class StepModuleBuilder extends ModuleBuilderWithSrc {
    private static final Logger logger = Logger.getInstance(StepModuleBuilder.class);
    private final Step step;
    private final Project project;

    StepModuleBuilder(String moduleDir, @NotNull Step step, @NotNull Project project) {
        this.step = step;
        this.project = project;
        String stepName = step.getDirectory();
        setName(stepName);
        setModuleFilePath(FileUtil.join(moduleDir, stepName,
                stepName + ModuleFileType.DOT_DEFAULT_EXTENSION));
    }

    @NotNull
    @Override
    public Module createModule(@NotNull ModifiableModuleModel moduleModel) throws InvalidDataException,
            IOException, ModuleWithNameAlreadyExists, JDOMException, ConfigurationException {
        Module module = super.createModule(moduleModel);
        createStepContent();

        return module;
    }

    private void createStepContent() throws IOException {
        StepikProjectManager stepManager = StepikProjectManager.getInstance(project);
        step.setCurrentLang(stepManager.getDefaultLang());

        String src = stepManager.getProject().getBasePath() + String.join("/", step.getPath(), EduNames.SRC);

        createStepFiles(step, src);

        SupportedLanguages currentLang = step.getCurrentLang();

        moveFromHide(currentLang.getMainFileName(), src);
    }

    private void createStepFiles(@NotNull Step step, @NotNull String src) {
        String hide = src + "/" + EduNames.HIDE;
        for (Map.Entry<String, StepFile> stepFileEntry : step.getStepFiles().entrySet()) {
            final String name = stepFileEntry.getKey();
            final StepFile stepFile = stepFileEntry.getValue();
            final File file = new File(hide, name);

            try {
                if (EduUtils.isImage(stepFile.getName())) {
                    FileUtil.writeToFile(file, Base64.decodeBase64(stepFile.getText()));
                } else {
                    FileUtil.writeToFile(file, stepFile.getText());
                }
            } catch (IOException e) {
                logger.error("Failed copying file " + file);
            }
        }
    }

    private void moveFromHide(@NotNull String filename, @NotNull String src) throws IOException {
        Path source = Paths.get(src, EduNames.HIDE, filename);
        Path target = Paths.get(src, filename);
        Files.move(source, target, StandardCopyOption.REPLACE_EXISTING);
    }
}
