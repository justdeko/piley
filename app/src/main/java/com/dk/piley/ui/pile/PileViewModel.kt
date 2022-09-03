package com.dk.piley.ui.pile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class PileViewModel @Inject constructor(
    private val repository: TaskRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(PileViewState())

    val state: StateFlow<PileViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val tasksFlow = repository.getTasks()
            combine(tasksFlow) { (tasks) ->
                PileViewState(tasks.filter { it.status == TaskStatus.DEFAULT })
            }.collect { _state.value = it }
        }
    }

    fun add(text: String) {
        viewModelScope.launch {
            repository.insertTask(
                Task(
                    title = text,
                    createdAt = LocalDateTime.now(),
                    modifiedAt = LocalDateTime.now()
                )
            )
        }
    }

    fun done(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task.apply {
                status = TaskStatus.DONE
            })
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            repository.insertTask(task.apply {
                status = TaskStatus.DELETED
            })
        }
    }
}

data class PileViewState(
    val tasks: List<Task> = emptyList()
)
