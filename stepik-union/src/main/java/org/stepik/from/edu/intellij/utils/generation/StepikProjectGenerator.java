package org.stepik.from.edu.intellij.utils.generation;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.stepik.CourseInfo;
import com.jetbrains.tmp.learning.stepik.StepikConnectorGet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.util.*;

import static com.jetbrains.tmp.learning.StudyUtils.execCancelable;

public class StepikProjectGenerator extends EduProjectGenerator {
    private static final Logger logger = Logger.getInstance(StepikProjectGenerator.class);

    protected static final String CACHE_NAME = "enrolledCourseNames.txt";

    public List<CourseInfo> getCourses(boolean force) {
        if (OUR_COURSES_DIR.exists()) {
            myCourses = getCoursesFromCache();
        }
        if (force || myCourses.isEmpty()) {
            myCourses = execCancelable(StepikConnectorGet::getEnrolledCourses);
            flushCache(myCourses);
        }
        if (myCourses.isEmpty()) {
            myCourses = getBundledIntro();
        }
        return myCourses;
    }

    public static void flushCache(List<CourseInfo> courses) {
        File cacheFile = new File(OUR_COURSES_DIR, CACHE_NAME);
        PrintWriter writer = null;
        try {
            if (!createCacheFile(cacheFile)) return;
            Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();

            final Set<CourseInfo> courseInfos = new HashSet<>();
            courseInfos.addAll(courses);
//            courseInfos.addAll(getCoursesFromCache());

            writer = new PrintWriter(cacheFile);
            for (CourseInfo courseInfo : courseInfos) {
                final String json = gson.toJson(courseInfo);
                writer.println(json);
            }
        } catch (IOException e) {
            logger.error(e);
        } finally {
            StudyUtils.closeSilently(writer);
        }
    }

    private static boolean createCacheFile(File cacheFile) throws IOException {
        if (!OUR_COURSES_DIR.exists()) {
            final boolean created = OUR_COURSES_DIR.mkdirs();
            if (!created) {
                logger.error("Cannot flush courses cache. Can't create courses directory");
                return false;
            }
        }
        if (!cacheFile.exists()) {
            final boolean created = cacheFile.createNewFile();
            if (!created) {
                logger.error("Cannot flush courses cache. Can't create " + CACHE_NAME + " file");
                return false;
            }
        }
        return true;
    }

    public static List<CourseInfo> getCoursesFromCache() {
        List<CourseInfo> courses = new ArrayList<>();
        final File cacheFile = new File(OUR_COURSES_DIR, CACHE_NAME);
        if (!cacheFile.exists()) {
            return courses;
        }
        try {
            final FileInputStream inputStream = new FileInputStream(cacheFile);
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                try {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                                .create();
                        final CourseInfo courseInfo = gson.fromJson(line, CourseInfo.class);
                        courses.add(courseInfo);
                    }
                } catch (IOException | JsonSyntaxException e) {
                    logger.error(e.getMessage());
                } finally {
                    StudyUtils.closeSilently(reader);
                }
            } finally {
                StudyUtils.closeSilently(inputStream);
            }
        } catch (FileNotFoundException e) {
            logger.error(e.getMessage());
        }
        return courses;
    }

    @Override
    @Nullable
    protected Course getCourse(@NotNull final Project project) {
        return ProgressManager.getInstance()
                .runProcessWithProgressSynchronously(() -> {
                    ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                    return execCancelable(() -> {
                        final Course course = StepikConnectorGet.getCourse(project, mySelectedCourseInfo);
                        if (course != null) {
                            flushCourse(project, course);
                            course.initCourse(false);
                        }
                        return course;
                    });
                }, "Creating Course", true, project);
    }

    @NotNull
    public List<CourseInfo> getCoursesUnderProgress(
            boolean force,
            @NotNull final String progressTitle,
            @NotNull final Project project) {
        try {
            return ProgressManager.getInstance()
                    .runProcessWithProgressSynchronously(() -> {
                        ProgressManager.getInstance().getProgressIndicator().setIndeterminate(true);
                        return getCourses(force);
                    }, progressTitle, true, project);
        } catch (RuntimeException e) {
            return Collections.singletonList(CourseInfo.INVALID_COURSE);
        }
    }
}
