package com.example.refluenceds.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refluenceds.data.local.SessionManager
import com.example.refluenceds.data.remote.dto.*
import com.example.refluenceds.domain.repository.AppRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AppRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val isLoggedIn: StateFlow<Boolean> = sessionManager.isLoggedInState

    val isOnboardingCompleted: Boolean
        get() = sessionManager.isOnboardingCompleted()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage.asStateFlow()

    // ── Form State ────────────────────────────────────────────────────────────
    val firstName = MutableStateFlow("")
    val lastName = MutableStateFlow("")
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val referralCode = MutableStateFlow("")
    val gender = MutableStateFlow("")
    val country = MutableStateFlow("")
    val selectedCountryId = MutableStateFlow<Int?>(null)
    val selectedInterests = MutableStateFlow<List<String>>(emptyList())
    val selectedIndustryIds = MutableStateFlow<List<Int>>(emptyList())
    val profilePhotos = MutableStateFlow<List<Any>>(emptyList())

    // ── Dynamic Server Data ──────────────────────────────────────────────────
    private val _countriesList = MutableStateFlow<List<CountryDto>>(emptyList())
    val countriesList: StateFlow<List<CountryDto>> = _countriesList.asStateFlow()

    private val _industriesList = MutableStateFlow<List<IndustryDto>>(emptyList())
    val industriesList: StateFlow<List<IndustryDto>> = _industriesList.asStateFlow()

    private val _termsData = MutableStateFlow<TermsResponseDto?>(null)
    val termsData: StateFlow<TermsResponseDto?> = _termsData.asStateFlow()

    private val _onboardingStatus = MutableStateFlow<OnboardingStatusResponseDto?>(null)
    val onboardingStatus: StateFlow<OnboardingStatusResponseDto?> = _onboardingStatus.asStateFlow()

    private val _referralStats = MutableStateFlow<ReferralStatsDto?>(null)
    val referralStats: StateFlow<ReferralStatsDto?> = _referralStats.asStateFlow()

    private val _userProfile = MutableStateFlow<UserDto?>(null)
    val userProfile: StateFlow<UserDto?> = _userProfile.asStateFlow()

    private val _pushPreferences = MutableStateFlow<PushPreferencesDto?>(null)
    val pushPreferences: StateFlow<PushPreferencesDto?> = _pushPreferences.asStateFlow()

    private val _emailPreferences = MutableStateFlow<EmailPreferencesDto?>(null)
    val emailPreferences: StateFlow<EmailPreferencesDto?> = _emailPreferences.asStateFlow()

    init {
        viewModelScope.launch {
            sessionManager.sessionExpiredEvent.collect {
                clearOnboardingFields()
                _userProfile.value = null
                _referralStats.value = null
                _pushPreferences.value = null
                _emailPreferences.value = null
                _errorMessage.value = "Session expired. Please log in again."
            }
        }
        fetchCountries()
        fetchIndustries()
        fetchTerms()
        if (sessionManager.hasToken()) {
            fetchUserProfile()
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }

    fun clearSuccess() {
        _successMessage.value = null
    }

    // ── Auth Methods ─────────────────────────────────────────────────────────

    fun checkAndResumeOnboarding(onResume: (Int) -> Unit) {
        if (!sessionManager.hasToken() || sessionManager.isOnboardingCompleted()) return
        viewModelScope.launch {
            repository.getCurrentUser().onSuccess { user ->
                val isCompleted = user.isOnboardingCompleted == true
                if (isCompleted) {
                    sessionManager.setOnboardingCompleted(true)
                } else {
                    val step = user.currentStep ?: user.onboarding?.currentStep ?: 1
                    user.firstName?.let { firstName.value = it }
                    user.lastName?.let { lastName.value = it }
                    user.gender?.let { gender.value = it }
                    user.country?.name?.let { country.value = it }
                    user.countryId?.let { selectedCountryId.value = (it as? Number)?.toInt() ?: it.toString().toIntOrNull() }
                    onResume(step)
                }
            }
        }
    }

    fun login(
        emailInput: String = email.value,
        passwordInput: String = password.value,
        onSuccess: (isCompleted: Boolean, currentStep: Int) -> Unit = { _, _ -> },
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.login(emailInput, passwordInput)
                .onSuccess { res ->
                    _isLoading.value = false
                    val token = res.getEffectiveToken()
                    if (!token.isNullOrEmpty()) {
                        sessionManager.saveToken(token)
                    }
                    val user = res.getEffectiveUser()
                    user?.id?.let {
                        sessionManager.saveUserId(it.toString())
                    }
                    val isCompleted = user?.isOnboardingCompleted == true
                    sessionManager.setOnboardingCompleted(isCompleted)

                    val step = user?.currentStep ?: user?.onboarding?.currentStep ?: 1
                    user?.firstName?.let { firstName.value = it }
                    user?.lastName?.let { lastName.value = it }
                    user?.gender?.let { gender.value = it }
                    user?.country?.name?.let { country.value = it }
                    user?.countryId?.let { selectedCountryId.value = (it as? Number)?.toInt() ?: it.toString().toIntOrNull() }

                    onSuccess(isCompleted, step)
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Login failed"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun register(
        emailInput: String = email.value,
        passwordInput: String = password.value,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.register(emailInput, passwordInput)
                .onSuccess { res ->
                    _isLoading.value = false
                    val token = res.getEffectiveToken()
                    if (!token.isNullOrEmpty()) {
                        sessionManager.saveToken(token)
                    }
                    res.getEffectiveUser()?.id?.let {
                        sessionManager.saveUserId(it.toString())
                    }
                    sessionManager.setOnboardingCompleted(false)
                    onSuccess()
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Signup failed"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun signup(
        emailInput: String = email.value,
        passwordInput: String = password.value,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        register(emailInput, passwordInput, onSuccess, onError)
    }

    fun clearOnboardingFields() {
        firstName.value = ""
        lastName.value = ""
        email.value = ""
        password.value = ""
        referralCode.value = ""
        gender.value = ""
        country.value = ""
        selectedCountryId.value = null
        selectedInterests.value = emptyList()
        selectedIndustryIds.value = emptyList()
        profilePhotos.value = emptyList()
    }

    fun signup() {
        completeOnboarding()
    }

    fun completeOnboarding() {
        sessionManager.setOnboardingCompleted(true)
        clearOnboardingFields()
        fetchUserProfile()
    }

    fun socialLogin(
        provider: String,
        providerId: String,
        socialEmail: String? = null,
        fName: String? = null,
        lName: String? = null,
        deviceToken: String? = null,
        deviceType: String? = "android",
        lat: String? = null,
        lng: String? = null,
        onSuccess: (isCompleted: Boolean, currentStep: Int) -> Unit = { _, _ -> },
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.socialLogin(
                provider, providerId, socialEmail, fName, lName, deviceToken, deviceType, lat, lng
            ).onSuccess { res ->
                _isLoading.value = false
                val token = res.getEffectiveToken()
                if (!token.isNullOrEmpty()) {
                    sessionManager.saveToken(token)
                }
                val user = res.getEffectiveUser()
                user?.id?.let {
                    sessionManager.saveUserId(it.toString())
                }
                val isCompleted = user?.isOnboardingCompleted == true
                sessionManager.setOnboardingCompleted(isCompleted)

                val step = user?.currentStep ?: user?.onboarding?.currentStep ?: 1
                user?.firstName?.let { firstName.value = it }
                user?.lastName?.let { lastName.value = it }
                user?.gender?.let { gender.value = it }
                user?.country?.name?.let { country.value = it }
                user?.countryId?.let { selectedCountryId.value = (it as? Number)?.toInt() ?: it.toString().toIntOrNull() }

                onSuccess(isCompleted, step)
            }.onFailure { err ->
                _isLoading.value = false
                val msg = err.localizedMessage ?: "Social login failed"
                _errorMessage.value = msg
                onError(msg)
            }
        }
    }

    fun forgotPassword(
        emailInput: String,
        onSuccess: (ForgotPasswordResponseDto) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.forgotPassword(emailInput)
                .onSuccess { res ->
                    _isLoading.value = false
                    _successMessage.value = res.message ?: "Reset link sent"
                    onSuccess(res)
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Failed to send reset link"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun resetPassword(
        token: String,
        emailInput: String,
        pass: String,
        passConfirmation: String,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.resetPassword(token, emailInput, pass, passConfirmation)
                .onSuccess { msg ->
                    _isLoading.value = false
                    _successMessage.value = msg
                    onSuccess(msg)
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Password reset failed"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun changePassword(
        currentPass: String,
        newPass: String,
        confirmPass: String,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.changePassword(currentPass, newPass, confirmPass)
                .onSuccess { msg ->
                    _isLoading.value = false
                    _successMessage.value = msg
                    onSuccess(msg)
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Failed to change password"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun logout(onComplete: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                repository.logout()
            } catch (_: Exception) {}
            sessionManager.clear()
            clearOnboardingFields()
            _userProfile.value = null
            _referralStats.value = null
            onComplete()
        }
    }

    // ── Referral Methods ─────────────────────────────────────────────────────

    fun confirmReferral(
        code: String = referralCode.value,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.confirmReferral(code)
                .onSuccess {
                    _isLoading.value = false
                    onSuccess()
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Invalid referral code"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun skipReferral(onSuccess: () -> Unit = {}) {
        viewModelScope.launch {
            repository.skipReferral()
            onSuccess()
        }
    }

    fun fetchMyReferralCodeAndStats(
        onSuccess: (ReferralStatsDto) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.getMyReferralCode()
                .onSuccess { stats ->
                    _referralStats.value = stats
                    onSuccess(stats)
                }
                .onFailure { err ->
                    val msg = err.localizedMessage ?: "Failed to load referral stats"
                    onError(msg)
                }
        }
    }

    // ── Countries & Industries & Terms Methods ────────────────────────────────

    fun fetchCountries(search: String? = null) {
        viewModelScope.launch {
            repository.getCountries(search)
                .onSuccess { list ->
                    if (list.isNotEmpty()) {
                        _countriesList.value = list
                    }
                }
                .onFailure {
                    // Fallback to defaults if network fails
                    if (_countriesList.value.isEmpty()) {
                        _countriesList.value = listOf(
                            CountryDto(82, "Germany", "DE", "DE", "DEU", "49", "Berlin", "EUR", "€", "🇩🇪"),
                            CountryDto(205, "Switzerland", "CH", "CH", "CHE", "41", "Bern", "CHF", "CHF", "🇨🇭"),
                            CountryDto(75, "France", "FR", "FR", "FRA", "33", "Paris", "EUR", "€", "🇫🇷"),
                            CountryDto(107, "Italy", "IT", "IT", "ITA", "39", "Rome", "EUR", "€", "🇮🇹"),
                            CountryDto(14, "Austria", "AT", "AT", "AUT", "43", "Vienna", "EUR", "€", "🇦🇹"),
                            CountryDto(124, "Liechtenstein", "LI", "LI", "LIE", "423", "Vaduz", "CHF", "CHF", "🇱🇮"),
                            CountryDto(226, "United States", "US", "US", "USA", "1", "Washington", "USD", "$", "🇺🇸"),
                            CountryDto(228, "United Kingdom", "GB", "GB", "GBR", "44", "London", "GBP", "£", "🇬🇧")
                        )
                    }
                }
        }
    }

    fun fetchCountryById(
        countryId: Int,
        onSuccess: (CountryDto) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            repository.getCountryById(countryId)
                .onSuccess(onSuccess)
                .onFailure { onError(it.localizedMessage ?: "Failed to get country") }
        }
    }

    fun fetchIndustries() {
        viewModelScope.launch {
            repository.getIndustries()
                .onSuccess { list ->
                    if (list.isNotEmpty()) {
                        _industriesList.value = list
                    }
                }
                .onFailure {
                    if (_industriesList.value.isEmpty()) {
                        _industriesList.value = listOf(
                            IndustryDto(1, "Beauty", "beauty.png", null),
                            IndustryDto(2, "Fashion", "fashion.png", null),
                            IndustryDto(3, "Gastronomy", "gastronomy.png", null),
                            IndustryDto(4, "Food & Drink", "food.png", null),
                            IndustryDto(5, "Travel", "travel.png", null),
                            IndustryDto(6, "Sports", "sports.png", null)
                        )
                    }
                }
        }
    }

    fun fetchTerms() {
        viewModelScope.launch {
            repository.getTerms()
                .onSuccess { terms ->
                    _termsData.value = terms
                }
        }
    }

    fun fetchOnboardingStatus() {
        viewModelScope.launch {
            repository.getOnboardingStatus()
                .onSuccess { status ->
                    _onboardingStatus.value = status
                }
        }
    }

    // ── Onboarding Step Submissions ──────────────────────────────────────────

    fun submitStep1FirstName(
        fName: String = firstName.value,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.submitOnboardingStep1(fName)
                .onSuccess {
                    _isLoading.value = false
                    firstName.value = fName
                    onSuccess()
                }
                .onFailure { err ->
                    _isLoading.value = false
                    // Fallback to local progress if offline/network error
                    firstName.value = fName
                    onSuccess()
                }
        }
    }

    fun submitStep2LastName(
        lName: String = lastName.value,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.submitOnboardingStep2(lName)
                .onSuccess {
                    _isLoading.value = false
                    lastName.value = lName
                    onSuccess()
                }
                .onFailure { err ->
                    _isLoading.value = false
                    lastName.value = lName
                    onSuccess()
                }
        }
    }

    fun submitStep3Gender(
        selectedGender: String = gender.value,
        skip: Boolean = false,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.submitOnboardingStep3(if (skip) null else selectedGender, skip)
                .onSuccess {
                    _isLoading.value = false
                    if (!skip) gender.value = selectedGender
                    onSuccess()
                }
                .onFailure {
                    _isLoading.value = false
                    if (!skip) gender.value = selectedGender
                    onSuccess()
                }
        }
    }

    fun submitStep4Country(
        countryId: Int,
        countryName: String,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            selectedCountryId.value = countryId
            country.value = countryName
            repository.submitOnboardingStep4(countryId)
                .onSuccess {
                    _isLoading.value = false
                    onSuccess()
                }
                .onFailure {
                    _isLoading.value = false
                    onSuccess()
                }
        }
    }

    fun submitStep5Industries(
        industryIds: List<Int>,
        industryNames: List<String>,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            selectedIndustryIds.value = industryIds
            selectedInterests.value = industryNames
            repository.submitOnboardingStep5(industryIds)
                .onSuccess {
                    _isLoading.value = false
                    onSuccess()
                }
                .onFailure {
                    _isLoading.value = false
                    onSuccess()
                }
        }
    }

    fun submitStep6Photos(
        photoFiles: List<File>,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            val parts = photoFiles.mapIndexed { index, file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photos[]", file.name, requestFile)
            }
            repository.submitOnboardingStep6(parts)
                .onSuccess {
                    _isLoading.value = false
                    onSuccess()
                }
                .onFailure {
                    _isLoading.value = false
                    onSuccess()
                }
        }
    }

    fun submitStep7Terms(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.submitOnboardingStep7(true)
                .onSuccess {
                    _isLoading.value = false
                    sessionManager.setOnboardingCompleted(true)
                    clearOnboardingFields()
                    fetchUserProfile()
                    onSuccess()
                }
                .onFailure {
                    _isLoading.value = false
                    sessionManager.setOnboardingCompleted(true)
                    clearOnboardingFields()
                    fetchUserProfile()
                    onSuccess()
                }
        }
    }

    // ── Profile Methods ───────────────────────────────────────────────────────

    fun fetchUserProfile(
        onSuccess: (UserDto) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getProfile()
                .onSuccess { res ->
                    _isLoading.value = false
                    val user = res.getEffectiveUser()
                    if (user != null) {
                        _userProfile.value = user
                        user.theme?.let { th ->
                            sessionManager.setTheme(th.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() })
                        }
                        _pushPreferences.value = user.toPushPreferences()
                        _emailPreferences.value = user.toEmailPreferences()
                        user.firstName?.let { firstName.value = it }
                        user.lastName?.let { lastName.value = it }
                        user.email?.let { email.value = it }
                        user.gender?.let { gender.value = it }
                        user.country?.name?.let { country.value = it }
                        user.countryId?.let { selectedCountryId.value = (it as? Number)?.toInt() ?: it.toString().toIntOrNull() }
                        user.referral?.let { ref ->
                            val existingStats = _referralStats.value
                            _referralStats.value = ReferralStatsDto(
                                myReferralCode = ref.getEffectiveCode(),
                                code = ref.getEffectiveCode(),
                                myCode = ref.getEffectiveCode(),
                                referralCode = ref.getEffectiveCode(),
                                shareUrl = ref.shareUrl,
                                shareText = ref.shareText,
                                hasApplied = ref.hasApplied,
                                hasSkipped = ref.hasSkipped,
                                appliedType = ref.appliedType,
                                appliedCode = ref.appliedCode,
                                status = ref.status,
                                invitationLink = ref.shareUrl,
                                inviteLink = ref.shareUrl,
                                stats = ref.stats ?: existingStats?.stats,
                                totalInvited = ref.stats?.totalInvited ?: existingStats?.getEffectiveTotalInvited() ?: 0,
                                pendingFirstApplication = ref.stats?.pendingFirstApplication ?: existingStats?.getEffectivePending() ?: 0,
                                rewardsEarned = ref.stats?.totalEarned ?: existingStats?.getEffectiveRewards() ?: "0.00 CHF"
                            )
                        }
                        onSuccess(user)
                    }
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Failed to load profile"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun updateProfileDetails(
        fields: Map<String, String>,
        profilePictureFile: File? = null,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val part = profilePictureFile?.let { file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("profile_picture", file.name, requestFile)
            }
            val res = if (part != null) {
                repository.updateProfileMultipart(fields, part)
            } else {
                repository.updateProfile(fields)
            }
            res.onSuccess { profileRes ->
                _isLoading.value = false
                profileRes.getEffectiveUser()?.let { updated ->
                    _userProfile.value = updated
                }
                _successMessage.value = "Profile updated successfully"
                onSuccess()
            }.onFailure { err ->
                _isLoading.value = false
                val msg = err.localizedMessage ?: "Failed to update profile"
                _errorMessage.value = msg
                onError(msg)
            }
        }
    }

    fun updateProfileIndustries(
        industryIds: List<Int>,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.updateProfileIndustries(industryIds)
                .onSuccess { res ->
                    _isLoading.value = false
                    res.getEffectiveUser()?.let { updated ->
                        _userProfile.value = updated
                    }
                    _successMessage.value = "Interests updated successfully"
                    onSuccess()
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Failed to update interests"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun updateProfilePhotos(
        photoFiles: List<File>,
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val parts = photoFiles.mapIndexed { index, file ->
                val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("photos[]", file.name, requestFile)
            }
            repository.updateProfilePhotos(parts)
                .onSuccess { msg ->
                    _isLoading.value = false
                    _successMessage.value = msg
                    fetchUserProfile()
                    onSuccess()
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Failed to upload photos"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun deleteAccount(
        onSuccess: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.deleteAccount()
                .onSuccess {
                    _isLoading.value = false
                    sessionManager.clear()
                    onSuccess()
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Failed to delete account"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }

    fun fetchPushPreferences() {
        if (_userProfile.value == null) {
            fetchUserProfile()
        } else {
            _pushPreferences.value = _userProfile.value?.toPushPreferences()
        }
    }

    fun updatePushPreferences(prefs: PushPreferencesDto) {
        viewModelScope.launch {
            _pushPreferences.value = prefs
            repository.updateProfile(prefs.toMap())
        }
    }

    fun fetchEmailPreferences() {
        if (_userProfile.value == null) {
            fetchUserProfile()
        } else {
            _emailPreferences.value = _userProfile.value?.toEmailPreferences()
        }
    }

    fun updateEmailPreferences(prefs: EmailPreferencesDto) {
        viewModelScope.launch {
            _emailPreferences.value = prefs
            repository.updateProfile(prefs.toMap())
        }
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch {
            sessionManager.setTheme(theme)
            repository.updateProfile(mapOf("theme" to theme.lowercase()))
        }
    }

    fun submitFeedback(
        feedback: String,
        onSuccess: (String) -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            repository.submitFeedback(feedback)
                .onSuccess { msg ->
                    _isLoading.value = false
                    _successMessage.value = msg
                    onSuccess(msg)
                }
                .onFailure { err ->
                    _isLoading.value = false
                    val msg = err.localizedMessage ?: "Failed to submit feedback"
                    _errorMessage.value = msg
                    onError(msg)
                }
        }
    }
}
