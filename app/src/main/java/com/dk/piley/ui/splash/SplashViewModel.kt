package com.dk.piley.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.backup.BackupManager
import com.dk.piley.model.common.Resource
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val backupManager: BackupManager
) : ViewModel() {
    private val _state = MutableStateFlow(SplashViewState())

    val state: StateFlow<SplashViewState>
        get() = _state

    init {
        fetchBackup()
        viewModelScope.launch {
            combine(userRepository.getSignedInUser()) { (user) ->
                SplashViewState(signedIn = user != null)
            }.collect { _state.value = it }
        }
    }

    fun isSignedIn() = state.value.signedIn

    private fun fetchBackup() {
        viewModelScope.launch {
            backupManager.syncBackupToLocalForUserFlow().collectLatest {
                when (it) {
                    is Resource.Loading -> Timber.i("Loading backup")
                    is Resource.Success -> Timber.i("Remote backup request successful, replaced local db: ${it.data}")
                    is Resource.Failure -> Timber.e(it.exception)
                }
            }
        }
    }
}

data class SplashViewState(
    val signedIn: Boolean = false
)
