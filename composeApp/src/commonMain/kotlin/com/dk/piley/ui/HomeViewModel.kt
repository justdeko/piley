package com.dk.piley.ui

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.navigation.NavigationEvent
import com.dk.piley.model.navigation.NavigationEventRepository
import com.dk.piley.model.navigation.Shortcut
import com.dk.piley.model.navigation.ShortcutEventRepository
import com.dk.piley.model.notification.NotificationRepository
import com.dk.piley.model.notification.UiNotification
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.user.UserRepository
import com.dk.piley.ui.nav.Screen
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

class HomeViewModel(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val notificationRepository: NotificationRepository,
    private val navigationEventRepository: NavigationEventRepository,
    private val shortcutEventRepository: ShortcutEventRepository,
) : StatefulViewModel<HomeViewState>(
    HomeViewState(
        skipSplashScreen = userRepository.getSkipSplashScreen()
    )
) {
    init {
        viewModelScope.launch {
            val notificationFlow = notificationRepository.notificationFlow
            val navigationEventFlow = navigationEventRepository.navigationEventFlow
            val skipSplashScreen = userRepository.getSkipSplashScreen()
            if (skipSplashScreen) {
                runLaunchTasks()
            }
            collectState(
                notificationFlow.combine(navigationEventFlow) { notification, navigationEvent ->
                    HomeViewState(
                        message = notification?.let { transformNotificationToMessage(it) },
                        navigationEvent = navigationEvent?.destination,
                        skipSplashScreen = skipSplashScreen
                    )
                }
            )
        }
        observeShortcuts()
    }

    private fun observeShortcuts() {
        viewModelScope.launch {
            shortcutEventRepository.keyEventFlow.collect { shortcut ->
                when (shortcut) {
                    Shortcut.Pile -> navigationEventRepository.addNavigationEvent(
                        NavigationEvent(Screen.Pile.route)
                    )

                    Shortcut.Piles -> navigationEventRepository.addNavigationEvent(
                        NavigationEvent(Screen.Piles.route)
                    )

                    Shortcut.Profile -> navigationEventRepository.addNavigationEvent(
                        NavigationEvent(Screen.Profile.route)
                    )

                    Shortcut.Settings -> navigationEventRepository.addNavigationEvent(
                        NavigationEvent(Screen.Settings.route)
                    )

                    else -> {}
                }
            }
        }
    }

    private fun runLaunchTasks() {
        taskRepository.restartAlarms()
    }

    private suspend fun transformNotificationToMessage(notification: UiNotification): String {
        val task: Task = taskRepository.getTaskById(notification.taskId).firstOrNull() ?: return ""
        return task.title + if (task.description.isNotBlank()) " - ${task.description}" else ""
    }

    fun onConsumeNavEvent() {
        viewModelScope.launch {
            navigationEventRepository.clear()
        }
    }
}

data class HomeViewState(
    val message: String? = null,
    val navigationEvent: String? = null,
    val skipSplashScreen: Boolean = false
)