package com.dk.piley.ui.common

import androidx.compose.runtime.Composable
import platform.EventKit.EKAuthorizationStatusAuthorized
import platform.EventKit.EKAuthorizationStatusDenied
import platform.EventKit.EKAuthorizationStatusNotDetermined
import platform.EventKit.EKAuthorizationStatusRestricted
import platform.EventKit.EKEntityType
import platform.EventKit.EKEventStore

@Composable
actual fun CalendarPermissionHandler(
    launch: Boolean,
    setPermissionGranted: (Boolean) -> Unit
) {
    val eventStore = EKEventStore()
    val status =
        EKEventStore.Companion.authorizationStatusForEntityType(EKEntityType.EKEntityTypeReminder)

    if (launch) {
        when (status) {
            EKAuthorizationStatusAuthorized -> setPermissionGranted(true)
            EKAuthorizationStatusNotDetermined -> {
                eventStore.requestFullAccessToRemindersWithCompletion { granted, error ->
                    error?.let { println("Error granting permission: $error") }
                    setPermissionGranted(granted)
                    println("Permission granted: $granted")
                }
            }

            EKAuthorizationStatusRestricted, EKAuthorizationStatusDenied -> {
                setPermissionGranted(false)
                println("Permission denied or restricted")
            }

            else -> setPermissionGranted(false)
        }
    }
}