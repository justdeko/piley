package com.dk.piley.model.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class NavigationEventRepository {
    private val _navigationEventFlow = MutableStateFlow<NavigationEvent?>(null)
    val navigationEventFlow: StateFlow<NavigationEvent?> get() = _navigationEventFlow

    fun addNavigationEvent(event: NavigationEvent) {
        _navigationEventFlow.value = event
    }

    fun clear() {
        _navigationEventFlow.value = null
    }
}