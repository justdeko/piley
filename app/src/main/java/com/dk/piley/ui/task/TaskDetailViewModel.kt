package com.dk.piley.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.reminder.NotificationManager
import com.dk.piley.reminder.ReminderManager
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.titleCharacterLimit
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository,
    private val reminderManager: ReminderManager,
    private val notificationManager: NotificationManager,
    savedStateHandle: SavedStateHandle
) : StatefulViewModel<TaskDetailViewState>(TaskDetailViewState()) {

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>(taskScreen.identifier)
            // set initial values for text fields
            id?.let { taskId ->
                repository.getTaskById(taskId).firstOrNull()?.let {
                    state.value = state.value.copy(
                        titleTextValue = it.title,
                        descriptionTextValue = it.description
                    )
                }
            }
            // observe changed values and update state accordingly
            id?.let { repository.getTaskById(it) }?.collect { task ->
                state.update {
                    it.copy(
                        task = task,
                        reminderDateTimeText = task.reminder?.dateTimeString()
                    )
                }
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

    fun addReminder(reminderState: ReminderState) {
        Timber.d("adding reminder for state $reminderState")
        state.update {
            it.copy(reminderDateTimeText = reminderState.reminder.dateTimeString())
        }
        viewModelScope.launch {
            repository.insertTask(
                state.value.task.copy(
                    reminder = reminderState.reminder,
                    isRecurring = reminderState.recurring,
                    recurringFrequency = reminderState.recurringFrequency,
                    recurringTimeRange = reminderState.recurringTimeRange,
                )
            )
            dismissAlarmAndNotification()
            reminderManager.startReminder(reminderState.reminder, state.value.task.id)
        }
    }

    fun cancelReminder() {
        state.update {
            it.copy(reminderDateTimeText = null)
        }
        viewModelScope.launch {
            // reset reminder to default values
            repository.insertTask(
                state.value.task.copy(
                    reminder = null,
                    recurringTimeRange = RecurringTimeRange.DAILY,
                    recurringFrequency = 1,
                    isRecurring = false
                )
            )
            dismissAlarmAndNotification()
        }
    }

    private fun dismissAlarmAndNotification() {
        reminderManager.cancelReminder(state.value.task.id)
        notificationManager.dismiss(state.value.task.id)
    }

    fun editDescription(desc: String) {
        state.update {
            it.copy(descriptionTextValue = desc)
        }
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(description = desc))
        }
    }

    fun editTitle(title: String) {
        if (title.length > titleCharacterLimit) return
        state.update {
            it.copy(titleTextValue = title)
        }
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(title = title))
        }
    }

}

data class TaskDetailViewState(
    val task: Task = Task(),
    val titleTextValue: String = "",
    val descriptionTextValue: String = "",
    val reminderDateTimeText: String? = null
)