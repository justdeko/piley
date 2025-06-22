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
    val status = eventStore.authorizationStatusForEntityType(EKEntityType.EKEntityTypeEvent)

    when (status) {
        EKAuthorizationStatusAuthorized -> setPermissionGranted(true)
        EKAuthorizationStatusNotDetermined -> {
            if (launch) {
                eventStore.requestAccessToEntityType(EKEntityType.EKEntityTypeEvent) { granted, _ ->
                    setPermissionGranted(granted)
                }
            } else {
                setPermissionGranted(false)
            }
        }
        EKAuthorizationStatusRestricted, EKAuthorizationStatusDenied -> setPermissionGranted(false)
        else -> setPermissionGranted(false)
    }
}