@file:Suppress("UNCHECKED_CAST")

package com.dk.piley.ui

import androidx.lifecycle.AbstractSavedStateViewModelFactory
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController

fun <VM : ViewModel> viewModelFactory(initializer: () -> VM): ViewModelProvider.Factory {
    return object : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return initializer() as T
        }
    }
}


inline fun <reified VM : ViewModel> savedStateViewModelFactory(
    navController: NavController,
    crossinline initializer: (SavedStateHandle) -> VM
): ViewModelProvider.Factory {
    return object : AbstractSavedStateViewModelFactory(
        navController.currentBackStackEntry!!,
        navController.currentBackStackEntry?.arguments
    ) {
        override fun <T : ViewModel> create(
            key: String,
            modelClass: Class<T>,
            handle: SavedStateHandle
        ): T {
            return initializer(handle) as T
        }
    }
}