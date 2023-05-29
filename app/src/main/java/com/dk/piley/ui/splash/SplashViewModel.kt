package com.dk.piley.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.backup.BackupManager
import com.dk.piley.model.common.Resource
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flow
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
        viewModelScope.launch {
            val userEmail = userRepository.getSignedInUserEmail()
            if (userEmail.isNotBlank()) {
                combine(loadingBackupFlow()) { (loadingBackup) ->
                    if (loadingBackup) {
                        Timber.d("loading backup..")
                        SplashViewState(InitState.LOADING_BACKUP)
                    } else {
                        Timber.d("backup loading attempt finished...")
                        SplashViewState(InitState.BACKUP_LOADED_SIGNED_IN)
                    }
                }.collect { _state.value = it }
            } else {
                _state.value = SplashViewState(InitState.NOT_SIGNED_IN)
            }
        }
    }

    private suspend fun loadingBackupFlow(): Flow<Boolean> = flow {
        backupManager.syncBackupToLocalForUserFlow().collect {
            when (it) {
                is Resource.Loading -> {
                    emit(true)
                    Timber.i("Loading backup")
                }

                is Resource.Success -> {
                    Timber.i("Remote backup request successful, replaced local db: ${it.data}")
                    emit(false)
                }

                is Resource.Failure -> {
                    Timber.e(it.exception)
                    emit(false)
                }
            }
        }
    }
}

data class SplashViewState(
    val initState: InitState = InitState.INIT
)

enum class InitState {
    INIT,
    NOT_SIGNED_IN,
    LOADING_BACKUP,
    BACKUP_LOADED_SIGNED_IN,
}
