package com.dk.piley.model.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Manages user preferences file
 *
 * @property userPrefs instance of the user preferences data store
 */
class UserPrefsManager(
    private val userPrefs: DataStore<Preferences>
) {
    private val signedInUserKey = stringPreferencesKey("signed_in_user_id")
    private val hasSeenTutorialKey = booleanPreferencesKey("tutorial_shown")
    private val tasksDeletedKey = booleanPreferencesKey("tasksDeleted")
    private val skipSplashScreenKey = booleanPreferencesKey("skip_splash_screen")
    private val pileOrderKey = stringPreferencesKey("pile_order")

    suspend fun setSignedInUser(email: String) = userPrefs.edit { prefs ->
        prefs[signedInUserKey] = email
    }

    suspend fun setTutorialShown(shown: Boolean) = userPrefs.edit { prefs ->
        prefs[hasSeenTutorialKey] = shown
    }

    suspend fun setTasksDeleted(tasksDeleted: Boolean) = userPrefs.edit { prefs ->
        prefs[tasksDeletedKey] = tasksDeleted
    }

    suspend fun setSkipSplashScreen(skip: Boolean) = userPrefs.edit { prefs ->
        prefs[skipSplashScreenKey] = skip
    }

    suspend fun setPileOrder(order: List<Long>) = userPrefs.edit { prefs ->
        prefs[pileOrderKey] = order.joinToString(",")
    }

    fun getUserPrefsEmail(): Flow<String> =
        userPrefs.data.map { prefs -> prefs[signedInUserKey] ?: "" }

    fun getTutorialShown(): Flow<Boolean> =
        userPrefs.data.map { prefs -> prefs[hasSeenTutorialKey] ?: false }

    fun getTasksDeleted(): Flow<Boolean> =
        userPrefs.data.map { prefs -> prefs[tasksDeletedKey] ?: false }

    fun getSkipSplashScreen(): Flow<Boolean> =
        userPrefs.data.map { prefs -> prefs[skipSplashScreenKey] ?: false }

    fun getPileOrder(): Flow<List<Long>> =
        userPrefs.data.map { prefs ->
            prefs[pileOrderKey]?.split(",")?.mapNotNull { it.toLongOrNull() } ?: emptyList()
        }
}