package org.stepik.plugin.projectWizard.ui;

import org.jetbrains.annotations.NotNull;
import org.stepik.api.objects.courses.Course;

/**
 * @author meanmail
 */
interface ProjectSetting {
    void selectedCourse(@NotNull Course course);

    void addListener(@NotNull ProjectSettingListener listener);

    void removeListener(@NotNull ProjectSettingListener listener);
}
