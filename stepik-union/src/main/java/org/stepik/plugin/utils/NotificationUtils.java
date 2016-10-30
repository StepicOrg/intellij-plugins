package org.stepik.plugin.utils;

import com.intellij.notification.Notification;
import com.intellij.openapi.project.Project;

public class NotificationUtils {
    private NotificationUtils() {
    }

    public static void initRuntimeException(Notification notification, Project project) {
        notification.notify(project);
        throw new RuntimeException();
    }

    public static void initRuntimeException(Project project) {
        initRuntimeException(NotificationTemplates.CONNECTION_ERROR, project);
    }

    public static void initRuntimeException(Project project, String text) {
        NotificationTemplates.CONNECTION_ERROR.notify(project);
        throw new RuntimeException(text);
    }

    public static void showNotification(Notification notification, Project project) {
        notification.notify(project);
    }

}
