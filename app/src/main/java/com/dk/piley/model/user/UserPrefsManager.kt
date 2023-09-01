package com.dk.piley.model.user

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
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
    private val signedInUser = stringPreferencesKey("signed_in_user_id")
    suspend fun setSignedInUser(email: String) = userPrefs.edit { prefs ->
        prefs[signedInUser] = email
    }

    suspend fun setBaseUrl(url: String) = userPrefs.edit { prefs ->
        prefs[urlKey] = url
    }

    fun getBaseUrl(): Flow<String> =
        userPrefs.data.map { prefs -> prefs[urlKey] ?: BuildConfig.LOCAL_API_BASE_URL }

    fun getUserPrefsEmail(): Flow<String> =
        userPrefs.data.map { prefs -> prefs[signedInUser] ?: "" }
}