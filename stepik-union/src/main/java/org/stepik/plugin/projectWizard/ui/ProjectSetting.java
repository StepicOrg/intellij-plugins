package org.stepik.plugin.projectWizard.ui;

import com.jetbrains.tmp.learning.SupportedLanguages;
import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.courses.Course;

/**
 * @author meanmail
 */
interface ProjectSetting {
    void selectedBuildType(@NotNull BuildType type);

    void selectedCourse(@NotNull Course course);

    void addListener(@NotNull ProjectSettingListener listener);

    void removeListener(@NotNull ProjectSettingListener listener);

    void selectedProgrammingLanguage(@NotNull SupportedLanguages language);
}
