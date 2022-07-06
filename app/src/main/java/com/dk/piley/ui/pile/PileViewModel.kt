package com.dk.piley.ui.pile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
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
                PileViewState(tasks)
            }.collect { _state.value = it }
        }
    }

    fun add(text: String) {
        viewModelScope.launch {
            repository.insertTask(
                Task(
                    title = text,
                    createdAt = System.currentTimeMillis(),
                    modifiedAt = System.currentTimeMillis()
                )
            )
        }
    }

    fun delete(task: Task) {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
    }
}

data class PileViewState(
    val tasks: List<Task> = emptyList()
)
