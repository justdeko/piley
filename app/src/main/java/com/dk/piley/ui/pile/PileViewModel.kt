package com.dk.piley.ui.pile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PileViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(PileViewState())

    val state: StateFlow<PileViewState>
        get() = _state

    init {
        viewModelScope.launch {
            // get piles and start updating state
            userRepository.getSignedInUserNotNull().flatMapLatest { user ->
                pileRepository.getPileById(user.selectedPileId).map { pileWithTasks ->
                    PileViewState(
                        pileWithTasks.pile,
                        pileWithTasks.tasks.filter { task -> task.status == TaskStatus.DEFAULT },
                        user.autoHideKeyboard
                    )
                }
            }.collect {
                _state.value = it
            }
        }
    }

    fun add(text: String) {
        viewModelScope.launch {
            taskRepository.insertTask(
                Task(
                    title = text,
                    pileId = state.value.pile.pileId,
                    createdAt = LocalDateTime.now(),
                    modifiedAt = LocalDateTime.now()
                )
            )
        }
    }

    fun done(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task.copy(status = TaskStatus.DONE))
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTask(task.copy(status = TaskStatus.DELETED))
        }
    }
}

data class PileViewState(
    val pile: Pile = Pile(),
    val tasks: List<Task> = emptyList(),
    val autoHideEnabled: Boolean = true,
)
