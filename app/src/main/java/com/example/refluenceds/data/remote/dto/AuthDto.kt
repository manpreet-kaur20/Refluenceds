package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class SignupRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "password") val password: String
)

@JsonClass(generateAdapter = true)
data class AuthResponseDto(
    @Json(name = "token") val token: String,
    @Json(name = "userId") val userId: String,
    @Json(name = "message") val message: String? = null
)

@JsonClass(generateAdapter = true)
data class ForgotPasswordRequestDto(
    @Json(name = "email") val email: String
)

@JsonClass(generateAdapter = true)
data class VerifyEmailRequestDto(
    @Json(name = "email") val email: String,
    @Json(name = "code") val code: String
)

@JsonClass(generateAdapter = true)
data class SocialVerificationRequestDto(
    @Json(name = "platform") val platform: String,
    @Json(name = "username") val username: String
)

@JsonClass(generateAdapter = true)
data class OnboardingRequestDto(
    @Json(name = "firstName") val firstName: String,
    @Json(name = "lastName") val lastName: String,
    @Json(name = "gender") val gender: String,
    @Json(name = "country") val country: String,
    @Json(name = "interests") val interests: List<String>
)
