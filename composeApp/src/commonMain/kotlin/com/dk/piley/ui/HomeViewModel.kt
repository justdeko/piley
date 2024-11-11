package com.dk.piley.ui

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.navigation.NavigationEventRepository
import com.dk.piley.model.notification.NotificationRepository
import com.dk.piley.model.notification.UiNotification
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val notificationRepository: NotificationRepository,
    private val navigationEventRepository: NavigationEventRepository
) : StatefulViewModel<HomeViewState>(HomeViewState()) {
    init {
        viewModelScope.launch {
            val notificationFlow = notificationRepository.notificationFlow
            val navigationEventFlow = navigationEventRepository.navigationEventFlow
            collectState(
                notificationFlow.combine(navigationEventFlow) { notification, navigationEvent ->
                    HomeViewState(
                        notification?.let { transformNotificationToMessage(it) },
                        navigationEvent?.destination
                    )
                }
            )
        }
    }

    private suspend fun transformNotificationToMessage(notification: UiNotification): String {
        val task: Task = taskRepository.getTaskById(notification.taskId).firstOrNull() ?: return ""
        return task.title + if (task.description.isNotBlank()) " - ${task.description}" else ""
    }
}

data class HomeViewState(
    val message: String? = null,
    val navigationEvent: String? = null
)