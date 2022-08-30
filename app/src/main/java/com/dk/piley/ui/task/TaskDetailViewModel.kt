package com.dk.piley.ui.task

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository
) : ViewModel() {
    private val _state = MutableStateFlow(TaskDetailViewState())

    val state: StateFlow<TaskDetailViewState>
        get() = _state

    fun setTask(taskId: Long) {
        viewModelScope.launch {
            val tasksFlow = repository.getTaskById(taskId)
            tasksFlow.collect {
                _state.value = TaskDetailViewState(it)
            }
        }
    }

}

data class TaskDetailViewState(
    val task: Task = Task()
)