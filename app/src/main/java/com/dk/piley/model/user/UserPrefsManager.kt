package com.dk.piley.model.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.dk.piley.BuildConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

/**
 * Manages user preferences file
 *
 * @property userPrefs instance of the user preferences data store
 */
@Singleton
class UserPrefsManager(
    private val userPrefs: DataStore<Preferences>
) {
    private val urlKey = stringPreferencesKey("base_url")
    private val signedInUserKey = stringPreferencesKey("signed_in_user_id")
    private val hasSeenTutorialKey = booleanPreferencesKey("tutorial_shown")
    private val signedOutKey = booleanPreferencesKey("signed_out")
    private val tasksDeletedKey = booleanPreferencesKey("tasksDeleted")

    suspend fun setSignedInUser(email: String) = userPrefs.edit { prefs ->
        prefs[signedInUserKey] = email
    }

    suspend fun setBaseUrl(url: String) = userPrefs.edit { prefs ->
        prefs[urlKey] = url
    }

    suspend fun setTutorialShown(shown: Boolean) = userPrefs.edit { prefs ->
        prefs[hasSeenTutorialKey] = shown
    }

    suspend fun setSignedOut(signedOut: Boolean) = userPrefs.edit { prefs ->
        prefs[signedOutKey] = signedOut
    }

    suspend fun setTasksDeleted(tasksDeleted: Boolean) = userPrefs.edit { prefs ->
        prefs[tasksDeletedKey] = tasksDeleted
    }

    fun getBaseUrl(): Flow<String> =
        userPrefs.data.map { prefs -> prefs[urlKey] ?: BuildConfig.LOCAL_API_BASE_URL }

    fun getUserPrefsEmail(): Flow<String> =
        userPrefs.data.map { prefs -> prefs[signedInUserKey] ?: "" }

    fun getTutorialShown(): Flow<Boolean> =
        userPrefs.data.map { prefs -> prefs[hasSeenTutorialKey] ?: false }

    fun getSignedOut(): Flow<Boolean> =
        userPrefs.data.map { prefs -> prefs[signedOutKey] ?: false }

    fun getTasksDeleted(): Flow<Boolean> =
        userPrefs.data.map { prefs -> prefs[tasksDeletedKey] ?: false }
}