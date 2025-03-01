package com.dk.piley.ui.piles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileColor
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.UserRepository
import com.dk.piley.ui.nav.pileScreen
import com.dk.piley.util.descriptionCharacterLimit
import com.dk.piley.util.getCompletedTasksForWeekValues
import com.dk.piley.util.pileTitleCharacterLimit
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Pile detail view model
 *
 * @property pileRepository pile repository instance
 * @property taskRepository task repository instance
 * @property userRepository user repository instance
 *
 * @param savedStateHandle saved state handle to receive pile id passed through navigation
 */
class PileDetailViewModel(
    private val pileRepository: PileRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : StatefulViewModel<PileDetailViewState>(PileDetailViewState()) {

    private val signedInUserFlow = userRepository.getSignedInUserNotNullFlow()

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>(pileScreen.identifier)
            // set initial values for text fields
            id?.let { pileId ->
                pileRepository.getPileById(pileId).firstOrNull()?.let {
                    state.value = state.value.copy(
                        titleTextValue = it.pile.name,
                        descriptionTextValue = it.pile.description
                    )
                }
            }
            // observe changed values and update state accordingly
            id?.let { pileRepository.getPileById(it) }?.collect { pileWithTasks ->
                signedInUserFlow.take(1).collect { user ->
                    state.update { pileDetailViewState ->
                        pileDetailViewState.copy(
                            pile = pileWithTasks.pile,
                            completedTaskCounts = getCompletedTasksForWeekValues(pileWithTasks),
                            modifiedTasks = pileWithTasks.tasks
                                // TODO  || it.isRecurring && it.status == TaskStatus.DELETED
                                // option to recover reminder as well
                                .filter {
                                    !it.isRecurring
                                            && (it.status == TaskStatus.DONE
                                            || it.status == TaskStatus.DELETED
                                            )
                                }.sortedByDescending { it.modifiedAt }.take(3),
                            doneCount = pileWithTasks.tasks.count { it.status == TaskStatus.DONE },
                            currentCount = pileWithTasks.tasks.count { it.status == TaskStatus.DEFAULT },
                            recurringCount = pileWithTasks.tasks.count { it.isRecurring },
                            canDelete = id != user.defaultPileId
                        )
                    }
                }
            }
        }
    }

    fun deletePile() {
        viewModelScope.launch {
            signedInUserFlow.take(1).collect {
                userRepository.insertUser(it.copy(selectedPileId = it.defaultPileId))
                pileRepository.deletePile(state.value.pile)
            }
        }
    }

    fun editTitle(title: String) {
        if (title.length > pileTitleCharacterLimit || title.isEmpty()) return
        viewModelScope.launch {
            state.update {
                it.copy(titleTextValue = title)
            }
            pileRepository.updatePile(state.value.pile.copy(name = title))
        }
    }

    fun setPileMode(pileMode: PileMode) {
        viewModelScope.launch {
            pileRepository.updatePile(state.value.pile.copy(pileMode = pileMode))
        }
    }

    fun editDescription(description: String) {
        if (description.length > descriptionCharacterLimit) return
        state.update {
            it.copy(descriptionTextValue = description)
        }
        viewModelScope.launch {
            pileRepository.updatePile(state.value.pile.copy(description = description))
        }
    }

    fun setPileLimit(limit: Int) {
        viewModelScope.launch {
            pileRepository.updatePile(state.value.pile.copy(pileLimit = limit))
        }
    }

    fun clearStatistics() {
        viewModelScope.launch {
            taskRepository.deleteAllCompletedDeletedTasksForPile(state.value.pile.pileId)
        }
    }

    fun undoTask(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTaskWithStatus(
                task = task.copy(status = TaskStatus.DEFAULT),
                undo = true
            )
        }
    }

    fun selectColor(color: PileColor) {
        viewModelScope.launch {
            pileRepository.updatePile(state.value.pile.copy(color = color))
        }
    }
}

data class PileDetailViewState(
    val pile: Pile = Pile(),
    val completedTaskCounts: List<Int> = emptyList(),
    val modifiedTasks: List<Task> = emptyList(),
    val doneCount: Int = 0,
    val currentCount: Int = 0,
    val recurringCount: Int = 0,
    val titleTextValue: String = "",
    val descriptionTextValue: String = "",
    val canDelete: Boolean = true
)
