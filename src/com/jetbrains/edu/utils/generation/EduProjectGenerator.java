package com.jetbrains.edu.utils.generation;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.edu.learning.StudyTaskManager;
import com.jetbrains.edu.learning.courseFormat.Course;
import com.jetbrains.edu.learning.courseGeneration.StudyProjectGenerator;
import com.jetbrains.edu.learning.stepic.CourseInfo;
import com.jetbrains.edu.learning.stepic.StepicConnectorGet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import static com.jetbrains.edu.learning.StudyUtils.execCancelable;

public class EduProjectGenerator extends StudyProjectGenerator {
    private static final Logger LOG = Logger.getInstance(EduProjectGenerator.class);

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir) {
        final Course course = getCourse(project);
        if (course == null) {
            LOG.warn("EduProjectGenerator: Failed to get builders");
            return;
        }
        //need this not to update builders
        //when we update builders we don't know anything about modules, so we create folders for lessons directly
        course.setUpToDate(true);
        StudyTaskManager.getInstance(project).setCourse(course);
        course.setCourseDirectory(new File(OUR_COURSES_DIR, mySelectedCourseInfo.getName()).getAbsolutePath());
    }

    @Override
    public List<CourseInfo> getCourses(boolean force) {
        if (OUR_COURSES_DIR.exists()) {
            myCourses = getCoursesFromCache();
        }
        if (force || myCourses.isEmpty()) {
            myCourses = execCancelable(StepicConnectorGet::getCourses);
            flushCache(myCourses);
        }
        if (myCourses.isEmpty()) {
            myCourses = getBundledIntro();
        }
        return myCourses;
    }
}
