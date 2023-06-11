package com.dk.piley.ui.piles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.UserRepository
import com.dk.piley.ui.nav.pileScreen
import com.dk.piley.util.getCompletedTasksForWeekValues
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PileDetailViewModel @Inject constructor(
    private val pileRepository: PileRepository,
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(PileDetailViewState())

    val state: StateFlow<PileDetailViewState>
        get() = _state

    private val signedInUserFlow = userRepository.getSignedInUserNotNullFlow()

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>(pileScreen.identifier)
            id?.let { pileRepository.getPileById(it) }?.collect { pileWithTasks ->
                signedInUserFlow.take(1).collect { user ->
                    _state.value = PileDetailViewState(
                        pile = pileWithTasks.pile,
                        completedTaskCounts = getCompletedTasksForWeekValues(pileWithTasks),
                        doneCount = pileWithTasks.tasks.count { it.status == TaskStatus.DONE },
                        deletedCount = pileWithTasks.tasks.count { it.status == TaskStatus.DELETED },
                        currentCount = pileWithTasks.tasks.count { it.status == TaskStatus.DEFAULT },
                        titleTextValue = pileWithTasks.pile.name,
                        descriptionTextValue = pileWithTasks.pile.description,
                        canDeleteOrEdit = id != user.defaultPileId
                    )
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
        if (title.length <= 20) {
            viewModelScope.launch {
                _state.update {
                    it.copy(titleTextValue = title)
                }
                pileRepository.insertPile(state.value.pile.copy(name = title))
            }
        }
    }

    fun setPileMode(pileMode: PileMode) {
        viewModelScope.launch {
            pileRepository.insertPile(state.value.pile.copy(pileMode = pileMode))
        }
    }

    fun editDescription(description: String) {
        _state.update {
            it.copy(descriptionTextValue = description)
        }
        viewModelScope.launch {
            pileRepository.insertPile(state.value.pile.copy(description = description))
        }
    }

    fun setPileLimit(limit: Int) {
        viewModelScope.launch {
            pileRepository.insertPile(state.value.pile.copy(pileLimit = limit))
        }
    }

    fun clearStatistics() {
        viewModelScope.launch {
            taskRepository.deleteAllCompletedDeletedTasksForPile(state.value.pile.pileId)
        }
    }
}

data class PileDetailViewState(
    val pile: Pile = Pile(),
    val completedTaskCounts: List<Int> = emptyList(),
    val doneCount: Int = 0,
    val deletedCount: Int = 0,
    val currentCount: Int = 0,
    val titleTextValue: String = "",
    val descriptionTextValue: String = "",
    val canDeleteOrEdit: Boolean = true
)
