package com.dk.piley.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.calendar.TaskCalendarSyncManager
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.RecurringTimeRange
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.reminder.DelayRange
import com.dk.piley.reminder.INotificationManager
import com.dk.piley.reminder.IReminderManager
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.util.dateTimeString
import com.dk.piley.util.descriptionCharacterLimit
import com.dk.piley.util.titleCharacterLimit
import com.dk.piley.util.toInstantWithOffset
import com.dk.piley.util.toLocalDateTime
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Task detail view model
 *
 * @property repository task repository entity
 * @property reminderManager user reminder manager entity
 * @property notificationManager user notification manager entity
 * @property calendarSyncManager task calendar sync manager entity
 *
 * @param savedStateHandle saved state handle to receive task id passed through navigation
 */
class TaskDetailViewModel(
    private val repository: TaskRepository,
    private val pileRepository: PileRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val reminderManager: IReminderManager,
    private val notificationManager: INotificationManager,
    private val calendarSyncManager: TaskCalendarSyncManager,
    savedStateHandle: SavedStateHandle
) : StatefulViewModel<TaskDetailViewState>(TaskDetailViewState()) {

    init {

        viewModelScope.launch {
            val id = savedStateHandle.get<Long>(taskScreen.identifier)
            val isDelay = savedStateHandle.get<Boolean>("delay") ?: false
            if (isDelay) dismissDelayNotification(id)
            // set initial values for text fields
            id?.let { taskId ->
                val piles = pileRepository.getPilesWithTasks().firstOrNull()
                    ?.map { Pair(it.pile.pileId, it.pile.name) }
                    ?: emptyList()
                repository.getTaskById(taskId).firstOrNull()?.let { task ->
                    val user = userRepository.getSignedInUserEntity()
                    val selectionIndex = piles.indexOfFirst { it.first == task.pileId }
                    state.value = state.value.copy(
                        titleTextValue = task.title,
                        descriptionTextValue = task.description,
                        piles = piles,
                        selectedPileIndex = if (selectionIndex == -1) 0 else selectionIndex,
                        showDelaySection = isDelay,
                        defaultDelayRange = user?.defaultReminderDelayRange ?: DelayRange.Minute,
                        defaultDelayIndex = user?.defaultReminderDelayIndex ?: 0
                    )
                }
            }
            // observe changed values and update state accordingly
            id?.let { repository.getTaskById(it) }?.collect { it ->
                it?.let { task ->
                    state.update {
                        it.copy(
                            task = task,
                            reminderDateTimeText = task.reminder?.toLocalDateTime()
                                ?.dateTimeString()
                        )
                    }
                }
            }
        }
    }

    private fun dismissDelayNotification(id: Long?) {
        id?.let { notificationManager.dismiss(taskId = it) }
    }

    /**
     * Delete task
     *
     */
    fun deleteTask() {
        viewModelScope.launch {
            repository.insertTaskWithStatus(state.value.task.copy(status = TaskStatus.DELETED))
        }
    }

    /**
     * Complete task
     *
     */
    fun completeTask() {
        viewModelScope.launch {
            repository.insertTaskWithStatus(state.value.task.copy(status = TaskStatus.DONE))
        }
    }

    /**
     * Add reminder using state of user reminder selections
     *
     * @param reminderState reminder selection state containing parameters set by user when setting reminder
     */
    fun addReminder(reminderState: ReminderState) {
        state.update {
            it.copy(reminderDateTimeText = reminderState.reminder.dateTimeString())
        }
        viewModelScope.launch {
            val task = state.value.task.copy(
                reminder = reminderState.reminder.toInstantWithOffset(),
                isRecurring = reminderState.recurring,
                recurringFrequency = reminderState.recurringFrequency,
                recurringTimeRange = reminderState.recurringTimeRange,
                nowAsReminderTime = reminderState.nowAsReminderTime,
            )
            repository.insertTask(task)
            // dismiss existing alarms and notification for this task
            dismissAlarmAndNotification()
            // start new reminder
            reminderManager.startReminder(
                reminderTime = reminderState.reminder.toInstantWithOffset(),
                task = task,
                actionTitles = taskRepository.getReminderActionTitles()
            )
            if (reminderState.syncWithCalendar) {
                calendarSyncManager.addReminder(task)
            }
        }
    }

    /**
     * Cancel reminder
     *
     */
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
            // dismiss alarms and notifications related to this task
            dismissAlarmAndNotification()
        }
    }

    /**
     * Cancels system alarm and dismisses notifications associated with this task
     *
     */
    private fun dismissAlarmAndNotification() {
        reminderManager.cancelReminder(state.value.task.id)
        notificationManager.dismiss(state.value.task.id)
    }

    /**
     * Edit task description if it does not exceed character limit
     *
     * @param desc description text value
     */
    fun editDescription(desc: String) {
        if (desc.length > descriptionCharacterLimit) return
        state.update {
            it.copy(descriptionTextValue = desc)
        }
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(description = desc))
        }
    }

    /**
     * Edit task title if it does not exceed character limit
     *
     * @param title title text value
     */
    fun editTitle(title: String) {
        if (title.length > titleCharacterLimit || title.isEmpty()) return
        state.update {
            it.copy(titleTextValue = title)
        }
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(title = title))
        }
    }

    /**
     * Select pile of this task
     *
     * @param index index of the selected pile
     */
    fun selectPile(index: Int) {
        viewModelScope.launch {
            repository.insertTask(state.value.task.copy(pileId = state.value.piles[index].first))
            state.update { it.copy(selectedPileIndex = index) }
        }
    }

    fun delayReminder(durationInMinutes: Long) {
        viewModelScope.launch {
            if (durationInMinutes > 0) {
                taskRepository.delayTask(state.value.task, durationInMinutes)
            }
            state.update { it.copy(delayFinished = true) }
        }
    }

}

data class TaskDetailViewState(
    val task: Task = Task(),
    val titleTextValue: String = "",
    val descriptionTextValue: String = "",
    val reminderDateTimeText: String? = null,
    val piles: List<Pair<Long, String>> = emptyList(),
    val selectedPileIndex: Int = 0,
    val showDelaySection: Boolean = false,
    val defaultDelayRange: DelayRange = DelayRange.Minute,
    val defaultDelayIndex: Int = 0,
    val delayFinished: Boolean = false
)