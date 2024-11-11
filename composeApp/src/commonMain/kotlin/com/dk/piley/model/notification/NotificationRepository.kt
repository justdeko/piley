package com.dk.piley.model.notification

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NotificationRepository {
    private val _notificationEventFlow = MutableStateFlow<UiNotification?>(null)
    val notificationFlow: StateFlow<UiNotification?> get() = _notificationEventFlow

    fun addNotification(event: UiNotification) {
        _notificationEventFlow.value = event
    }
}