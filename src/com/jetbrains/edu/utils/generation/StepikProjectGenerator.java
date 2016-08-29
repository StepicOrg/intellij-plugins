package com.jetbrains.edu.utils.generation;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.intellij.openapi.diagnostic.Logger;
import com.jetbrains.edu.learning.StudyUtils;
import com.jetbrains.edu.learning.stepik.CourseInfo;
import com.jetbrains.edu.learning.stepik.StepikConnectorGet;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.jetbrains.edu.learning.StudyUtils.execCancelable;

public class StepikProjectGenerator extends EduProjectGenerator {
    private static final Logger LOG = Logger.getInstance(StepikProjectGenerator.class);

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
            courseInfos.addAll(getCoursesFromCache());

            writer = new PrintWriter(cacheFile);
            for (CourseInfo courseInfo : courseInfos) {
                final String json = gson.toJson(courseInfo);
                writer.println(json);
            }
        }
        catch (IOException e) {
            LOG.error(e);
        }
        finally {
            StudyUtils.closeSilently(writer);
        }
    }

    private static boolean createCacheFile(File cacheFile) throws IOException {
        if (!OUR_COURSES_DIR.exists()) {
            final boolean created = OUR_COURSES_DIR.mkdirs();
            if (!created) {
                LOG.error("Cannot flush courses cache. Can't create courses directory");
                return false;
            }
        }
        if (!cacheFile.exists()) {
            final boolean created = cacheFile.createNewFile();
            if (!created) {
                LOG.error("Cannot flush courses cache. Can't create " + CACHE_NAME + " file");
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
                        Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create();
                        final CourseInfo courseInfo = gson.fromJson(line, CourseInfo.class);
                        courses.add(courseInfo);
                    }
                }
                catch (IOException | JsonSyntaxException e) {
                    LOG.error(e.getMessage());
                }
                finally {
                    StudyUtils.closeSilently(reader);
                }
            }
            finally {
                StudyUtils.closeSilently(inputStream);
            }
        }
        catch (FileNotFoundException e) {
            LOG.error(e.getMessage());
        }
        return courses;
    }
}
