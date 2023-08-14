package com.dk.piley.ui.pile

import androidx.lifecycle.viewModelScope
import com.dk.piley.backup.BackupManager
import com.dk.piley.common.StatefulViewModel
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
import java.time.Instant
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PileViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
    private val backupManager: BackupManager,
) : StatefulViewModel<PileViewState>(PileViewState()) {

    private val _selectedPileIndex = MutableStateFlow(-1)
    val selectedPileIndex: StateFlow<Int>
        get() = _selectedPileIndex

    private var differsFromSelected = false

    init {
        viewModelScope.launch {
            // perform a recurring backup if necessary
            backupManager.performBackupIfNecessary()
            // get piles and start updating state
            userRepository.getSignedInUserNotNullFlow().flatMapLatest { user ->
                pileRepository.getPilesWithTasks().flatMapLatest { pilesWithTasks ->
                    selectedPileIndex.map { index ->
                        val idTitleList = pilesWithTasks.map {
                            Pair(
                                it.pile.pileId,
                                it.pile.name
                            )
                        }
                        if (!differsFromSelected) {
                            val selectedPileIndex =
                                idTitleList.indexOfFirst { it.first == user.selectedPileId }
                            if (selectedPileIndex != -1) {
                                onPileChanged(selectedPileIndex, false)
                            }
                        }
                        // set index if needed
                        val selectedPileId =
                            idTitleList.getOrNull(index)?.first ?: user.selectedPileId
                        // start mapping pile to view state
                        pilesWithTasks
                            .find { it.pile.pileId == selectedPileId }
                            ?.let { pileWithTasks ->
                                state.value.copy(
                                    pile = pileWithTasks.pile,
                                    tasks = pileWithTasks.tasks.filter { task -> task.status == TaskStatus.DEFAULT },
                                    autoHideEnabled = user.autoHideKeyboard,
                                    pileIdTitleList = idTitleList,
                                    noTasksYet = pileWithTasks.tasks.isEmpty()
                                )
                            }
                    }
                }
            }.collect { viewState ->
                if (viewState != null) {
                    state.value = viewState
                }
            }
        }
    }

    fun add(text: String) {
        viewModelScope.launch {
            taskRepository.insertTask(
                Task(
                    title = text.trim(),
                    pileId = state.value.pile.pileId,
                    createdAt = Instant.now(),
                    modifiedAt = Instant.now()
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

    fun onPileChanged(index: Int, setDiffersFromSelected: Boolean = true) {
        differsFromSelected = setDiffersFromSelected
        _selectedPileIndex.update { index }
    }

    fun setMessage(message: String?) {
        state.update { it.copy(message = message) }
    }
}

data class PileViewState(
    val pile: Pile = Pile(),
    val tasks: List<Task>? = null,
    val autoHideEnabled: Boolean = true,
    val pileIdTitleList: List<Pair<Long, String>> = emptyList(),
    val noTasksYet: Boolean = false,
    val message: String? = null
)
