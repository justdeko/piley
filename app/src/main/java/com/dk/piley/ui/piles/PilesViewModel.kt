package com.dk.piley.ui.piles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PilesViewModel @Inject constructor(
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
) : ViewModel() {
    private val _state = MutableStateFlow(PilesViewState())

    val state: StateFlow<PilesViewState>
        get() = _state

    private val signedInUserFlow = userRepository.getSignedInUserNotNullFlow()

    init {
        viewModelScope.launch {
            val pilesFlow = pileRepository.getPilesWithTasks()
            signedInUserFlow.combine(pilesFlow) { user, piles ->
                PilesViewState(
                    piles,
                    user.selectedPileId
                )
            }.collect { _state.value = it }
        }
    }

    fun createPile(name: String) {
        viewModelScope.launch {
            signedInUserFlow.take(1).collect { user ->
                pileRepository.insertPile(
                    Pile(
                        name = name,
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
        _state.update {
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