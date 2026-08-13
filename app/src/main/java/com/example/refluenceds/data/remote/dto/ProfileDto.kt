package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.UserProfile
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileResponseDto(
    @Json(name = "id") val id: String,
    @Json(name = "name") val name: String,
    @Json(name = "email") val email: String,
    @Json(name = "avatarUrl") val avatarUrl: String?,
    @Json(name = "followersCount") val followersCount: Int? = 0
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            id = id,
            name = name,
            email = email,
            avatarUrl = avatarUrl ?: "",
            followersCount = followersCount ?: 0
        )
    }
}
