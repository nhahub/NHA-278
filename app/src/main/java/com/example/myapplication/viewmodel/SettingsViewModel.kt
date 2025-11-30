package com.example.myapplication.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapplication.data.Language
import com.example.myapplication.repository.SettingsRepository
import com.example.myapplication.data.Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow

import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


class SettingsViewModel(private val settingsRepository: SettingsRepository): ViewModel() {

    val themeState: StateFlow<Theme> = settingsRepository.theme
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Theme.SYSTEM
        )

    val languageState: StateFlow<Language> = settingsRepository.language
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Lazily,
            initialValue = Language.ENGLISH
        )


    fun changeTheme(newTheme: Theme) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveTheme(newTheme)
        }
    }

    fun changeLanguage(newLanguage: Language) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsRepository.saveLanguage(newLanguage)
        }
    }
}
