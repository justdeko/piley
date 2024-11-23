package com.dk.piley.ui.common

import androidx.compose.runtime.Composable
import com.dk.piley.reminder.setupNotificationDelegate
import platform.UserNotifications.UNAuthorizationOptionAlert
import platform.UserNotifications.UNAuthorizationOptionBadge
import platform.UserNotifications.UNAuthorizationOptionSound
import platform.UserNotifications.UNAuthorizationStatusAuthorized
import platform.UserNotifications.UNUserNotificationCenter

/**
 * Notification permission handler that requests the permission and shows a dialog if denied
 *
 * @param launch when the permission request should be launched
 * @param setPermissionGranted callback function when the permission result is returned
 */
@Composable
actual fun NotificationPermissionHandler(
    launch: Boolean,
    setPermissionGranted: (Boolean) -> Unit
) {
    if (launch) {
        val notificationCenter = UNUserNotificationCenter.currentNotificationCenter()
        notificationCenter.getNotificationSettingsWithCompletionHandler {
            if (it?.authorizationStatus != UNAuthorizationStatusAuthorized) {
                notificationCenter.requestAuthorizationWithOptions(NOTIFICATION_PERMISSIONS) { isGranted, _ ->
                    setPermissionGranted(isGranted)
                    if (isGranted) {
                        setupNotificationDelegate(notificationCenter)
                    }
                }
            } else {
                setPermissionGranted(true)
                setupNotificationDelegate(notificationCenter)
            }
        }

    }
}


internal val NOTIFICATION_PERMISSIONS =
    UNAuthorizationOptionAlert or
            UNAuthorizationOptionSound or
            UNAuthorizationOptionBadge