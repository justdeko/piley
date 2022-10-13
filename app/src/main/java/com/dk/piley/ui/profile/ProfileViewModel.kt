package com.dk.piley.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())

    val state: StateFlow<ProfileViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val tasksFlow = taskRepository.getTasks()
            // TODO: remove hardcoded
            val userFlow = userRepository.getUserById(1)
            userFlow.combine(tasksFlow) { user, tasks ->
                val done = tasks.count { it.status == TaskStatus.DONE }
                val deleted = tasks.count { it.status == TaskStatus.DELETED }
                val current = tasks.count { it.status == TaskStatus.DEFAULT }

                ProfileViewState(user.name ?: "", done, deleted, current)
            }.collect { _state.value = it }
        }
    }
}


data class ProfileViewState(
    val userName: String = "",
    val doneTasks: Int = 0,
    val deletedTasks: Int = 0,
    val currentTasks: Int = 0,
)