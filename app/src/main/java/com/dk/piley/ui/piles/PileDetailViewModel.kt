package com.dk.piley.ui.piles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.PileMode
import com.dk.piley.ui.nav.pileScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PileDetailViewModel @Inject constructor(
    private val repository: PileRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(PileDetailViewState())

    val state: StateFlow<PileDetailViewState>
        get() = _state

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>(pileScreen.identifier)
            id?.let { repository.getPileById(it) }?.collect { pileWithTasks ->
                _state.value = PileDetailViewState(pileWithTasks.pile)
            }
        }
    }

    fun deletePile() {
        viewModelScope.launch {
            repository.deletePile(state.value.pile)
        }
    }

    fun editTitle(title: String) {
        viewModelScope.launch {
            repository.insertPile(state.value.pile.copy(name = title))
        }
    }

    fun setPileMode(pileMode: PileMode) {
        viewModelScope.launch {
            repository.insertPile(state.value.pile.copy(pileMode = pileMode))
        }
    }
}

data class PileDetailViewState(
    val pile: Pile = Pile()
)
