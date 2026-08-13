package com.example.refluenceds.domain.model

data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String,
    val followersCount: Int
)
