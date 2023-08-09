package com.dk.piley.ui.piles

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PilesViewModel @Inject constructor(
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
) : StatefulViewModel<PilesViewState>(PilesViewState()) {

    private val signedInUserFlow = userRepository.getSignedInUserNotNullFlow()

    init {
        viewModelScope.launch {
            collectState(
                signedInUserFlow.combine(pileRepository.getPilesWithTasks()) { user, piles ->
                    state.value.copy(
                        piles = piles,
                        selectedPileId = user.selectedPileId
                    )
                }
            )
        }
    }

    fun createPile(name: String) {
        viewModelScope.launch {
            signedInUserFlow.take(1).collect { user ->
                pileRepository.insertPile(
                    Pile(
                        name = name.trim(),
                        pileMode = user.pileMode
                    )
                )
            }
        }
    }

    fun deletePile(pile: Pile) {
        viewModelScope.launch {
            signedInUserFlow.take(1).collect {
                userRepository.insertUser(it.copy(selectedPileId = it.defaultPileId))
                pileRepository.deletePile(pile)
            }
        }
    }

    fun setSelectedPile(id: Long) {
        state.update {
            it.copy(selectedPileId = id)
        }
        viewModelScope.launch {
            signedInUserFlow.take(1).collect {
                userRepository.insertUser(it.copy(selectedPileId = id))
            }
        }
    }
}

data class PilesViewState(
    val piles: List<PileWithTasks> = emptyList(),
    val selectedPileId: Long = 1
)