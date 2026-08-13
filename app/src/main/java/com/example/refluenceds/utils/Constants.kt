package com.example.refluenceds.utils

object Constants {

    const val BASE_URL = "https://api.refluenceds.com/v1/"

    // ── Auth & Security Endpoints ─────────────────────────────────────────────
    const val LOGIN_ENDPOINT = "auth/login"
    const val SIGNUP_ENDPOINT = "auth/signup"
    const val FORGOT_PASSWORD_ENDPOINT = "auth/forgot-password"
    const val EMAIL_VERIFY_ENDPOINT = "auth/verify-email"
    const val SOCIAL_VERIFY_ENDPOINT = "auth/verify-social"
    const val LOGOUT_ENDPOINT = "auth/logout"
    const val CHANGE_PASSWORD_ENDPOINT = "auth/change-password"

    // ── User & Profile Endpoints ──────────────────────────────────────────────
    const val ONBOARDING_ENDPOINT = "user/onboarding"
    const val PROFILE_ENDPOINT = "user/profile"
    const val EDIT_PROFILE_ENDPOINT = "user/edit-profile"
    const val DELETE_ACCOUNT_ENDPOINT = "user/delete-account"
    const val NOTIFICATIONS_ENDPOINT = "user/notifications"
    const val PUSH_PREFERENCES_ENDPOINT = "user/push-preferences"
    const val EMAIL_PREFERENCES_ENDPOINT = "user/email-preferences"

    // ── Home & Dashboard Endpoints ────────────────────────────────────────────
    const val HOME_ENDPOINT = "home"
    const val PRODUCTS_ENDPOINT = "products"

    // ── Campaigns & Details Endpoints ─────────────────────────────────────────
    const val CAMPAIGNS_ENDPOINT = "campaigns"
    const val CAMPAIGN_DETAIL_ENDPOINT = "campaigns/{id}"
    const val FAVORITE_CAMPAIGN_ENDPOINT = "campaigns/{id}/favorite"
    const val REPORT_CAMPAIGN_ENDPOINT = "campaigns/{id}/report"
    const val APPLY_CAMPAIGN_ENDPOINT = "campaigns/{id}/apply"

    // ── Brands & Followers Endpoints ──────────────────────────────────────────
    const val BRANDS_ENDPOINT = "brands"
    const val FOLLOW_BRAND_ENDPOINT = "brands/{id}/follow"

    // ── Referral & Earnings Endpoints ─────────────────────────────────────────
    const val REFERRALS_ENDPOINT = "referrals"
    const val EARNINGS_ENDPOINT = "earnings"
    const val PAYOUT_WITHDRAW_ENDPOINT = "earnings/withdraw"

    // ── Academy & Submissions Endpoints ──────────────────────────────────────
    const val ACADEMY_ENDPOINT = "academy"
    const val ACADEMY_DETAIL_ENDPOINT = "academy/{id}"
    const val SUBMISSIONS_ENDPOINT = "submissions"

    // Network Timeout
    const val NETWORK_TIMEOUT = 30L
}
