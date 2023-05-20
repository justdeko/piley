package com.dk.piley.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.backup.BackupManager
import com.dk.piley.model.common.Resource
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val userRepository: UserRepository,
    private val backupManager: BackupManager
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())

    val state: StateFlow<ProfileViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val tasksFlow = taskRepository.getTasks()
            val userFlow = userRepository.getSignedInUserNotNull()
            userFlow.combine(tasksFlow) { user, tasks ->
                val done = tasks.count { it.status == TaskStatus.DONE }
                val deleted = tasks.count { it.status == TaskStatus.DELETED }
                val current = tasks.count { it.status == TaskStatus.DEFAULT }

                ProfileViewState(user.name, done, deleted, current)
            }.collect { _state.value = it }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            backupManager.pushBackupToRemoteForUserFlow().collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        setSignedOutState(SignOutState.SIGNING_OUT)
                    }

                    is Resource.Success -> {
                        userRepository.setSignedInUser("")
                        backupManager.cancelPeriodicBackup()
                        setSignedOutState(SignOutState.SIGNED_OUT)
                    }

                    is Resource.Failure -> {
                        setSignedOutState(SignOutState.SIGNED_OUT_ERROR)
                    }
                }
            }

        }
    }

    fun setSignedOutState(state: SignOutState) =
        _state.update { it.copy(signedOutState = state) }
}


data class ProfileViewState(
    val userName: String = "",
    val doneTasks: Int = 0,
    val deletedTasks: Int = 0,
    val currentTasks: Int = 0,
    val signedOutState: SignOutState = SignOutState.SIGNED_IN,
)

enum class SignOutState {
    SIGNED_IN, SIGNING_OUT, SIGNED_OUT, SIGNED_OUT_ERROR
}