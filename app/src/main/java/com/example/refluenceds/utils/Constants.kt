package com.example.refluenceds.utils

object Constants {

    const val BASE_URL = "http://162.241.68.61/refluenced/api/v1/"

    // ── External Web Pages ───────────────────────────────────────────────────
    const val TERMS_WEB_URL = "http://162.241.68.61/refluenced/terms"
    const val PRIVACY_WEB_URL = "http://162.241.68.61/refluenced/privacy"
    const val FAQ_WEB_URL = "http://162.241.68.61/refluenced/faq"
    const val REFLUENCED_INSTAGRAM_URL = "https://www.instagram.com/refluenced/"
    const val REFLUENCED_TIKTOK_URL = "https://www.tiktok.com/@refluenced"
    const val PODCAST_YOUTUBE_URL = "https://www.youtube.com/@refluenced"
    const val PODCAST_SPOTIFY_URL = "https://open.spotify.com/show/6BO5L36ZPiCo48ZQcicroj"
    const val PODCAST_APPLE_URL = "https://podcasts.apple.com/ch/podcast/refluenced-offline-talks/id1706758757"

    // ── Module 1: Auth & Security Endpoints ─────────────────────────────────
    const val REGISTER_ENDPOINT = "register"
    const val LOGIN_ENDPOINT = "login"
    const val SOCIAL_LOGIN_ENDPOINT = "social-login"
    const val CURRENT_USER_ENDPOINT = "user"
    const val FORGOT_PASSWORD_ENDPOINT = "forgot-password"
    const val RESET_PASSWORD_ENDPOINT = "reset-password"
    const val LOGOUT_ENDPOINT = "logout"

    // ── Module 2: Profile & Account Settings ─────────────────────────────────
    const val PROFILE_ENDPOINT = "profile"
    const val EDIT_PROFILE_ENDPOINT = "profile"
    const val CHANGE_PASSWORD_ENDPOINT = "change-password"
    const val FEEDBACK_ENDPOINT = "feedback"
    const val DELETE_ACCOUNT_ENDPOINT = "profile"

    // ── Module 3: Home & Mobile Feed ─────────────────────────────────────────
    const val HOME_ENDPOINT = "home"
    const val RECOMMENDED_CAMPAIGNS_ENDPOINT = "campaigns/recommended"

    // ── Module 4: Mobile Unified Inbox ───────────────────────────────────────
    const val INBOX_ENDPOINT = "inbox"
    const val INBOX_SUMMARY_ENDPOINT = "inbox/summary"

    // ── Module 5: Community & Campaign Content Feed ──────────────────────────
    const val CONTENT_FEED_ENDPOINT = "content-feed"
    const val CONTENT_FEED_CATEGORIES_ENDPOINT = "content-feed/categories"
    const val CONTENT_FEED_REPORT_REASONS_ENDPOINT = "content-feed/report-reasons"

    // ── Module 6: Influencer Showcase & Portfolio ────────────────────────────
    const val INFLUENCERS_ENDPOINT = "influencers"

    // ── Module 7: Refluenced Academy ─────────────────────────────────────────
    const val ACADEMY_CATEGORIES_ENDPOINT = "academy/categories"
    const val ACADEMY_VIDEOS_ENDPOINT = "academy/videos"
    const val ACADEMY_ENDPOINT = "academy"

    // ── Module 8: Brand Following & My Brands ────────────────────────────────
    const val MY_BRANDS_ENDPOINT = "my/brands"

    // ── Module 9: Referrals & Invites ────────────────────────────────────────
    const val REFERRAL_MY_CODE_ENDPOINT = "referral/my-code"
    const val REFERRAL_CONFIRM_ENDPOINT = "referral/confirm"
    const val REFERRAL_SKIP_ENDPOINT = "referral/skip"
    const val REFERRALS_ENDPOINT = "referrals"
    const val EARNINGS_ENDPOINT = "earnings"
    const val PAYOUT_WITHDRAW_ENDPOINT = "earnings/withdraw"

    // ── Module 10: Geo & Master Data ─────────────────────────────────────────
    const val COUNTRIES_ENDPOINT = "countries"
    const val INDUSTRIES_ENDPOINT = "industries"
    const val TERMS_ENDPOINT = "terms"

    // ── Module 11: Creator Onboarding Steps ──────────────────────────────────
    const val ONBOARDING_STATUS_ENDPOINT = "onboarding/status"
    const val ONBOARDING_ENDPOINT = "onboarding"

    // ── Module 12: Brand Management ──────────────────────────────────────────
    const val BRANDS_ME_ENDPOINT = "brands/me"
    const val BRANDS_ENDPOINT = "brands"
    const val BRANDS_UPDATE_ENDPOINT = "brands/update"
    const val FOLLOW_BRAND_ENDPOINT = "brands/{id}/follow"

    // ── Module 13: Campaign Management (Brands) ──────────────────────────────
    const val BRAND_CAMPAIGNS_ENDPOINT = "brand/campaigns"

    // ── Module 14: Campaign Discovery (Public & Creators) ────────────────────
    const val CAMPAIGNS_ENDPOINT = "campaigns"
    const val CAMPAIGNS_FILTERS_ENDPOINT = "campaigns/filters"
    const val CAMPAIGNS_REPORT_REASONS_ENDPOINT = "campaigns/report-reasons"
    const val CAMPAIGN_DETAIL_ENDPOINT = "campaigns/{id}"
    const val FAVORITE_CAMPAIGN_ENDPOINT = "campaigns/{id}/favorite"
    const val REPORT_CAMPAIGN_ENDPOINT = "campaigns/{id}/report"
    const val MY_FAVORITE_CAMPAIGNS_ENDPOINT = "my/favorite-campaigns"

    // ── Module 15: Campaign Applications ─────────────────────────────────────
    const val APPLY_CAMPAIGN_ENDPOINT = "campaigns/{id}/apply"
    const val MY_APPLICATIONS_ENDPOINT = "my/applications"
    const val BRAND_APPLICATIONS_ENDPOINT = "brand/applications"

    // ── Module 16: Campaign Invitations ──────────────────────────────────────
    const val BRAND_INVITATIONS_ENDPOINT = "brand/invitations"
    const val MY_INVITATIONS_ENDPOINT = "my/invitations"

    // ── Module 17: Creator Discovery (For Brands) ────────────────────────────
    const val CREATORS_ENDPOINT = "creators"

    // ── Module 18: Ratings & Reviews ─────────────────────────────────────────
    const val RATINGS_ENDPOINT = "ratings"
    const val MY_RATINGS_ENDPOINT = "my/ratings"

    // ── Module 19: Notifications Center ──────────────────────────────────────
    const val NOTIFICATIONS_ENDPOINT = "notifications"
    const val USER_NOTIFICATIONS_ENDPOINT = "user/notifications"
    const val NOTIFICATIONS_READ_ALL_ENDPOINT = "notifications/read-all"

    // ── Module 20: Chat & Messaging ──────────────────────────────────────────
    const val CONVERSATIONS_ENDPOINT = "conversations"

    // ── Module 21: Badges & Achievements ─────────────────────────────────────
    const val BADGES_ENDPOINT = "badges"
    const val MY_BADGES_ENDPOINT = "my/badges"

    // Legacy & Aux Endpoints
    const val PRODUCTS_ENDPOINT = "products"
    const val SUBMISSIONS_ENDPOINT = "submissions"

    // Network Timeout
    const val NETWORK_TIMEOUT = 30L
}
