package com.dk.piley.reminder

import com.dk.piley.model.task.Task
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.resourcesPath
import com.dk.piley.util.toLocalDateTime
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import java.io.File
import javax.swing.JOptionPane

class NotificationManager : INotificationManager {
    override suspend fun showNotification(task: Task, pileName: String?) {
        val notificationDescription = task.description.ifBlank {
            pileName?.ifBlank {
                task.reminder?.toLocalDateTime()?.dateTimeString()
            } ?: ""
        }
        if (SystemTray.isSupported()) {
            val tray = SystemTray.getSystemTray()
            val image = Toolkit.getDefaultToolkit()
                .getImage(resourcesPath() + File.separator + "icon_transparent.png")
            val trayIcon = TrayIcon(image, "Desktop Notification").apply {
                isImageAutoSize = true
            }
            tray.add(trayIcon)
            trayIcon.displayMessage(
                task.title,
                notificationDescription,
                TrayIcon.MessageType.INFO
            )
        } else {
            // Fallback for systems that don't support SystemTray
            JOptionPane.showMessageDialog(
                null,
                task.title,
                notificationDescription,
                JOptionPane.INFORMATION_MESSAGE
            )
        }
    }

    override fun dismiss(taskId: Long) {
        // TODO("Not yet implemented")
    }
}