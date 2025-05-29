package com.dk.piley.ui.profile

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.getBiggestPileName
import com.dk.piley.util.getCompletedTasksForWeekValues
import com.dk.piley.util.getUpcomingTasks
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Profile view model
 *
 * @property pileRepository pile repository instance
 * @property userRepository user repository instance
 */
class ProfileViewModel(
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
) : StatefulViewModel<ProfileViewState>(ProfileViewState()) {

    init {
        viewModelScope.launch {
            // one-time script to delete deleted tasks
            if (!userRepository.getTasksDeleted()) {
                deleteDeletedTasks()
            }
            // instantiate flows
            val pileFlow = pileRepository.getPilesWithTasks()
            val userFlow = userRepository.getSignedInUserNotNullFlow()
            userFlow.combine(pileFlow) { user, pilesWithTasks ->
                val tasks = pilesWithTasks.flatMap { it.tasks }
                val done = tasks.count { it.status == TaskStatus.DONE }
                val recurring =
                    pilesWithTasks.sumOf { pileWithTasks -> pileWithTasks.tasks.count { it.isRecurring } }
                val current = tasks.count { it.status == TaskStatus.DEFAULT }
                val completedTasksForWeekValues = getCompletedTasksForWeekValues(tasks)

                state.value.copy(
                    userName = user.name,
                    doneTasks = done,
                    recurringTasks = recurring,
                    currentTasks = current,
                    upcomingTaskList = getUpcomingTasks(pilesWithTasks),
                    biggestPileName = getBiggestPileName(pilesWithTasks),
                    tasksCompletedPastDays = completedTasksForWeekValues.sum(),
                    completedTaskFrequencies = completedTasksForWeekValues,
                )
            }.collect { state.value = it }
        }
    }

    /**
     * Delete tasks with status deleted forever
     *
     */
    private suspend fun deleteDeletedTasks() {
        // launch in io thread to prevent view blocking
        withContext(Dispatchers.IO) {
            // delete all deleted tasks
            pileRepository.deleteDeletedTasks() // TODO adapt this to last n tasks per pile (to conform to undo button)
            // set preference to true
            userRepository.setTasksDeleted(true)
        }
    }

    /**
     * Set user message
     *
     * @param message message text
     */
    fun setMessage(message: String?) = state.update { it.copy(message = message) }
}


data class ProfileViewState(
    val userName: String = "",
    val doneTasks: Int = 0,
    val recurringTasks: Int = 0,
    val currentTasks: Int = 0,
    val upcomingTaskList: List<Pair<String, Task>> = emptyList(),
    val tasksCompletedPastDays: Int = 0,
    val completedTaskFrequencies: List<Int> = emptyList(),
    val biggestPileName: String? = "None",
    val message: String? = null,
)