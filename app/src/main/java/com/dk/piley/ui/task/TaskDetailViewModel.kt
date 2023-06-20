package com.dk.piley.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.util.dateTimeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val reminderManager: ReminderManager,
    private val notificationManager: NotificationManager,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(TaskDetailViewState())

    val state: StateFlow<TaskDetailViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>(taskScreen.identifier)
            id?.let { repository.getTaskById(it) }?.collect { task ->
                _state.value = TaskDetailViewState(
                    task, task.description, task.reminder?.dateTimeString()
                )
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(status = TaskStatus.DELETED))
        }
    }

    fun completeTask() {
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(status = TaskStatus.DONE))
        }
    }

    fun addReminder(reminderDateTime: LocalDateTime) {
        Timber.d("adding reminder for time $reminderDateTime")
        _state.update {
            it.copy(reminderDateTimeText = reminderDateTime.dateTimeString())
        }
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(reminder = reminderDateTime))
            dismissAlarmAndNotification()
            reminderManager.startReminder(reminderDateTime, state.value.task.id)
        }
    }

    fun cancelReminder() {
        _state.update {
            it.copy(reminderDateTimeText = null)
        }
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(reminder = null))
            dismissAlarmAndNotification()
        }
    }

    private fun dismissAlarmAndNotification() {
        reminderManager.cancelReminder(state.value.task.id)
        notificationManager.dismiss(state.value.task.id)
    }

    fun editDescription(desc: String) {
        _state.update {
            it.copy(descriptionTextValue = desc)
        }
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(description = desc))
        }
    }

}

data class TaskDetailViewState(
    val task: Task = Task(),
    val descriptionTextValue: String = "",
    val reminderDateTimeText: String? = null
)