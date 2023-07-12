package com.dk.piley.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.backup.BackupManager
import com.dk.piley.model.common.Resource
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.getAverageTaskCompletionInHours
import com.dk.piley.util.getBiggestPile
import com.dk.piley.util.getUpcomingTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.threeten.bp.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
    private val backupManager: BackupManager
) : ViewModel() {
    private val _state = MutableStateFlow(ProfileViewState())

    val state: StateFlow<ProfileViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val pileFlow = pileRepository.getPilesWithTasks()
            val userFlow = userRepository.getSignedInUserNotNullFlow()
            userFlow.combine(pileFlow) { user, pilesWithTasks ->
                val tasks = pilesWithTasks.flatMap { it.tasks }
                val done = tasks.count { it.status == TaskStatus.DONE }
                val deleted = tasks.count { it.status == TaskStatus.DELETED }
                val current = tasks.count { it.status == TaskStatus.DEFAULT }

                ProfileViewState(
                    userName = user.name,
                    lastBackup = user.lastBackup,
                    doneTasks = done,
                    deletedTasks = deleted,
                    currentTasks = current,
                    upcomingTaskList = getUpcomingTasks(pilesWithTasks),
                    biggestPileName = getBiggestPile(pilesWithTasks),
                    averageTaskDurationInHours = getAverageTaskCompletionInHours(pilesWithTasks),
                    userIsOffline = user.isOffline
                )
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
                        signOutLocally()
                    }

                    is Resource.Failure -> {
                        setSignedOutState(SignOutState.SIGNED_OUT_ERROR)
                    }
                }
            }

        }
    }

    private fun setShowProgressbar(visible: Boolean) =
        _state.update { it.copy(showProgressBar = visible) }

    fun setSignedOutState(state: SignOutState) = _state.update { it.copy(signedOutState = state) }
    fun setToastMessage(message: String?) = _state.update { it.copy(toastMessage = message) }

    fun attemptBackup() {
        viewModelScope.launch {
            setShowProgressbar(true)
            val successful = backupManager.doBackup()
            setShowProgressbar(false)
            if (successful) {
                setToastMessage("Backup upload successful")
            } else {
                setToastMessage("Failed to upload backup")
            }
        }
    }

    fun signOutAfterError() = viewModelScope.launch { signOutLocally() }

    private suspend fun signOutLocally() {
        pileRepository.deletePileData()
        userRepository.setSignedInUser("")
        userRepository.deleteUserTable()
        _state.update {
            it.copy(
                toastMessage = "Signed out!",
                signedOutState = SignOutState.SIGNED_OUT
            )
        }
    }
}


data class ProfileViewState(
    val userName: String = "",
    val lastBackup: LocalDateTime? = null,
    val doneTasks: Int = 0,
    val deletedTasks: Int = 0,
    val currentTasks: Int = 0,
    val upcomingTaskList: List<Pair<String, Task>> = emptyList(),
    val averageTaskDurationInHours: Long = 0,
    val biggestPileName: String = "None",
    val signedOutState: SignOutState = SignOutState.SIGNED_IN,
    val showProgressBar: Boolean = false,
    val toastMessage: String? = null,
    val userIsOffline: Boolean = false
)

enum class SignOutState {
    SIGNED_IN, SIGNING_OUT, SIGNED_OUT, SIGNED_OUT_ERROR
}