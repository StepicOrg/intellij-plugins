package com.jetbrains.tmp.learning.stepik;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.intellij.ide.projectView.ProjectView;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.jetbrains.tmp.learning.StudyTaskManager;
import com.jetbrains.tmp.learning.StudyUtils;
import com.jetbrains.tmp.learning.core.EduNames;
import com.jetbrains.tmp.learning.courseFormat.Course;
import com.jetbrains.tmp.learning.courseFormat.Lesson;
import com.jetbrains.tmp.learning.courseFormat.StudyStatus;
import com.jetbrains.tmp.learning.courseFormat.Task;
import com.jetbrains.tmp.learning.courseFormat.TaskFile;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import com.jetbrains.tmp.learning.courseGeneration.StudyGenerator;
import com.jetbrains.tmp.learning.editor.StudyEditor;
import com.jetbrains.tmp.learning.ui.StudyToolWindow;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static com.jetbrains.tmp.learning.stepik.StepikConnectorGet.getFromStepik;

class EduAdaptiveStepikConnector {
    private static final Logger logger = Logger.getInstance(EduAdaptiveStepikConnector.class);
    private static final int CONNECTION_TIMEOUT = 60 * 1000;

    @Nullable
    static Task getNextRecommendation(@NotNull final Project project, @NotNull Course course) {
        try {
            final CloseableHttpClient client = StepikConnectorLogin.getHttpClient();
            final URI uri = new URIBuilder(EduStepikNames.STEPIK_API_URL + EduStepikNames.RECOMMENDATIONS_URL)
                    .addParameter(EduNames.COURSE, String.valueOf(course.getId()))
                    .build();
            final HttpGet request = new HttpGet(uri);
            //setHeaders(request, EduStepikNames.CONTENT_TYPE_APPL_JSON);
            setTimeout(request);

            final CloseableHttpResponse response = client.execute(request);
            final StatusLine statusLine = response.getStatusLine();
            final HttpEntity responseEntity = response.getEntity();
            final String responseString = responseEntity != null ? EntityUtils.toString(responseEntity) : "";

            if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                final Gson gson = new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                        .create();
                final StepikWrappers.RecommendationWrapper recomWrapper = gson.fromJson(responseString,
                        StepikWrappers.RecommendationWrapper.class);

                if (recomWrapper.recommendations.length != 0) {
                    final StepikWrappers.Recommendation recommendation = recomWrapper.recommendations[0];
                    final String lessonId = recommendation.lesson;
                    final StepikWrappers.LessonContainer
                            lessonContainer = getFromStepik(EduStepikNames.LESSONS + lessonId,
                            StepikWrappers.LessonContainer.class);
                    if (lessonContainer.lessons.size() == 1) {
                        final Lesson realLesson = lessonContainer.lessons.get(0);
                        course.getSections().get(0).getLessons().get(0).setId(Integer.parseInt(lessonId));

                        viewAllSteps(client, realLesson.getId());

                        for (int stepId : realLesson.steps) {
                            final StepikWrappers.Step step = StepikConnectorGet.getStep(stepId);
                            if (step.name.equals("code")) {
                                return getTaskFromStep(stepId, step, realLesson.getName());
                            }
                        }

                        logger.warn("Got a lesson without code part as a recommendation");
                    } else {
                        logger.warn("Got unexpected number of lessons: " + lessonContainer.lessons.size());
                    }
                }
            } else {
                throw new IOException("Stepik returned non 200 status code: " + responseString);
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());

            final String connectionMessages = "Connection problems, Please, try again";
            final Balloon balloon =
                    JBPopupFactory.getInstance()
                            .createHtmlTextBalloonBuilder(connectionMessages, MessageType.ERROR, null)
                            .createBalloon();
            ApplicationManager.getApplication().invokeLater(() -> {
                if (StudyUtils.getSelectedEditor(project) != null) {
                    StudyUtils.showCheckPopUp(project, balloon);
                }
            });

        } catch (URISyntaxException e) {
            logger.warn(e.getMessage());
        }
        return null;
    }

    private static void setTimeout(HttpGet request) {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT)
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(CONNECTION_TIMEOUT)
                .build();
        request.setConfig(requestConfig);
    }

    private static void setTimeout(HttpPost request) {
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(CONNECTION_TIMEOUT)
                .setConnectTimeout(CONNECTION_TIMEOUT)
                .setSocketTimeout(CONNECTION_TIMEOUT)
                .build();
        request.setConfig(requestConfig);
    }

    private static void viewAllSteps(CloseableHttpClient client, int lessonId) throws URISyntaxException, IOException {
        final URI unitsUrl = new URIBuilder(EduStepikNames.UNITS).addParameter(EduNames.LESSON,
                String.valueOf(lessonId)).build();
        final StepikWrappers.UnitContainer unitContainer = getFromStepik(unitsUrl.toString(),
                StepikWrappers.UnitContainer.class);
        if (unitContainer.units.size() != 1) {
            logger.warn("Got unexpected numbers of units: " + unitContainer.units.size());
            return;
        }

        final URIBuilder builder = new URIBuilder(EduStepikNames.ASSIGNMENT);
        for (Integer step : unitContainer.units.get(0).assignments) {
            builder.addParameter("ids[]", String.valueOf(step));
        }
        final URI assignmentUrl = builder.build();
        final StepikWrappers.AssignmentsWrapper assignments = getFromStepik(assignmentUrl.toString(),
                StepikWrappers.AssignmentsWrapper.class);
        if (assignments.assignments.size() > 0) {
            for (StepikWrappers.Assignment assignment : assignments.assignments) {
                final HttpPost post = new HttpPost(EduStepikNames.STEPIK_API_URL + EduStepikNames.VIEWS_URL);
                final StepikWrappers.ViewsWrapper viewsWrapper = new StepikWrappers.ViewsWrapper(assignment.id,
                        assignment.step);
                post.setEntity(new StringEntity(new Gson().toJson(viewsWrapper)));
                //setHeaders(post, EduStepikNames.CONTENT_TYPE_APPL_JSON);
                final CloseableHttpResponse viewPostResult = client.execute(post);
                if (viewPostResult.getStatusLine().getStatusCode() != HttpStatus.SC_CREATED) {
                    logger.warn("Error while Views post, code: " + viewPostResult.getStatusLine().getStatusCode());
                }
            }
        } else {
            logger.warn("Got assignments of incorrect length: " + assignments.assignments.size());
        }
    }

    private static boolean postRecommendationReaction(
            @NotNull final String lessonId,
            @NotNull final String user, int reaction) {
        final HttpPost post = new HttpPost(EduStepikNames.STEPIK_API_URL + EduStepikNames.RECOMMENDATION_REACTIONS_URL);
        final String json = new Gson()
                .toJson(new StepikWrappers.RecommendationReactionWrapper(new StepikWrappers.RecommendationReaction(
                        reaction,
                        user,
                        lessonId)));
        post.setEntity(new StringEntity(json, ContentType.APPLICATION_JSON));
        final CloseableHttpClient client = StepikConnectorLogin.getHttpClient();
        //setHeaders(post, EduStepikNames.CONTENT_TYPE_APPL_JSON);
        setTimeout(post);
        try {
            final CloseableHttpResponse execute = client.execute(post);
            if (execute.getStatusLine().getStatusCode() == HttpStatus.SC_CREATED) {
                return true;
            } else {
                logger.warn("Stepik returned non-201 status code: " + execute.getStatusLine().getStatusCode() + " " +
                        EntityUtils.toString(execute.getEntity()));
                return false;
            }
        } catch (IOException e) {
            logger.warn(e.getMessage());
        }
        return false;
    }

    static void addNextRecommendedTask(@NotNull final Project project, int reaction) {
        final StudyEditor editor = StudyUtils.getSelectedStudyEditor(project);
        final Course course = StudyTaskManager.getInstance(project).getCourse();
        if (course != null && editor != null && editor.getTaskFile() != null) {
            final StepikUser user = StudyTaskManager.getInstance(project).getUser();

            final boolean recommendationReaction =
                    postRecommendationReaction(
                            String.valueOf(editor.getTaskFile().getTask().getLesson().getId()),
                            String.valueOf(user.getId()),
                            reaction);
            if (recommendationReaction) {
                final Task task = getNextRecommendation(project, course);

                if (task != null) {
                    final Lesson adaptive = course.getSections().get(0).getLessons().get(0);
                    final Task unsolvedTask = adaptive.getTaskList().get(adaptive.getTaskList().size() - 1);
                    if (reaction == 0 || reaction == -1) {
                        unsolvedTask.setName(task.getName());
                        unsolvedTask.setStepId(task.getStepId());
                        unsolvedTask.setText(task.getText());
                        unsolvedTask.setStatus(StudyStatus.UNCHECKED);
                        final Map<String, TaskFile> taskFiles = task.getTaskFiles();
                        if (taskFiles.size() == 1) {
                            final TaskFile taskFile = editor.getTaskFile();
                            taskFile.setText(((TaskFile) taskFiles.values().toArray()[0]).getText());
                            ApplicationManager.getApplication().invokeLater(() ->
                                    ApplicationManager.getApplication().runWriteAction(() ->
                                            editor.getEditor()
                                                    .getDocument()
                                                    .setText(
                                                            taskFiles.get(
                                                                    EduStepikNames.DEFAULT_TASKFILE_NAME).getText())));
                        } else {
                            logger.warn("Got task without unexpected number of task files: " + taskFiles.size());
                        }

                        final File lessonDirectory = new File(course.getCourseDirectory(), adaptive.getDirectory());
                        final File taskDirectory = new File(lessonDirectory,
                                EduNames.TASK + String.valueOf(adaptive.getTaskList().size()));
                        StepikProjectGenerator.flushTask(task, taskDirectory);
                        StepikProjectGenerator.flushCourseJson(course, new File(course.getCourseDirectory()));
                        final VirtualFile lessonDir = project.getBaseDir().findChild(adaptive.getDirectory());

                        if (lessonDir != null) {
                            createTestFiles(course, task, unsolvedTask, lessonDir);
                        }
                        final StudyToolWindow window = StudyUtils.getStudyToolWindow(project);
                        if (window != null) {
                            window.setTaskText(unsolvedTask.getText(), unsolvedTask.getTaskDir(project), project);
                        }
                    } else {
                        adaptive.addTask(task);
                        task.setIndex(adaptive.getTaskList().size());
                        final VirtualFile lessonDir = project.getBaseDir().findChild(adaptive.getDirectory());

                        if (lessonDir != null) {
                            ApplicationManager.getApplication()
                                    .invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
                                        try {
                                            StudyGenerator.createTask(task,
                                                    lessonDir,
                                                    new File(course.getCourseDirectory(), lessonDir.getName()),
                                                    project);
                                        } catch (IOException e) {
                                            logger.warn(e.getMessage());
                                        }
                                    }));
                        }

                        final File lessonDirectory = new File(course.getCourseDirectory(), adaptive.getDirectory());
                        StepikProjectGenerator.flushLesson(lessonDirectory, adaptive);
                        StepikProjectGenerator.flushCourseJson(course, new File(course.getCourseDirectory()));
                        course.initCourse(true);
                    }
                }
                ApplicationManager.getApplication().invokeLater(() -> {
                    VirtualFileManager.getInstance().refreshWithoutFileWatcher(false);
                    ProjectView.getInstance(project).refresh();
                });
            } else {
                logger.warn("Recommendation reactions weren't posted");
                ApplicationManager.getApplication().invokeLater(() -> StudyUtils.showErrorPopupOnToolbar(project));
            }
        }
    }

    private static void createTestFiles(Course course, Task task, Task unsolvedTask, VirtualFile lessonDir) {
        ApplicationManager.getApplication().invokeLater(() -> ApplicationManager.getApplication().runWriteAction(() -> {
            try {
                final VirtualFile taskDir = VfsUtil.findFileByIoFile(new File(lessonDir.getCanonicalPath(),
                        unsolvedTask.getDirectory()), true);
                final File resourceRoot = new File(course.getCourseDirectory(), lessonDir.getName());
                if (taskDir != null) {
                    File newResourceRoot = new File(resourceRoot, taskDir.getName());
                    File[] filesInTask = newResourceRoot.listFiles();
                    if (filesInTask != null) {
                        for (File file : filesInTask) {
                            String fileName = file.getName();
                            if (!task.isTaskFile(fileName)) {
                                File resourceFile = new File(newResourceRoot, fileName);
                                File fileInProject = new File(taskDir.getCanonicalPath(), fileName);
                                FileUtil.copy(resourceFile, fileInProject);
                            }
                        }
                    }
                } else {
                    logger.warn("Task directory is null");
                }
            } catch (IOException e) {
                logger.warn(e.getMessage());
            }
        }));
    }

    @NotNull
    private static Task getTaskFromStep(
            int lessonID,
            @NotNull final StepikWrappers.Step step, @NotNull String name) {
        final Task task = new Task();
        task.setName(name);
        task.setStepId(lessonID);
        task.setText(step.text);
        task.setStatus(StudyStatus.UNCHECKED);
        if (step.options.samples != null) {
            final StringBuilder builder = new StringBuilder();
            step.options.samples.stream().filter(sample -> sample.size() == 2).forEach(sample -> {
                builder.append("<b>Sample Input:</b><br>");
                builder.append(StringUtil.replace(sample.get(0), "\n", "<br>"));
                builder.append("<br>");
                builder.append("<b>Sample Output:</b><br>");
                builder.append(StringUtil.replace(sample.get(1), "\n", "<br>"));
                builder.append("<br><br>");
            });
            task.setText(task.getText() + "<br>" + builder.toString());
        }

        if (step.options.executionMemoryLimit != null && step.options.executionTimeLimit != null) {
            String builder = "<b>Memory limit</b>: " +
                    step.options.executionMemoryLimit + " Mb" +
                    "<br>" +
                    "<b>Time limit</b>: " +
                    step.options.executionTimeLimit + "s" +
                    "<br><br>";
            task.setText(task.getText() + builder);
        }

        task.taskFiles = new HashMap<>();      // TODO: it looks like we don't need taskFiles as map anymore
        if (step.options.files != null) {
            for (TaskFile taskFile : step.options.files) {
                task.taskFiles.put(taskFile.getName(), taskFile);
            }
        } else {
            final TaskFile taskFile = new TaskFile();
            taskFile.setName("code");
            task.taskFiles.put("code.py", taskFile);
        }
        return task;
    }
}
