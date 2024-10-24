package com.dk.piley.ui.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulAndroidViewModel
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.getBiggestPileName
import com.dk.piley.util.getTasksCompletedInPastDays
import com.dk.piley.util.getUpcomingTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Profile view model
 *
 * @property application generic application context
 * @property pileRepository pile repository instance
 * @property userRepository user repository instance
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val application: Application,
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
) : StatefulAndroidViewModel<ProfileViewState>(application, ProfileViewState()) {

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
                val deleted = pilesWithTasks.sumOf { it.pile.deletedCount }
                val current = tasks.count { it.status == TaskStatus.DEFAULT }

                state.value.copy(
                    userName = user.name,
                    doneTasks = done,
                    deletedTasks = deleted,
                    currentTasks = current,
                    upcomingTaskList = getUpcomingTasks(pilesWithTasks),
                    biggestPileName = getBiggestPileName(pilesWithTasks, application),
                    tasksCompletedPastDays = getTasksCompletedInPastDays(tasks),
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
            val piles = pileRepository.getPilesWithTasks().firstOrNull()
            // updated all piles with deleted count
            piles?.forEach { pileWithTasks ->
                val deletedCount =
                    pileWithTasks.pile.deletedCount + pileWithTasks.tasks.count { it.status == TaskStatus.DELETED }
                pileRepository.updatePile(pileWithTasks.pile.copy(deletedCount = deletedCount))
            }
            // delete all deleted tasks
            pileRepository.deleteDeletedTasks()
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
    val deletedTasks: Int = 0,
    val currentTasks: Int = 0,
    val upcomingTaskList: List<Pair<String, Task>> = emptyList(),
    val tasksCompletedPastDays: Int = 0,
    val biggestPileName: String = "None",
    val isLoading: Boolean = false,
    val message: String? = null,
    val userIsOffline: Boolean = false
)