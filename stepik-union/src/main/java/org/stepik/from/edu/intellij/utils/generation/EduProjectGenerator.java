package org.stepik.from.edu.intellij.utils.generation;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseGeneration.StudyProjectGenerator;
import com.jetbrains.tmp.learning.stepik.CourseInfo;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.List;

import static com.jetbrains.tmp.learning.StudyUtils.execCancelable;

public class EduProjectGenerator extends StudyProjectGenerator {
    private static final Logger logger = Logger.getInstance(EduProjectGenerator.class);

    @Override
    public void generateProject(@NotNull Project project, @NotNull VirtualFile baseDir) {
        final Course course = getCourse(project);
        if (course == null) {
            logger.warn("EduProjectGenerator: Failed to get builders");
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
            myCourses = execCancelable(StepikConnectorGet::getCourses);
            flushCache(myCourses);
        }
        if (myCourses.isEmpty()) {
            myCourses = getBundledIntro();
        }
        return myCourses;
    }
}
