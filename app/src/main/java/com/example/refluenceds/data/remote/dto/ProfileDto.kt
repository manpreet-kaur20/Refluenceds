package com.example.refluenceds.data.remote.dto

import com.example.refluenceds.domain.model.UserProfile
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ProfileResponseDto(
    @Json(name = "id") val id: String? = null,
    @Json(name = "name") val name: String? = null,
    @Json(name = "first_name") val firstName: String? = null,
    @Json(name = "last_name") val lastName: String? = null,
    @Json(name = "email") val email: String? = null,
    @Json(name = "avatarUrl") val avatarUrl: String? = null,
    @Json(name = "profile_picture") val profilePicture: String? = null,
    @Json(name = "bio") val bio: String? = null,
    @Json(name = "followersCount") val followersCount: Int? = 0,
    @Json(name = "user") val user: UserDto? = null,
    @Json(name = "data") val data: UserDto? = null,
    @Json(name = "status") val status: Any? = null,
    @Json(name = "success") val success: Boolean? = null,
    @Json(name = "message") val message: String? = null
) {
    fun toDomain(): UserProfile {
        return UserProfile(
            id = id ?: user?.id?.toString() ?: "",
            name = name ?: user?.fullName ?: "${user?.firstName.orEmpty()} ${user?.lastName.orEmpty()}".trim(),
            email = email ?: user?.email.orEmpty(),
            avatarUrl = avatarUrl ?: profilePicture ?: user?.profilePicture.orEmpty(),
            followersCount = followersCount ?: 0
        )
    }

    fun getEffectiveUser(): UserDto? = user ?: data
}

@JsonClass(generateAdapter = true)
data class PushPreferencesDto(
    @Json(name = "push_invitations_from_brands") val pushInvitationsFromBrands: Any? = true,
    @Json(name = "push_recommended_campaigns") val pushRecommendedCampaigns: Any? = true,
    @Json(name = "push_new_rating_received") val pushNewRatingReceived: Any? = true,
    @Json(name = "push_new_badge_received") val pushNewBadgeReceived: Any? = true,
    @Json(name = "push_accepted_to_campaign") val pushAcceptedToCampaign: Any? = true,
    @Json(name = "push_content_creation_reminder") val pushContentCreationReminder: Any? = true,
    @Json(name = "push_chat_notifications") val pushChatNotifications: Any? = true,
    @Json(name = "push_brand_has_sent_product") val pushBrandHasSentProduct: Any? = true,
    @Json(name = "invitations") val invitations: Any? = null,
    @Json(name = "recommended") val recommended: Any? = null,
    @Json(name = "rating_received") val ratingReceived: Any? = null,
    @Json(name = "badge_received") val badgeReceived: Any? = null
) {
    fun getInvitations(): Boolean = (pushInvitationsFromBrands ?: invitations).toBooleanFlag(true)
    fun getRecommended(): Boolean = (pushRecommendedCampaigns ?: recommended).toBooleanFlag(true)
    fun getRatingReceived(): Boolean = (pushNewRatingReceived ?: ratingReceived).toBooleanFlag(true)
    fun getBadgeReceived(): Boolean = (pushNewBadgeReceived ?: badgeReceived).toBooleanFlag(true)
    fun getAcceptedToCampaign(): Boolean = pushAcceptedToCampaign.toBooleanFlag(true)
    fun getContentCreationReminder(): Boolean = pushContentCreationReminder.toBooleanFlag(true)
    fun getChatNotifications(): Boolean = pushChatNotifications.toBooleanFlag(true)
    fun getBrandHasSentProduct(): Boolean = pushBrandHasSentProduct.toBooleanFlag(true)

    fun toMap(): Map<String, String> = mapOf(
        "push_invitations_from_brands" to if (getInvitations()) "1" else "0",
        "push_recommended_campaigns" to if (getRecommended()) "1" else "0",
        "push_new_rating_received" to if (getRatingReceived()) "1" else "0",
        "push_new_badge_received" to if (getBadgeReceived()) "1" else "0",
        "push_accepted_to_campaign" to if (getAcceptedToCampaign()) "1" else "0",
        "push_content_creation_reminder" to if (getContentCreationReminder()) "1" else "0",
        "push_chat_notifications" to if (getChatNotifications()) "1" else "0",
        "push_brand_has_sent_product" to if (getBrandHasSentProduct()) "1" else "0"
    )
}

@JsonClass(generateAdapter = true)
data class EmailPreferencesDto(
    @Json(name = "email_invitations_from_brands") val emailInvitationsFromBrands: Any? = true,
    @Json(name = "email_recommended_campaigns") val emailRecommendedCampaigns: Any? = true,
    @Json(name = "email_new_rating_received") val emailNewRatingReceived: Any? = true,
    @Json(name = "email_accepted_to_campaign") val emailAcceptedToCampaign: Any? = true,
    @Json(name = "email_content_creation_reminder") val emailContentCreationReminder: Any? = true,
    @Json(name = "email_chat_notifications") val emailChatNotifications: Any? = true,
    @Json(name = "email_brand_has_sent_product") val emailBrandHasSentProduct: Any? = true,
    @Json(name = "email_newsletter") val emailNewsletter: Any? = true,
    @Json(name = "invitations") val invitations: Any? = null,
    @Json(name = "recommended") val recommended: Any? = null,
    @Json(name = "rating_received") val ratingReceived: Any? = null,
    @Json(name = "accepted") val accepted: Any? = null,
    @Json(name = "content_reminder") val contentReminder: Any? = null,
    @Json(name = "chat_notifications") val chatNotifications: Any? = null,
    @Json(name = "brand_sent_product") val brandSentProduct: Any? = null,
    @Json(name = "newsletter") val newsletter: Any? = null
) {
    fun getInvitations(): Boolean = (emailInvitationsFromBrands ?: invitations).toBooleanFlag(true)
    fun getRecommended(): Boolean = (emailRecommendedCampaigns ?: recommended).toBooleanFlag(true)
    fun getRatingReceived(): Boolean = (emailNewRatingReceived ?: ratingReceived).toBooleanFlag(true)
    fun getAccepted(): Boolean = (emailAcceptedToCampaign ?: accepted).toBooleanFlag(true)
    fun getContentReminder(): Boolean = (emailContentCreationReminder ?: contentReminder).toBooleanFlag(true)
    fun getChatNotifications(): Boolean = (emailChatNotifications ?: chatNotifications).toBooleanFlag(true)
    fun getBrandSentProduct(): Boolean = (emailBrandHasSentProduct ?: brandSentProduct).toBooleanFlag(true)
    fun getBrandHasSentProduct(): Boolean = getBrandSentProduct()
    fun getNewsletter(): Boolean = (emailNewsletter ?: newsletter).toBooleanFlag(true)

    fun toMap(): Map<String, String> = mapOf(
        "email_invitations_from_brands" to if (getInvitations()) "1" else "0",
        "email_recommended_campaigns" to if (getRecommended()) "1" else "0",
        "email_new_rating_received" to if (getRatingReceived()) "1" else "0",
        "email_accepted_to_campaign" to if (getAccepted()) "1" else "0",
        "email_content_creation_reminder" to if (getContentReminder()) "1" else "0",
        "email_chat_notifications" to if (getChatNotifications()) "1" else "0",
        "email_brand_has_sent_product" to if (getBrandSentProduct()) "1" else "0",
        "email_newsletter" to if (getNewsletter()) "1" else "0"
    )
}

internal fun Any?.toBooleanFlag(default: Boolean = false): Boolean {
    return when (this) {
        is Boolean -> this
        is Number -> this.toInt() == 1
        is String -> this == "1" || this.equals("true", ignoreCase = true)
        else -> default
    }
}
