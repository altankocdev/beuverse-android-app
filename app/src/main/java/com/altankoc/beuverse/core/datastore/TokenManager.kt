package com.altankoc.beuverse.core.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "beuverse_prefs")

@Singleton
class TokenManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        private val ACCESS_TOKEN = stringPreferencesKey("access_token")
        private val USER_ID = longPreferencesKey("user_id")
        private val USERNAME = stringPreferencesKey("username")
        private val EMAIL = stringPreferencesKey("email")
        private val ROLE = stringPreferencesKey("role")
    }

    val accessToken: Flow<String?> = context.dataStore.data.map { it[ACCESS_TOKEN] }
    val userId: Flow<Long?> = context.dataStore.data.map { it[USER_ID] }
    val username: Flow<String?> = context.dataStore.data.map { it[USERNAME] }
    val email: Flow<String?> = context.dataStore.data.map { it[EMAIL] }
    val role: Flow<String?> = context.dataStore.data.map { it[ROLE] }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { it[ACCESS_TOKEN] = token }
    }

    suspend fun saveUserInfo(id: Long, username: String, email: String, role: String) {
        context.dataStore.edit {
            it[USER_ID] = id
            it[USERNAME] = username
            it[EMAIL] = email
            it[ROLE] = role
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }
}