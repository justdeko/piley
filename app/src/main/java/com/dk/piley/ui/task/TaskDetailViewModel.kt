package com.dk.piley.ui.task

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.ui.nav.taskScreen
import com.dk.piley.ui.util.dateTimeString
import com.dk.piley.ui.util.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class TaskDetailViewModel @Inject constructor(
    private val repository: TaskRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(TaskDetailViewState())

    val state: StateFlow<TaskDetailViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>(taskScreen.identifier)
            id?.let { repository.getTaskById(it) }?.collect { task ->
                _state.value = TaskDetailViewState(
                    task,
                    task.description,
                    task.reminder?.toLocalDateTime()?.dateTimeString()
                )
            }
        }
    }

    fun deleteTask() {
        viewModelScope.launch {
            repository.insertTask(state.value.task.apply {
                status = TaskStatus.DELETED
            })
        }
    }

    fun completeTask() {
        viewModelScope.launch {
            repository.insertTask(state.value.task.apply {
                status = TaskStatus.DONE
            })
        }
    }

    fun addReminder(date: Date) {
        _state.update {
            it.copy(reminderDateTimeText = date.toLocalDateTime()?.dateTimeString())
        }
        viewModelScope.launch {
            repository.insertTask(state.value.task.apply {
                reminder = date
            })
        }
    }

    fun editDescription(desc: String) {
        _state.update {
            it.copy(descriptionTextValue = desc)
        }
        viewModelScope.launch {
            repository.insertTask(state.value.task.apply {
                description = desc
            })
        }
    }

}

data class TaskDetailViewState(
    val task: Task = Task(),
    val descriptionTextValue: String = "",
    val reminderDateTimeText: String? = null
)