package com.altankoc.beuverse.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.searchDataStore by preferencesDataStore(name = "search_history")

data class SearchHistoryItem(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val username: String,
    val bio: String?
)

@Singleton
class SearchHistoryManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val gson = Gson()
    private val MAX_HISTORY = 7

    private fun historyKey(userId: Long) = stringPreferencesKey("search_history_$userId")

    fun getHistory(userId: Long): Flow<List<SearchHistoryItem>> =
        context.searchDataStore.data.map { prefs ->
            val json = prefs[historyKey(userId)] ?: return@map emptyList()
            try {
                val type = object : TypeToken<List<SearchHistoryItem>>() {}.type
                gson.fromJson(json, type)
            } catch (e: Exception) {
                emptyList()
            }
        }

    suspend fun addToHistory(userId: Long, item: SearchHistoryItem) {
        context.searchDataStore.edit { prefs ->
            val key = historyKey(userId)
            val json = prefs[key] ?: "[]"
            val type = object : TypeToken<List<SearchHistoryItem>>() {}.type
            val current = try {
                gson.fromJson<List<SearchHistoryItem>>(json, type).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
            current.removeAll { it.id == item.id }
            current.add(0, item)
            val trimmed = current.take(MAX_HISTORY)
            prefs[key] = gson.toJson(trimmed)
        }
    }

    suspend fun removeFromHistory(userId: Long, id: Long) {
        context.searchDataStore.edit { prefs ->
            val key = historyKey(userId)
            val json = prefs[key] ?: return@edit
            val type = object : TypeToken<List<SearchHistoryItem>>() {}.type
            val current = try {
                gson.fromJson<List<SearchHistoryItem>>(json, type).toMutableList()
            } catch (e: Exception) {
                mutableListOf()
            }
            current.removeAll { it.id == id }
            prefs[key] = gson.toJson(current)
        }
    }

    suspend fun clearHistory(userId: Long) {
        context.searchDataStore.edit { prefs ->
            prefs[historyKey(userId)] = "[]"
        }
    }
}