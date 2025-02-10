package com.example.thuctapcosochuyennganh.ViewModel

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val RATE_PREFERENCES_NAME = "rate_preferences"

// Extension để tạo DataStore
val Context.dataStore by preferencesDataStore(name = RATE_PREFERENCES_NAME)

object RatePreferences {
    // Tạo key động cho mỗi movieId
    private val RATE_KEY = intPreferencesKey("rate_movie_")

    fun getRateKey(movieId: String): Preferences.Key<Int> {
        return intPreferencesKey("${RATE_KEY.name}$movieId")
    }

    // Lưu đánh giá vào DataStore
    suspend fun saveRating(context: Context, movieId: String, score: Int) {
        context.dataStore.edit { preferences ->
            preferences[getRateKey(movieId)] = score
        }
    }

    // Lấy đánh giá từ DataStore
    fun getRating(context: Context, movieId: String): Flow<Int?> {
        return context.dataStore.data.map { preferences ->
            preferences[getRateKey(movieId)]
        }
    }
}