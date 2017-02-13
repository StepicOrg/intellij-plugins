package org.stepik.plugin.projectWizard;

import org.jetbrains.annotations.Nullable;
import org.stepik.api.objects.StudyObject;
import org.stepik.api.objects.courses.Course;
import org.stepik.api.objects.lessons.CompoundUnitLesson;
import org.stepik.api.objects.sections.Section;
import org.stepik.api.objects.steps.Step;

import java.io.File;

/**
 * @author meanmail
 */
public class ProjectWizardUtils {
    private static String findNonExistingFileName(String searchDirectory, String preferredName) {
        String fileName = preferredName;
        int idx = 1;

        while (new File(searchDirectory, fileName).exists()) {
            fileName = preferredName + "_" + idx;
            ++idx;
        }

        return fileName;
    }

    @Nullable
    public static String getProjectDefaultName(String projectDirectory, StudyObject studyObject) {
        long id = studyObject.getId();
        String projectName = null;
        if (studyObject instanceof Course) {
            projectName = "course" + id;
        } else if (studyObject instanceof CompoundUnitLesson) {
            projectName = "lesson" + id;
        } else if (studyObject instanceof Section) {
            projectName = "section" + id;
        } else if (studyObject instanceof Step) {
            projectName = "step" + id;
        }

        if (projectName != null) {
            projectName = findNonExistingFileName(projectDirectory, projectName);
        }
        return projectName;
    }
}
