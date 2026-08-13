package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.AppNotification
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationListResponseDto(
    @Json(name = "notifications") val notifications: List<NotificationDto>
)

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "id") val id: String,
    @Json(name = "title") val title: String,
    @Json(name = "message") val message: String,
    @Json(name = "timestamp") val timestamp: String
) {
    fun toDomain(): AppNotification {
        return AppNotification(
            id = id,
            title = title,
            message = message,
            timestamp = timestamp
        )
    }
}
