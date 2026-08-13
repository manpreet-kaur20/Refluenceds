package com.example.refluenceds.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SessionManager @Inject constructor(@ApplicationContext context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    private val _themeState = MutableStateFlow(getTheme())
    val themeState: StateFlow<String> = _themeState.asStateFlow()

    private val _languageState = MutableStateFlow(getLanguage())
    val languageState: StateFlow<String> = _languageState.asStateFlow()

    fun isLoggedIn(): Boolean {
        return prefs.getString("user_token", null) != null
    }

    fun saveToken(token: String) {
        prefs.edit().putString("user_token", token).apply()
    }

    fun getTheme(): String {
        return prefs.getString("app_theme", "System") ?: "System"
    }

    fun setTheme(theme: String) {
        prefs.edit().putString("app_theme", theme).apply()
        _themeState.value = theme
    }

    fun getLanguage(): String {
        return prefs.getString("app_language", "en") ?: "en"
    }

    fun setLanguage(langCode: String) {
        prefs.edit().putString("app_language", langCode).apply()
        _languageState.value = langCode
    }

    fun clear() {
        prefs.edit().remove("user_token").apply()
    }
}
