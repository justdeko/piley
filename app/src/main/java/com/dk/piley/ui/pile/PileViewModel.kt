package com.dk.piley.ui.pile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.backup.BackupManager
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PileViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
    private val backupManager: BackupManager,
) : ViewModel() {
    private val _state = MutableStateFlow(PileViewState())
    val state: StateFlow<PileViewState>
        get() = _state

    private val _selectedPileIndex = MutableStateFlow(-1)
    val selectedPileIndex: StateFlow<Int>
        get() = _selectedPileIndex

    init {
        viewModelScope.launch {
            // perform a recurring backup if necessary
            backupManager.performBackupIfNecessary()
            // get piles and start updating state
            userRepository.getSignedInUserNotNull().flatMapLatest { user ->
                pileRepository.getPilesWithTasks().flatMapLatest { pilesWithTasks ->
                    selectedPileIndex.map { index ->
                        val idTitleList = pilesWithTasks.map {
                            Pair(
                                it.pile.pileId,
                                it.pile.name
                            )
                        }
                        val selectedPileIndex =
                            idTitleList.indexOfFirst { it.first == user.selectedPileId }
                        if (selectedPileIndex != -1) {
                            onPileChanged(selectedPileIndex)
                        }
                        // set index if needed
                        val selectedPileId =
                            idTitleList.getOrNull(index) ?: user.selectedPileId
                        // start mapping pile to view state
                        pilesWithTasks
                            .find { it.pile.pileId == selectedPileId }
                            ?.let { pileWithTasks ->
                                PileViewState(
                                    pile = pileWithTasks.pile,
                                    tasks = pileWithTasks.tasks.filter { task -> task.status == TaskStatus.DEFAULT },
                                    autoHideEnabled = user.autoHideKeyboard,
                                    pileIdTitleList = idTitleList,
                                )
                            }
                    }
                }
            }.collect { viewState ->
                if (viewState != null) {
                    _state.value = viewState
                }
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

    fun onPileChanged(index: Int) {
        _selectedPileIndex.update { index }
    }
}

data class PileViewState(
    val pile: Pile = Pile(),
    val tasks: List<Task> = emptyList(),
    val autoHideEnabled: Boolean = true,
    val pileIdTitleList: List<Pair<Long, String>> = emptyList(),
)
