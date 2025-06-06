package com.dk.piley.ui.piles

import androidx.lifecycle.viewModelScope
import com.dk.piley.common.StatefulViewModel
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.user.UserRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Piles view model
 *
 * @property pileRepository pile repository instance
 * @property userRepository user repository instance
 */
class PilesViewModel(
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
                        selectedPileId = user.selectedPileId,
                        pileOrder = userRepository.getPileOrder()
                    )
                }
            )
        }
    }

    /**
     * Create pile
     *
     * @param name pile name
     */
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

    /**
     * Set default selected pile
     *
     * @param id pile id
     */
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

    /**
     * Reorder piles
     *
     * @param pileOrder list of pile ids in new order
     */
    fun reorderPiles(pileOrder: List<Long>) {
        state.update {
            it.copy(pileOrder = pileOrder)
        }
        viewModelScope.launch {
            userRepository.setPileOrder(pileOrder)
        }
    }
}

data class PilesViewState(
    val piles: List<PileWithTasks> = emptyList(),
    val pileOrder: List<Long> = emptyList(),
    val selectedPileId: Long = 1
)