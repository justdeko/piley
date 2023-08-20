package com.dk.piley.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

/**
 * Stateful android view model which contains a mutable state flow of the ui
 *
 * @param T generic class representing the view state data class
 *
 * @param application application object used to initialize [AndroidViewModel]
 * @param initialViewState the initial view state when instantiating the viewModel
 */
abstract class StatefulAndroidViewModel<T>(
    application: Application,
    initialViewState: T,
) : AndroidViewModel(application) {
    var state = MutableStateFlow(initialViewState)
        private set

    suspend fun collectState(flow: Flow<T>) {
        flow.collect { state.value = it }
    }
}