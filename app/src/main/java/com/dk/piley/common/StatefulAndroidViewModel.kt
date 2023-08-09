package com.dk.piley.common

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

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