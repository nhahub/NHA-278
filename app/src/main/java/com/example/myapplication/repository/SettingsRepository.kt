package com.example.myapplication.repository

import com.example.myapplication.data.Language
import com.example.myapplication.data.Theme
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    val theme: Flow<Theme>
    val language: Flow<Language>

    suspend fun saveTheme(theme: Theme)
    suspend fun saveLanguage(language: Language)
}