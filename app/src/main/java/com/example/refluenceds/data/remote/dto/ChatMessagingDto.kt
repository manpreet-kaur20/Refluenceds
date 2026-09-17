package com.example.refluenceds.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class MessageDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "conversation_id") val conversationId: Any? = null,
    @Json(name = "sender_id") val senderId: Any? = null,
    @Json(name = "sender_name") val senderName: String? = null,
    @Json(name = "sender_avatar") val senderAvatar: String? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "message_type") val messageType: String? = "text", // text, image, file, system
    @Json(name = "attachment_url") val attachmentUrl: String? = null,
    @Json(name = "attachment_name") val attachmentName: String? = null,
    @Json(name = "is_mine") val isMine: Boolean? = false,
    @Json(name = "is_read") val isRead: Boolean? = false,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class ConversationParticipantDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "avatar") val avatar: String? = null,
    @Json(name = "role") val role: String? = null,
    @Json(name = "is_online") val isOnline: Boolean? = false
)

@JsonClass(generateAdapter = true)
data class ConversationDto(
    @Json(name = "id") val id: Any? = null,
    @Json(name = "campaign_id") val campaignId: Any? = null,
    @Json(name = "campaign_title") val campaignTitle: String? = null,
    @Json(name = "campaign_image") val campaignImage: String? = null,
    @Json(name = "participant") val participant: ConversationParticipantDto? = null,
    @Json(name = "latest_message") val latestMessage: MessageDto? = null,
    @Json(name = "unread_count") val unreadCount: Int? = 0,
    @Json(name = "updated_at") val updatedAt: String? = null,
    @Json(name = "created_at") val createdAt: String? = null
)

@JsonClass(generateAdapter = true)
data class ConversationListDataDto(
    @Json(name = "items") val items: List<ConversationDto>? = null,
    @Json(name = "conversations") val conversations: List<ConversationDto>? = null,
    @Json(name = "data") val data: List<ConversationDto>? = null,
    @Json(name = "pagination") val pagination: PaginationDto? = null
)

@JsonClass(generateAdapter = true)
data class ConversationListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: ConversationListDataDto? = null,
    @Json(name = "items") val items: List<ConversationDto>? = null,
    @Json(name = "conversations") val conversations: List<ConversationDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 15,
    @Json(name = "total") val total: Int? = 0
) {
    val effectiveItems: List<ConversationDto>
        get() = data?.items
            ?: data?.conversations
            ?: data?.data
            ?: items
            ?: conversations
            ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class ConversationDetailResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: ConversationDto? = null
)

@JsonClass(generateAdapter = true)
data class MessageListDataDto(
    @Json(name = "items") val items: List<MessageDto>? = null,
    @Json(name = "messages") val messages: List<MessageDto>? = null,
    @Json(name = "data") val data: List<MessageDto>? = null,
    @Json(name = "pagination") val pagination: PaginationDto? = null
)

@JsonClass(generateAdapter = true)
data class MessageListResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: MessageListDataDto? = null,
    @Json(name = "items") val items: List<MessageDto>? = null,
    @Json(name = "messages") val messages: List<MessageDto>? = null,
    @Json(name = "current_page") val currentPage: Int? = 1,
    @Json(name = "per_page") val perPage: Int? = 25,
    @Json(name = "total") val total: Int? = 0
) {
    val effectiveItems: List<MessageDto>
        get() = data?.items
            ?: data?.messages
            ?: data?.data
            ?: items
            ?: messages
            ?: emptyList()
}

@JsonClass(generateAdapter = true)
data class MessageDetailResponseDto(
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null,
    @Json(name = "data") val data: MessageDto? = null
)

@JsonClass(generateAdapter = true)
data class StartConversationRequestDto(
    @Json(name = "campaign_id") val campaignId: Any? = null,
    @Json(name = "recipient_user_id") val recipientUserId: Any? = null,
    @Json(name = "recipient_brand_id") val recipientBrandId: Any? = null,
    @Json(name = "message") val message: String? = null
)
