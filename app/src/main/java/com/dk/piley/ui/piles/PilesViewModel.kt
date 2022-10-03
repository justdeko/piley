package com.dk.piley.ui.piles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.pile.PileWithTasks
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PilesViewModel @Inject constructor(
    private val repository: PileRepository
) : ViewModel() {
    private val _state = MutableStateFlow(PilesViewState())

    val state: StateFlow<PilesViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val pilesFlow = repository.getPilesWithTasks()
            combine(pilesFlow) { (piles) ->
                PilesViewState(piles)
            }.collect { _state.value = it }
        }
    }

    fun createPile(name: String) {
        viewModelScope.launch {
            repository.insertPile(Pile(name = name))
        }
    }
}

data class PilesViewState(
    val piles: List<PileWithTasks> = emptyList()
)