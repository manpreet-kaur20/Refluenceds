package com.example.refluenceds.data.local

import android.content.Context
import android.content.SharedPreferences
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
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

    private val _isLoggedInState = MutableStateFlow(isLoggedIn())
    val isLoggedInState: StateFlow<Boolean> = _isLoggedInState.asStateFlow()

    private val _sessionExpiredEvent = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
    val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    fun isLoggedIn(): Boolean {
        return getToken() != null && isOnboardingCompleted()
    }

    fun hasToken(): Boolean {
        return getToken() != null
    }

    fun getToken(): String? {
        return prefs.getString("user_token", null)
    }

    fun saveToken(token: String) {
        prefs.edit().putString("user_token", token).apply()
        _isLoggedInState.value = isLoggedIn()
    }

    fun saveUserId(userId: String) {
        prefs.edit().putString("user_id", userId).apply()
    }

    fun getUserId(): String? {
        return prefs.getString("user_id", null)
    }

    fun setOnboardingCompleted(completed: Boolean) {
        prefs.edit().putBoolean("onboarding_completed", completed).apply()
        _isLoggedInState.value = isLoggedIn()
    }

    fun isOnboardingCompleted(): Boolean {
        return prefs.getBoolean("onboarding_completed", false)
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

    @Volatile
    private var lastSessionExpiredTimestamp: Long = 0L

    fun clear() {
        prefs.edit()
            .remove("user_token")
            .remove("user_id")
            .remove("onboarding_completed")
            .apply()
        _isLoggedInState.value = false
    }

    fun onSessionExpired() {
        val currentTime = System.currentTimeMillis()
        synchronized(this) {
            val hadToken = hasToken()
            clear()
            if (hadToken && (currentTime - lastSessionExpiredTimestamp > 3000L)) {
                lastSessionExpiredTimestamp = currentTime
                _sessionExpiredEvent.tryEmit(Unit)
            }
        }
    }
}
