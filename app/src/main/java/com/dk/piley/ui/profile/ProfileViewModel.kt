package com.dk.piley.ui.profile

import android.app.Application
import androidx.lifecycle.viewModelScope
import com.dk.piley.R
import com.dk.piley.backup.BackupManager
import com.dk.piley.common.StatefulAndroidViewModel
import com.dk.piley.model.common.Resource
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.Task
import com.dk.piley.model.task.TaskStatus
import com.dk.piley.model.user.UserRepository
import com.dk.piley.util.getAverageTaskCompletionInHours
import com.dk.piley.util.getBiggestPileName
import com.dk.piley.util.getUpcomingTasks
import com.dk.piley.util.toLocalDateTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * Profile view model
 *
 * @property application generic application context
 * @property pileRepository pile repository instance
 * @property userRepository user repository instance
 * @property backupManager user backup manager
 */
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val application: Application,
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
    private val backupManager: BackupManager
) : StatefulAndroidViewModel<ProfileViewState>(application, ProfileViewState()) {

    init {
        viewModelScope.launch {
            // one-time script to delete deleted tasks
            deleteDeletedTasks()
            // instantiate flows
            val pileFlow = pileRepository.getPilesWithTasks()
            val userFlow = userRepository.getSignedInUserNotNullFlow()
            userFlow.combine(pileFlow) { user, pilesWithTasks ->
                val tasks = pilesWithTasks.flatMap { it.tasks }
                val done = tasks.count { it.status == TaskStatus.DONE }
                val deleted = pilesWithTasks.sumOf { it.pile.deletedCount }
                val current = tasks.count { it.status == TaskStatus.DEFAULT }

                state.value.copy(
                    userName = user.name,
                    lastBackup = user.lastBackup?.toLocalDateTime(),
                    doneTasks = done,
                    deletedTasks = deleted,
                    currentTasks = current,
                    upcomingTaskList = getUpcomingTasks(pilesWithTasks),
                    biggestPileName = getBiggestPileName(pilesWithTasks, application),
                    averageTaskDurationInHours = getAverageTaskCompletionInHours(pilesWithTasks),
                    userIsOffline = user.isOffline
                )
            }.collect { state.value = it }
        }
    }

    /**
     * Delete tasks with status deleted forever
     *
     */
    private suspend fun deleteDeletedTasks() {
        val piles = pileRepository.getPilesWithTasks().firstOrNull()
        // updated all piles with deleted count
        piles?.forEach { pileWithTasks ->
            val deletedCount =
                pileWithTasks.pile.deletedCount + pileWithTasks.tasks.count { it.status == TaskStatus.DELETED }
            pileRepository.insertPile(pileWithTasks.pile.copy(deletedCount = deletedCount))
        }
        // delete all deleted tasks
        pileRepository.deleteDeletedTasks()
    }

    /**
     * Sign out user
     *
     */
    fun signOut() {
        viewModelScope.launch {
            backupManager.pushBackupToRemoteForUserFlow().collectLatest {
                when (it) {
                    is Resource.Loading -> {
                        setSignedOutState(SignOutState.SIGNING_OUT)
                    }

                    is Resource.Success -> {
                        // on successful backup upload, sign out user locally
                        signOutLocally()
                    }

                    is Resource.Failure -> {
                        setSignedOutState(SignOutState.SIGNED_OUT_ERROR)
                    }
                }
            }

        }
    }

    /**
     * Set progressbar visibility
     *
     * @param visible whether the progress bar is visible
     */
    private fun setShowProgressbar(visible: Boolean) =
        state.update { it.copy(isLoading = visible) }

    /**
     * Set signed out state
     *
     * @param signOutState new sign out state
     */
    fun setSignedOutState(signOutState: SignOutState) =
        state.update { it.copy(signedOutState = signOutState) }

    /**
     * Set user message
     *
     * @param message message text
     */
    fun setMessage(message: String?) = state.update { it.copy(message = message) }

    /**
     * Attempt a backup. Set the progressbar to visible and then perform a backup request.
     * Based on the result show the corresponding message to the user
     *
     */
    fun attemptBackup() {
        viewModelScope.launch {
            setShowProgressbar(true)
            val successful = backupManager.doBackup()
            setShowProgressbar(false)
            if (successful) {
                setMessage(application.getString(R.string.backup_upload_successful_message))
            } else {
                setMessage(application.getString(R.string.backup_upload_error_message))
            }
        }
    }

    /**
     * Sign out after error by signing out locally
     *
     */
    fun signOutAfterError() = viewModelScope.launch { signOutLocally() }

    /**
     * Sign out locally by deleting all tables and setting the signed in user preference to null.
     *
     */
    private suspend fun signOutLocally() {
        state.update {
            it.copy(
                signedOutState = SignOutState.SIGNED_OUT,
                message = application.getString(R.string.sign_out_successful_message)
            )
        }
        pileRepository.deletePileData()
        userRepository.setSignedInUser("")
        userRepository.setSignedOut(true)
        userRepository.deleteUserTable()
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
    val isLoading: Boolean = false,
    val message: String? = null,
    val userIsOffline: Boolean = false
)

enum class SignOutState {
    SIGNED_IN, SIGNING_OUT, SIGNED_OUT, SIGNED_OUT_ERROR
}