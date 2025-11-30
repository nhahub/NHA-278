package com.example.myapplication.repository

import com.example.myapplication.data.DataStoreManager
import com.example.myapplication.data.Language
import com.example.myapplication.data.Theme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SettingsRepositoryImp(private val dataStoreManager: DataStoreManager) : SettingsRepository {

    override val theme: Flow<Theme> = dataStoreManager.getTheme.map {
        runCatching {  Theme.valueOf(it.uppercase()) }.getOrElse{ Theme.SYSTEM}}

    override val language: Flow<Language> = dataStoreManager.getLanguage.map {
        runCatching {  Language.valueOf(it.uppercase()) }.getOrElse { Language.ENGLISH }}


    override suspend fun saveTheme(theme: Theme) =
        dataStoreManager.saveTheme(theme.name.lowercase())

    override suspend fun saveLanguage(language: Language) =
        dataStoreManager.saveLanguage(language.name.lowercase())


}