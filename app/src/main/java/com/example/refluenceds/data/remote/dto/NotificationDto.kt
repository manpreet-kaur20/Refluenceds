package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.AppNotification
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class NotificationDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "title") val title: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "body") val body: String? = null,
    @Json(name = "type") val type: String? = null,
    @Json(name = "action_url") val actionUrl: String? = null,
    @Json(name = "is_read") val isRead: Boolean? = false,
    @Json(name = "read_at") val readAt: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "timestamp") val timestamp: String? = null
) {
    fun toDomain(): AppNotification {
        return AppNotification(
            id = id?.toString() ?: "",
            title = title ?: "",
            message = message ?: body ?: "",
            timestamp = timestamp ?: createdAt ?: ""
        )
    }
}

@JsonClass(generateAdapter = true)
data class NotificationListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: List<NotificationDto>? = null,
    @Json(name = "notifications") val notifications: List<NotificationDto>? = null,
    @Json(name = "unread_count") val unreadCount: Int? = 0,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
) {
    fun getEffectiveList(): List<NotificationDto> = data ?: notifications ?: emptyList()
}
