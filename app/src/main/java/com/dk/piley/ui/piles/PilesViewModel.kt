package com.dk.piley.ui.piles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.pile.PileWithTasks
import com.dk.piley.model.user.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
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

    init {
        viewModelScope.launch {
            val pilesFlow = pileRepository.getPilesWithTasks()
            // TODO remove hardcoded
            val userFlow = userRepository.getUserById(1)
            userFlow.combine(pilesFlow) { user, piles ->
                PilesViewState(piles, user.selectedPileId)
            }.collect { _state.value = it }
        }
    }

    fun createPile(name: String) {
        viewModelScope.launch {
            pileRepository.insertPile(Pile(name = name))
        }
    }

    fun deletePile(pile: Pile) {
        viewModelScope.launch {
            userRepository.getUserById(1).take(1).collect {
                userRepository.insertUser(it.copy(selectedPileId = 1))
                pileRepository.deletePile(pile)
            }
        }
    }

    fun setSelectedPile(id: Long) {
        _state.update {
            it.copy(selectedPileId = id)
        }
        viewModelScope.launch {
            // TODO: remove hardcoded
            userRepository.getUserById(1).take(1).collect {
                userRepository.insertUser(it.copy(selectedPileId = id))
            }
        }
    }
}

data class PilesViewState(
    val piles: List<PileWithTasks> = emptyList(),
    val selectedPileId: Long = 1
)