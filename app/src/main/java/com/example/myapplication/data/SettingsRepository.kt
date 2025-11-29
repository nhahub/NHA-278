package com.example.myapplication.data

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<Theme>
    val language: Flow<Language>

    suspend fun saveTheme(theme:Theme)
    suspend fun saveLanguage(language: Language)
}