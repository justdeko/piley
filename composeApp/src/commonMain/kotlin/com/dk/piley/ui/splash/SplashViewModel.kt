package com.dk.piley.ui.splash

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.task.TaskRepository
import com.dk.piley.model.user.User
import com.dk.piley.model.user.UserRepository
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.getString
import piley.composeapp.generated.resources.Res
import piley.composeapp.generated.resources.daily_pile_name

/**
 * Splash view model
 *
 * @property userRepository user repository instance
 * @property pileRepository pile repository instance
 * @constructor Create empty Splash view model
 */
class SplashViewModel(
    private val userRepository: UserRepository,
    private val pileRepository: PileRepository,
    private val taskRepository: TaskRepository,
) : StatefulViewModel<SplashViewState>(SplashViewState()) {

    init {
        viewModelScope.launch {
            val userEmail = userRepository.getSignedInUserEmail()
            if (userEmail.isNotBlank()) {
                // if the tutorial has not been shown but the user is not new, set it to shown
                val tutorialShown = userRepository.getTutorialShown()
                if (!tutorialShown) {
                    userRepository.setTutorialShown(true)
                }
                // restart all alarms just in case
                taskRepository.restartAlarms()
                state.value = SplashViewState(InitState.BACKUP_LOADED_SIGNED_IN)
            } else {
                doFirstTimeRegister()
            }
        }
    }

    /**
     * Perform a first-time registration
     *
     */
    private fun doFirstTimeRegister() {
        val firstTimeUser = User(
            name = "John Doe",
            email = "john.doe@email2931238.com",
        )
        viewModelScope.launch {
            userRepository.insertUser(firstTimeUser)
            userRepository.setSignedInUser(firstTimeUser.email)
            createAndSetUserPile()
        }
    }

    /**
     * Create and set user pile for first-time user
     *
     */
    private suspend fun createAndSetUserPile() {
        // create default pile
        val pile = Pile(
            name = getString(Res.string.daily_pile_name),
        )
        val pileId = pileRepository.insertPile(pile)
        // update assigned pile as selected and set signed in state
        userRepository.getSignedInUserEntity()?.let { signedInUser ->
            userRepository.insertUser(
                signedInUser.copy(
                    selectedPileId = pileId,
                    defaultPileId = pileId
                )
            )
        }
        // signal loading process has finished to user and set state to first time
        // or navigate straight to main screen if tutorial already shown
        val tutorialShown = userRepository.getTutorialShown()
        val finalState =
            if (tutorialShown) InitState.BACKUP_LOADED_SIGNED_IN else InitState.FIRST_TIME
        state.update { SplashViewState(finalState) }
    }
}


data class SplashViewState(
    val initState: InitState = InitState.INIT
)

enum class InitState {
    INIT,
    BACKUP_LOADED_SIGNED_IN,
    FIRST_TIME
}
