package com.example.myapplication.data

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore(name = "settings")

class DataStoreManager(private val context:Context) {
    companion object {
        private val themeKey = stringPreferencesKey("app_theme")
        private val languageKey = stringPreferencesKey("app_language")
    }

    val getTheme: Flow<String> = context.dataStore.data.map { it[themeKey] ?: "system" }
    val getLanguage: Flow<String> = context.dataStore.data.map { it[languageKey] ?: "en" }

    suspend fun saveTheme(theme: String) {
        context.dataStore.edit { it[themeKey] = theme }
    }

    suspend fun saveLanguage(language: String) {
        context.dataStore.edit { it[languageKey] = language }

    }

}