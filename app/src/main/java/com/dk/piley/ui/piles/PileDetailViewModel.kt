package com.dk.piley.ui.piles

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.UserRepository
import com.dk.piley.ui.nav.pileScreen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PileDetailViewModel @Inject constructor(
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle,
    application: Application
) : AndroidViewModel(application) {
    private val _state = MutableStateFlow(PileDetailViewState())

    val state: StateFlow<PileDetailViewState>
        get() = _state

    private val signedInUserFlow = userRepository.getSignedInUserNotNull()

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>(pileScreen.identifier)
            id?.let { pileRepository.getPileById(it) }?.collect { pileWithTasks ->
                signedInUserFlow.take(1).collect {
                    _state.value = PileDetailViewState(pileWithTasks.pile, id != it.defaultPileId)
                }
            }
        }
    }

    fun deletePile() {
        viewModelScope.launch {
            signedInUserFlow.take(1).collect {
                userRepository.insertUser(it.copy(selectedPileId = it.defaultPileId))
                pileRepository.deletePile(state.value.pile)
            }
        }
    }

    fun editTitle(title: String) {
        viewModelScope.launch {
            pileRepository.insertPile(state.value.pile.copy(name = title))
        }
    }

    fun setPileMode(pileMode: PileMode) {
        viewModelScope.launch {
            pileRepository.insertPile(state.value.pile.copy(pileMode = pileMode))
        }
    }
}

data class PileDetailViewState(
    val pile: Pile = Pile(),
    val canDelete: Boolean = true
)
