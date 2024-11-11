package com.dk.piley.model.notification

data class UiNotification(
    val taskId: Long,
    val type: NotificationType,
)

enum class NotificationType {
    REMINDER
}
