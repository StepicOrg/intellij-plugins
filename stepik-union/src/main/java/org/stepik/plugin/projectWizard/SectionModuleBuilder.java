package org.stepik.plugin.projectWizard;

import com.intellij.ide.highlighter.ModuleFileType;
import com.intellij.openapi.util.io.FileUtil;
import com.jetbrains.tmp.learning.courseFormat.Section;
import org.jetbrains.annotations.NotNull;

class SectionModuleBuilder extends AbstractModuleBuilder {
    SectionModuleBuilder(@NotNull String moduleDir, @NotNull Section section) {
        String sectionName = section.getDirectory();
        setName(sectionName);
        String path = FileUtil.join(moduleDir, sectionName, sectionName + ModuleFileType.DOT_DEFAULT_EXTENSION);
        setModuleFilePath(path);
    }
}
