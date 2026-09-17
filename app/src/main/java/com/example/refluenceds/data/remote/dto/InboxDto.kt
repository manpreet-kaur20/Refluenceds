package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class InboxTabDto(
    @Json(name = "key") val key: String? = null,
    @Json(name = "label") val label: String? = null,
    @Json(name = "unread_count") val unreadCount: Int? = 0,
    @Json(name = "is_active") val isActive: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class InboxUnreadCountsDto(
    @Json(name = "chat") val chat: Int? = 0,
    @Json(name = "notifications") val notifications: Int? = 0,
    @Json(name = "total") val total: Int? = 0
)

@JsonClass(generateAdapter = true)
data class InboxEmptyStateDto(
    @Json(name = "has_items") val hasItems: Boolean? = false,
    @Json(name = "title") val title: String? = null,
    @Json(name = "subtitle") val subtitle: String? = null,
    @Json(name = "action_text") val actionText: String? = null,
    @Json(name = "action_url") val actionUrl: String? = null
)

@JsonClass(generateAdapter = true)
data class InboxSummaryDataDto(
    @Json(name = "unread_chats") val unreadChats: Int? = 0,
    @Json(name = "unread_notifications") val unreadNotifications: Int? = 0,
    @Json(name = "total_unread") val totalUnread: Int? = 0,
    @Json(name = "chat") val chat: Int? = 0,
    @Json(name = "notifications") val notifications: Int? = 0,
    @Json(name = "total") val total: Int? = 0
) {
    val effectiveUnreadChats: Int
        get() = unreadChats ?: chat ?: 0

    val effectiveUnreadNotifications: Int
        get() = unreadNotifications ?: notifications ?: 0

    val effectiveTotalUnread: Int
        get() = totalUnread ?: total ?: 0
}

@JsonClass(generateAdapter = true)
data class InboxSummaryResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: InboxSummaryDataDto? = null
)

@JsonClass(generateAdapter = true)
data class InboxItemDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "type") val type: String? = null, // "chat" or "notification", "new_badge_received"
    @Json(name = "title") val title: String? = null,
    @Json(name = "subtitle") val subtitle: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "image_url") val imageUrl: String? = null,
    @Json(name = "is_read") val isRead: Boolean? = false,
    @Json(name = "read_at") val readAt: String? = null,
    @Json(name = "unread_count") val unreadCount: Int? = 0,
    @Json(name = "timestamp") val timestamp: String? = null,
    @Json(name = "created_at") val createdAt: String? = null,
    @Json(name = "conversation_id") val conversationId: Any? = null,
    @Json(name = "notification_id") val notificationId: Any? = null,
    @Json(name = "campaign_id") val campaignId: Any? = null
) {
    val effectiveTitle: String
        get() = title?.takeIf { it.isNotBlank() } ?: subtitle ?: "Notification"

    val effectiveMessage: String
        get() = message?.takeIf { it.isNotBlank() } ?: subtitle ?: ""
}

@JsonClass(generateAdapter = true)
data class InboxDataDto(
    @Json(name = "active_tab") val activeTab: String? = null,
    @Json(name = "tabs") val tabs: List<InboxTabDto>? = null,
    @Json(name = "unread_counts") val unreadCounts: InboxUnreadCountsDto? = null,
    @Json(name = "empty_state") val emptyState: InboxEmptyStateDto? = null,
    @Json(name = "items") val items: List<InboxItemDto>? = null,
    @Json(name = "pagination") val pagination: PaginationDto? = null
)

@JsonClass(generateAdapter = true)
data class UnifiedInboxResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "tab") val tab: String? = null,
    @Json(name = "data") val data: InboxDataDto? = null,
    @Json(name = "items") val items: List<InboxItemDto>? = null,
    @Json(name = "unread_counts") val unreadCounts: InboxUnreadCountsDto? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
) {
    val effectiveItems: List<InboxItemDto>
        get() = data?.items ?: items ?: emptyList()
}
