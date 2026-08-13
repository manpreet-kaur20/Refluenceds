package com.example.refluenceds.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refluenceds.data.local.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoggedIn = MutableStateFlow(sessionManager.isLoggedIn())
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn

    // Signup form state
    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val referralCode = MutableStateFlow("")
    val gender = MutableStateFlow("")
    val country = MutableStateFlow("")
    val selectedInterests = MutableStateFlow<List<String>>(emptyList())
    val profilePhotos = MutableStateFlow<List<Any>>(emptyList())

    fun login() {
        sessionManager.saveToken("dummy_token")
        _isLoggedIn.value = true
    }

    fun signup() {
        sessionManager.saveToken("dummy_token")
        _isLoggedIn.value = true
    }

    fun logout() {
        sessionManager.clear()
        _isLoggedIn.value = false
    }
}
