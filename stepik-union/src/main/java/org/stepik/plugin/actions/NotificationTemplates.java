package org.stepik.plugin.actions;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;

public class NotificationTemplates {
    public static final Notification CONNECTION_ERROR =
            new Notification("Connection.error", "Connection error", "Please check your internet configuration.", NotificationType.ERROR);

    public static final Notification CERTIFICATE_ERROR =
            new Notification("Certificate.error", "Certificate error", "Please check your internet configuration.", NotificationType.ERROR);

    public static final Notification DOWNLOAD_WARNING =
            new Notification("Step.download", "Download error", "You didn't send a Step", NotificationType.WARNING);
}
