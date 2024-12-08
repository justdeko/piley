package com.dk.piley.ui.pile

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.navigation.Shortcut
import com.dk.piley.model.navigation.ShortcutEventRepository
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.ui.nav.Screen
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

/**
 * Pile view model
 *
 * @property taskRepository task repository instance
 * @property pileRepository pile repository instance
 * @property userRepository user repository instance
 */
@OptIn(ExperimentalCoroutinesApi::class)
class PileViewModel(
    private val taskRepository: TaskRepository,
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
    private val shortcutEventRepository: ShortcutEventRepository,
    savedStateHandle: SavedStateHandle
) : StatefulViewModel<PileViewState>(PileViewState()) {

    private var taskToUndo: Task? = null

    private val _selectedPileIndex = MutableStateFlow(-1)
    val selectedPileIndex: StateFlow<Int>
        get() = _selectedPileIndex

    private var differsFromSelected = false

    init {
        viewModelScope.launch {
            // get piles and start updating state
            userRepository.getSignedInUserNotNullFlow().flatMapLatest { user ->
                // set recurring tasks to shown if user property changes
                setShowRecurring(user.showRecurringTasks)
                pileRepository.getPilesWithTasks().flatMapLatest { pilesWithTasks ->
                    selectedPileIndex.map { index ->
                        val idTitleList = pilesWithTasks.map {
                            Pair(
                                it.pile.pileId,
                                it.pile.name
                            )
                        }
                        // initial default pile selection
                        if (!differsFromSelected) {
                            // set initial selected pile if navigated with pile ID
                            val navigationSelectedPileId =
                                savedStateHandle.get<Long>(Screen.Pile.argument)
                            navigationSelectedPileId?.let { pileId ->
                                // if pile id navigation argument passed, navigate to that pile
                                if (pileId != -1L) {
                                    // calculate index of selected pile
                                    val pileIndex = idTitleList.indexOfFirst { it.first == pileId }
                                    onPileChanged(pileIndex, selectedPileIndex.value == pileIndex)
                                } else {
                                    val pileIndex =
                                        idTitleList.indexOfFirst { it.first == user.selectedPileId }
                                    if (pileIndex != -1) {
                                        onPileChanged(pileIndex, false)
                                    }
                                }
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
                                    pileWithTasks = pileWithTasks,
                                    // only show non-completed tasks and non-deleted recurring tasks
                                    tasks = pileWithTasks.tasks.filter { task ->
                                        (task.status == TaskStatus.DEFAULT)
                                                || (task.isRecurring && task.status != TaskStatus.DELETED)
                                    },
                                    autoHideEnabled = user.autoHideKeyboard,
                                    pileIdTitleList = idTitleList,
                                    noTasksYet = pileWithTasks.tasks.isEmpty(),
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
        observeKeyEvents()
    }

    private fun observeKeyEvents() {
        viewModelScope.launch {
            shortcutEventRepository.keyEventFlow
                .collect { keyEvent ->
                    when (keyEvent) {
                        Shortcut.NavigateLeft -> {
                            if (_selectedPileIndex.value > 0) {
                                onPileChanged(_selectedPileIndex.value - 1)
                            }
                        }

                        Shortcut.NavigateRight -> {
                            if (_selectedPileIndex.value < state.value.pileIdTitleList.lastIndex) {
                                onPileChanged(_selectedPileIndex.value + 1)
                            }
                        }

                        Shortcut.Done -> {
                            state.value.tasks?.last()?.let {
                                taskToUndo = it
                                done(it)
                            }
                        }

                        Shortcut.Delete -> {
                            state.value.tasks?.last()?.let {
                                taskToUndo = it
                                delete(it)
                            }
                        }

                        Shortcut.Undo -> {
                            taskToUndo?.let {
                                undoDelete(it)
                                taskToUndo = null
                            }
                        }

                        else -> {}
                    }
                }
        }
    }

    /**
     * Add a new task to the pile
     *
     * @param text task title
     */
    fun add(text: String) {
        viewModelScope.launch {
            taskRepository.insertTaskWithStatus(
                Task(
                    title = text.trim(),
                    pileId = state.value.pileWithTasks.pile.pileId,
                    createdAt = Clock.System.now(),
                    modifiedAt = Clock.System.now()
                )
            )
        }
    }

    /**
     * Complete task
     *
     * @param task task entity
     */
    fun done(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTaskWithStatus(task.copy(status = TaskStatus.DONE))
        }
    }

    /**
     * Delete task
     *
     * @param task task entity
     */
    fun delete(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTaskWithStatus(task.copy(status = TaskStatus.DELETED))
        }
    }

    /**
     * Undo task deletion
     *
     * @param task the task to undo the deletion for
     */
    fun undoDelete(task: Task) {
        viewModelScope.launch {
            taskRepository.insertTaskWithStatus(task, true)
        }
    }

    /**
     * On pile selection changed
     *
     * @param index selected pile index
     * @param setDiffersFromSelected whether pile differs from currently selected pile
     */
    fun onPileChanged(index: Int, setDiffersFromSelected: Boolean = true) {
        differsFromSelected = setDiffersFromSelected
        _selectedPileIndex.update { index }
    }

    /**
     * Set user message with optional action
     *
     * @param messageWithAction user message with optional action
     */
    fun setMessage(messageWithAction: MessageWithAction?) {
        state.update { it.copy(messageWithAction = messageWithAction) }
    }

    /**
     * Set whether recurring tasks should be shown
     *
     * @param shown the visibility value
     */
    fun setShowRecurring(shown: Boolean) {
        state.update { it.copy(showRecurring = shown) }
    }
}

data class PileViewState(
    val pileWithTasks: PileWithTasks = PileWithTasks(Pile(), emptyList()),
    val tasks: List<Task>? = null,
    val autoHideEnabled: Boolean = true,
    val pileIdTitleList: List<Pair<Long, String>> = emptyList(),
    val noTasksYet: Boolean = false,
    val messageWithAction: MessageWithAction? = null,
    val showRecurring: Boolean = false
)

data class MessageWithAction(
    val message: String,
    val actionText: String? = null,
    val duration: SnackbarDuration = SnackbarDuration.Short,
    val action: () -> Unit = {},
)