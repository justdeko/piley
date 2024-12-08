package com.dk.piley.model.navigation

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class ShortcutEventRepository {
    private val mutableKeyEventFlow = MutableSharedFlow<Shortcut>(replay = 1)
    val keyEventFlow: Flow<Shortcut> = mutableKeyEventFlow.asSharedFlow()

    fun emitShortcutEvent(event: Shortcut) {
        mutableKeyEventFlow.tryEmit(event)
    }
}