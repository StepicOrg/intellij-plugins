package com.jetbrains.tmp.learning.stepik;

import com.google.gson.annotations.Expose;
import com.jetbrains.tmp.learning.SupportedLanguages;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@SuppressWarnings("CanBeFinal")
public class StepikWrappers {
    static class StepContainer {
        List<StepSource> steps;
    }

    public static class Step {
        @Expose
        StepOptions options;
        @Expose
        String text;
        @Expose
        String name;
    }

    static class StepOptions {
        @Expose
        List<List<String>> samples;
        @Expose
        CodeTemplatesWrapper codeTemplates;
        @Expose
        LimitsWrapper limits;
    }

    static class CodeTemplatesWrapper {
        String python3;
        String java8;

        @Nullable
        String getTemplateForLanguage(@NotNull final SupportedLanguages language) {
            if (language == SupportedLanguages.PYTHON) {
                return python3;
            }

            if (language == SupportedLanguages.JAVA) {
                return java8;
            }
            return null;
        }

        @NotNull
        List<SupportedLanguages> getLanguages() {
            ArrayList<SupportedLanguages> languages = new ArrayList<>();
            if (python3 != null) {
                languages.add(SupportedLanguages.PYTHON);
            }
            if (java8 != null) {
                languages.add(SupportedLanguages.JAVA);
            }
            return languages;
        }
    }

    static class LimitsWrapper {
        @Expose
        private Limit java8;
        @Expose
        private Limit python3;

        Limit getLimit(SupportedLanguages lang) {
            switch (lang) {
                case JAVA:
                    return java8;
                case PYTHON:
                    return python3;
            }
            return null;
        }
    }

    static class Limit {
        @Expose
        private int time;
        @Expose
        private int memory;

        @Override
        public String toString() {
            return String.format("<b>Memory limit</b>: %d Mb<br><b>Time limit</b>: %ds<br><br>", memory, time);
        }
    }

    public static class CoursesContainer {
        public List<CourseInfo> courses;
        public Map meta;
    }

    static class LessonContainer {
        List<Lesson> lessons;
    }

    @SuppressWarnings("WeakerAccess")
    static class StepSource {
        @Expose
        Step block;
        @Expose
        int id;
        @Expose
        int position;
    }

    public static class Section {
        public int course;
        List<Integer> units;
        String title;
        int position;
    }

    public static class SectionContainer {
        public List<Section> sections;
    }

    @SuppressWarnings("WeakerAccess")
    public static class Unit {
        public int section;
        int lesson;
        int position;
    }

    public static class UnitContainer {

        public List<Unit> units;
    }

    static class AttemptWrapper {
        Attempt attempt;

        AttemptWrapper(int step) {
            attempt = new Attempt(step);
        }

        public static class Attempt {
            public int step;
            public int id;

            Attempt(int step) {
                this.step = step;
            }
        }
    }

    public static class AttemptContainer {
        public List<AttemptWrapper.Attempt> attempts;
    }

    static class AuthorWrapper {
        List<StepikUser> users;
    }

    public static class SubmissionToPostWrapper {
        Submission submission;

        public SubmissionToPostWrapper(@NotNull String attemptId, @NotNull String language, @NotNull String code) {
            submission = new Submission(attemptId, new Submission.Reply(language, code));
        }

        static class Submission {
            String attempt;
            Reply reply;

            Submission(String attempt, Reply reply) {
                this.attempt = attempt;
                this.reply = reply;
            }

            static class Reply {
                String language;
                String code;

                Reply(String language, String code) {
                    this.language = language;
                    this.code = code;
                }
            }
        }
    }

    public static class ResultSubmissionWrapper {
        public ResultSubmission[] submissions;

        public static class ResultSubmission {
            public int id;
            public String status;
            public String hint;
        }
    }

    static class Enrollment {
        String course;

        Enrollment(String courseId) {
            course = courseId;
        }
    }

    static class EnrollmentWrapper {
        Enrollment enrollment;

        EnrollmentWrapper(@NotNull final String courseId) {
            enrollment = new Enrollment(courseId);
        }
    }

    static class TokenInfo {
        @Expose
        String accessToken;
        @Expose
        String refreshToken;

        public TokenInfo() {
            accessToken = "";
            refreshToken = "";
        }

        String getAccessToken() {
            return accessToken;
        }

        String getRefreshToken() {
            return refreshToken;
        }
    }

    public static class MetricsWrapper {
        Metric metric;

        public MetricsWrapper(String tags_name, String tags_action, int courseId, int stepId) {
            metric = new Metric(tags_name, tags_action, courseId, stepId);
        }

        public interface MetricActions {
            String POST = "post";
            String DOWNLOAD = "download";
            String GET_COURSE = "get_course";
        }

        public interface PluginNames {
            String STEPIK_UNION = "S_Union";
            String STEPIK_CLION = "S_CLion";
            String STEPIK_PYCHARM = "S_PyCharm";
        }

        class Metric {
            String name = "ide_plugin";
            Tags tags;
            Data data;

            Metric(String tags_name, String tags_action, int courseId, int stepId) {
                this.tags = new Tags(tags_name, tags_action);
                this.data = new Data(courseId, stepId);
            }

            public class Tags {
                String name;
                String action;

                public Tags(String action) {
                    this.action = action;
                }

                Tags(String name, String action) {
                    this.name = name;
                    this.action = action;
                }
            }

            class Data {
                int courseId;
                int stepId;

                Data(int courseId, int stepId) {
                    this.courseId = courseId;
                    this.stepId = stepId;
                }
            }
        }
    }
}
