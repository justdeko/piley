package com.dk.piley.ui.piles

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dk.piley.model.pile.Pile
import com.dk.piley.model.pile.PileRepository
import com.dk.piley.model.user.PileMode
import com.dk.piley.model.user.UserRepository
import com.dk.piley.ui.nav.pileScreen
import com.dk.piley.util.getCompletedTasksForWeekValues
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PileDetailViewModel @Inject constructor(
    private val pileRepository: PileRepository,
    private val userRepository: UserRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    private val _state = MutableStateFlow(PileDetailViewState())

    val state: StateFlow<PileDetailViewState>
        get() = _state

    private val signedInUserFlow = userRepository.getSignedInUserNotNull()

    init {
        viewModelScope.launch {
            val id = savedStateHandle.get<Long>(pileScreen.identifier)
            id?.let { pileRepository.getPileById(it) }?.collect { pileWithTasks ->
                signedInUserFlow.take(1).collect {
                    _state.value = PileDetailViewState(
                        pileWithTasks.pile,
                        getCompletedTasksForWeekValues(pileWithTasks),
                        pileWithTasks.pile.name,
                        pileWithTasks.pile.description,
                        id != it.defaultPileId
                    )
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
        if (title.length <= 20) {
            viewModelScope.launch {
                _state.update {
                    it.copy(titleTextValue = title)
                }
                pileRepository.insertPile(state.value.pile.copy(name = title))
            }
        }
    }

    fun setPileMode(pileMode: PileMode) {
        viewModelScope.launch {
            pileRepository.insertPile(state.value.pile.copy(pileMode = pileMode))
        }
    }

    fun editDescription(description: String) {
        _state.update {
            it.copy(descriptionTextValue = description)
        }
        viewModelScope.launch {
            pileRepository.insertPile(state.value.pile.copy(description = description))
        }
    }

    fun setPileLimit(limit: Int) {
        viewModelScope.launch {
            pileRepository.insertPile(state.value.pile.copy(pileLimit = limit))
        }
    }
}

data class PileDetailViewState(
    val pile: Pile = Pile(),
    val completedTaskCounts: List<Float> = emptyList(),
    val titleTextValue: String = "",
    val descriptionTextValue: String = "",
    val canDeleteOrEdit: Boolean = true
)
