package com.jetbrains.tmp.learning.stepik;

import com.intellij.ide.AppLifecycleListener;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.util.ActionCallback;
import com.intellij.openapi.util.Ref;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Alarm;
import com.intellij.util.text.DateFormatUtil;
import com.jetbrains.tmp.learning.courseGeneration.StepikProjectGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class EduStepikUpdater {
    private static final Logger logger = Logger.getInstance(EduStepikUpdater.class);
    private static final long CHECK_INTERVAL = DateFormatUtil.DAY;
    private static final String UPDATE_SETTINGS_IS_NULL = "StepikUpdateSettings instance is null";

    private final Runnable myCheckRunnable = () -> updateCourseList().doWhenDone(() -> queueNextCheck(CHECK_INTERVAL));
    private final Alarm myCheckForUpdatesAlarm = new Alarm(Alarm.ThreadToUse.SWING_THREAD);

    public EduStepikUpdater(@NotNull Application application) {
        scheduleCourseListUpdate(application);
    }

    private void scheduleCourseListUpdate(Application application) {
        if (!checkNeeded()) {
            return;
        }
        application.getMessageBus()
                .connect(application)
                .subscribe(AppLifecycleListener.TOPIC, new AppLifecycleListener.Adapter() {
                    @Override
                    public void appFrameCreated(String[] commandLineArgs, @NotNull Ref<Boolean> willOpenProject) {
                        StepikUpdateSettings updateSettings = StepikUpdateSettings.getInstance();
                        if (updateSettings == null) {
                            logger.warn(UPDATE_SETTINGS_IS_NULL);
                            return;
                        }
                        long timeToNextCheck = updateSettings.getNextTimeCheck() - System.currentTimeMillis();

                        if (timeToNextCheck <= 0) {
                            myCheckRunnable.run();
                        } else {
                            queueNextCheck(timeToNextCheck);
                        }
                    }
                });
    }

    private static ActionCallback updateCourseList() {
        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            final List<CourseInfo> courses = StepikConnectorGet.getCourses();

            StepikUpdateSettings updateSettings = StepikUpdateSettings.getInstance();
            if (updateSettings == null) {
                logger.warn(UPDATE_SETTINGS_IS_NULL);
                return;
            }
            updateSettings.setNextTimeCheck(System.currentTimeMillis() + CHECK_INTERVAL);

            if ((courses.size() == 1 && courses.get(0) == CourseInfo.INVALID_COURSE)) {
                return;
            }

            StepikProjectGenerator.flushCache(courses);

            final List<CourseInfo> cachedCourses = StepikProjectGenerator.getCoursesFromCache();
            courses.removeAll(cachedCourses);

            if (!courses.isEmpty() && !cachedCourses.isEmpty()) {
                final String message;
                final String title;
                if (courses.size() == 1) {
                    title = "New course available";
                    message = courses.get(0).getName();
                } else {
                    title = "New courses available";
                    message = StringUtil.join(courses, CourseInfo::getName, ", ");
                }

                if (!message.isEmpty()) {
                    final Notification notification =
                            new Notification("New.course", title, message, NotificationType.INFORMATION);
                    notification.notify(null);
                }
            }
        });

        return new ActionCallback();
    }

    private void queueNextCheck(long interval) {
        myCheckForUpdatesAlarm.addRequest(myCheckRunnable, interval);
    }

    private static boolean checkNeeded() {
        final List<CourseInfo> courses = StepikProjectGenerator.getCoursesFromCache();
        if (courses.isEmpty()) {
            return true;
        }

        StepikUpdateSettings updateSettings = StepikUpdateSettings.getInstance();
        if (updateSettings == null) {
            logger.warn(UPDATE_SETTINGS_IS_NULL);
            return false;
        }
        long timeToNextCheck = updateSettings.getNextTimeCheck() - System.currentTimeMillis();

        return timeToNextCheck <= 0;
    }
}
