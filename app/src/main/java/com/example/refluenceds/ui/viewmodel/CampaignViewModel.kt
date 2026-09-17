package com.example.refluenceds.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.refluenceds.data.remote.datasource.RemoteDataSource
import com.example.refluenceds.data.remote.dto.*
import com.example.refluenceds.domain.model.*
import com.example.refluenceds.domain.repository.CampaignRepository
import com.example.refluenceds.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ContentFeedPaginationState(
    val currentPage: Int = 1,
    val lastPage: Int = 1,
    val perPage: Int = 10,
    val total: Int = 0,
    val hasMore: Boolean = false,
    val isLoadingMore: Boolean = false
)

@HiltViewModel
class CampaignViewModel @Inject constructor(
    private val repository: CampaignRepository,
    private val remoteDataSource: RemoteDataSource
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory = _selectedCategory.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading = _isLoading.asStateFlow()

    private val _isHomeLoading = MutableStateFlow(false)
    val isHomeLoading: StateFlow<Boolean> = _isHomeLoading.asStateFlow()

    private val _isContentFeedLoading = MutableStateFlow(false)
    val isContentFeedLoading: StateFlow<Boolean> = _isContentFeedLoading.asStateFlow()

    private val _isCampaignsLoading = MutableStateFlow(false)
    val isCampaignsLoading: StateFlow<Boolean> = _isCampaignsLoading.asStateFlow()

    private val _isInboxLoading = MutableStateFlow(false)
    val isInboxLoading: StateFlow<Boolean> = _isInboxLoading.asStateFlow()

    // ── Live Server Data States ──────────────────────────────────────────────
    private val _homeData = MutableStateFlow<HomeFeedDataDto?>(null)
    val homeData: StateFlow<HomeFeedDataDto?> = _homeData.asStateFlow()

    private val _liveCampaigns = MutableStateFlow<List<Campaign>>(emptyList())
    val liveCampaigns: StateFlow<List<Campaign>> = _liveCampaigns.asStateFlow()

    private val _campaignDiscovery = MutableStateFlow<List<CampaignDetailDto>>(emptyList())
    val campaignDiscovery: StateFlow<List<CampaignDetailDto>> = _campaignDiscovery.asStateFlow()

    private val _contentFeed = MutableStateFlow<List<ContentFeedItemDto>>(emptyList())
    val contentFeed: StateFlow<List<ContentFeedItemDto>> = _contentFeed.asStateFlow()

    private val _liveSocialPosts = MutableStateFlow<List<Post>>(emptyList())

    private val _inboxItems = MutableStateFlow<List<InboxItemDto>>(emptyList())
    val inboxItems: StateFlow<List<InboxItemDto>> = _inboxItems.asStateFlow()

    private val _inboxSummary = MutableStateFlow<InboxSummaryDataDto?>(null)
    val inboxSummary: StateFlow<InboxSummaryDataDto?> = _inboxSummary.asStateFlow()

    private val _badges = MutableStateFlow<List<BadgeDto>>(emptyList())
    val badges: StateFlow<List<BadgeDto>> = _badges.asStateFlow()

    private val _liveTutorials = MutableStateFlow<List<Tutorial>>(emptyList())

    private val _myBrands = MutableStateFlow<List<BrandItemDto>>(emptyList())
    val myBrands: StateFlow<List<BrandItemDto>> = _myBrands.asStateFlow()

    private val _influencerProfile = MutableStateFlow<InfluencerProfileDto?>(null)
    val influencerProfile: StateFlow<InfluencerProfileDto?> = _influencerProfile.asStateFlow()

    private val _influencerInstagramFeed = MutableStateFlow<List<InfluencerSocialPostDto>>(emptyList())
    val influencerInstagramFeed: StateFlow<List<InfluencerSocialPostDto>> = _influencerInstagramFeed.asStateFlow()

    private val _influencerTiktokFeed = MutableStateFlow<List<InfluencerSocialPostDto>>(emptyList())
    val influencerTiktokFeed: StateFlow<List<InfluencerSocialPostDto>> = _influencerTiktokFeed.asStateFlow()

    private val _influencerCampaignContent = MutableStateFlow<List<ContentFeedItemDto>>(emptyList())
    val influencerCampaignContent: StateFlow<List<ContentFeedItemDto>> = _influencerCampaignContent.asStateFlow()

    private val _isInfluencerLoading = MutableStateFlow(false)
    val isInfluencerLoading: StateFlow<Boolean> = _isInfluencerLoading.asStateFlow()

    private val _brandDetail = MutableStateFlow<BrandItemDto?>(null)
    val brandDetail: StateFlow<BrandItemDto?> = _brandDetail.asStateFlow()

    private val _isBrandDetailLoading = MutableStateFlow(false)
    val isBrandDetailLoading: StateFlow<Boolean> = _isBrandDetailLoading.asStateFlow()

    private val _brandActiveCampaigns = MutableStateFlow<List<CampaignDetailDto>>(emptyList())
    val brandActiveCampaigns: StateFlow<List<CampaignDetailDto>> = _brandActiveCampaigns.asStateFlow()

    private val _brandPastCampaigns = MutableStateFlow<List<CampaignDetailDto>>(emptyList())
    val brandPastCampaigns: StateFlow<List<CampaignDetailDto>> = _brandPastCampaigns.asStateFlow()

    private val _brandRatings = MutableStateFlow<List<RatingItemDto>>(emptyList())
    val brandRatings: StateFlow<List<RatingItemDto>> = _brandRatings.asStateFlow()

    private val _brandRatingsSummary = MutableStateFlow<BrandRatingsSummaryDto?>(null)
    val brandRatingsSummary: StateFlow<BrandRatingsSummaryDto?> = _brandRatingsSummary.asStateFlow()

    private val _isBrandRatingsLoading = MutableStateFlow(false)
    val isBrandRatingsLoading: StateFlow<Boolean> = _isBrandRatingsLoading.asStateFlow()

    // ── Your Campaigns (Favorites, Past, Hidden) ─────────────────────────────
    private val _favoriteCampaigns = MutableStateFlow<List<CampaignDetailDto>>(emptyList())
    val favoriteCampaigns: StateFlow<List<CampaignDetailDto>> = _favoriteCampaigns.asStateFlow()

    private val _pastCampaigns = MutableStateFlow<List<CampaignDetailDto>>(emptyList())
    val pastCampaigns: StateFlow<List<CampaignDetailDto>> = _pastCampaigns.asStateFlow()

    private val _hiddenCampaigns = MutableStateFlow<List<CampaignDetailDto>>(emptyList())
    val hiddenCampaigns: StateFlow<List<CampaignDetailDto>> = _hiddenCampaigns.asStateFlow()

    private val _isYourCampaignsLoading = MutableStateFlow(false)
    val isYourCampaignsLoading: StateFlow<Boolean> = _isYourCampaignsLoading.asStateFlow()

    // ── Your Collection (Instagram, TikTok, Academy) ─────────────────────────
    private val _collectionItems = MutableStateFlow<List<ContentFeedItemDto>>(emptyList())
    val collectionItems: StateFlow<List<ContentFeedItemDto>> = _collectionItems.asStateFlow()

    private val _isCollectionLoading = MutableStateFlow(false)
    val isCollectionLoading: StateFlow<Boolean> = _isCollectionLoading.asStateFlow()

    // ── Refluenced Academy ───────────────────────────────────────────────────
    private val _academyCategories = MutableStateFlow<List<String>>(listOf("Onboarding", "Basics", "Most popular"))
    val academyCategories: StateFlow<List<String>> = _academyCategories.asStateFlow()

    private val _isAcademyLoading = MutableStateFlow(false)
    val isAcademyLoading: StateFlow<Boolean> = _isAcademyLoading.asStateFlow()

    // ── Chat & Messaging (Module 20) ─────────────────────────────────────────
    private val _conversations = MutableStateFlow<List<ConversationDto>>(emptyList())
    val conversations: StateFlow<List<ConversationDto>> = _conversations.asStateFlow()

    private val _currentMessages = MutableStateFlow<List<MessageDto>>(emptyList())
    val currentMessages: StateFlow<List<MessageDto>> = _currentMessages.asStateFlow()

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading.asStateFlow()

    private val _isSendingMessage = MutableStateFlow(false)
    val isSendingMessage: StateFlow<Boolean> = _isSendingMessage.asStateFlow()

    val categories = listOf("All", "Fashion", "Lifestyle", "Events", "Gaming")

    init {
        fetchHomeData()
        fetchContentFeed()
        fetchCampaigns()
        fetchUnifiedInbox()
        fetchConversations()
        fetchMyBadges()
    }

    // ── Home Data API (Module 3) ──────────────────────────────────────────────
    fun fetchHomeData(
        recommendedLimit: Int = 5,
        academyLimit: Int = 6,
        brandsLimit: Int = 6
    ) {
        viewModelScope.launch {
            _isHomeLoading.value = true
            when (val result = remoteDataSource.getHomeData(recommendedLimit, academyLimit, brandsLimit)) {
                is NetworkResult.Success -> {
                    val data = result.data.data
                    _homeData.value = data
                    data?.recommendedCampaigns?.let { dtoList ->
                        if (dtoList.isNotEmpty()) {
                            _liveCampaigns.value = dtoList.map { it.toDomain() }
                        }
                    }
                    data?.academyPreview?.let { dtoList ->
                        if (dtoList.isNotEmpty()) {
                            _liveTutorials.value = dtoList.map { dto ->
                                Tutorial(
                                    id = dto.id?.toString() ?: "",
                                    title = dto.title ?: "",
                                    description = dto.description ?: "",
                                    thumbnailUrl = dto.thumbnailUrl ?: "",
                                    category = dto.category ?: "Onboarding",
                                    duration = dto.duration ?: "01:00",
                                    videoUrl = dto.videoUrl ?: ""
                                )
                            }
                        }
                    }
                    data?.exploreBrands?.let {
                        _myBrands.value = it
                    }
                }
                is NetworkResult.Error -> {}
                else -> {}
            }
            _isHomeLoading.value = false
        }
    }

    private val _contentFeedPagination = MutableStateFlow(ContentFeedPaginationState())
    val contentFeedPagination: StateFlow<ContentFeedPaginationState> = _contentFeedPagination.asStateFlow()

    private var currentFeedTab: String = "all"
    private var currentFeedPlatform: String? = null
    private var currentFeedSearch: String? = null
    private var currentFeedCategory: String? = null

    // ── Content Feed API (Module 5) ─────────────────────────────────────────
    fun fetchContentFeed(
        tab: String = "all",
        platform: String? = null,
        search: String? = null,
        category: String? = null,
        page: Int = 1,
        perPage: Int = 10,
        isLoadMore: Boolean = false
    ) {
        if (isLoadMore) {
            if (_contentFeedPagination.value.isLoadingMore || !_contentFeedPagination.value.hasMore) return
            _contentFeedPagination.update { it.copy(isLoadingMore = true) }
        } else {
            _isContentFeedLoading.value = true
        }

        currentFeedTab = tab
        currentFeedPlatform = platform
        currentFeedSearch = search
        currentFeedCategory = category

        viewModelScope.launch {
            when (val result = remoteDataSource.getContentFeed(
                tab = tab,
                platform = platform,
                search = search,
                category = category,
                perPage = perPage,
                page = page
            )) {
                is NetworkResult.Success -> {
                    val response = result.data
                    val data = response.data
                    val items = data?.items ?: emptyList()
                    val pagination = data?.pagination

                    val newPosts = items.map { item ->
                        val vid = (item.mediaUrl?.takeIf { it.isNotBlank() } ?: item.thumbnailUrl?.takeIf { it.isNotBlank() } ?: "").sanitizeMediaUrl()
                        val thumb = (item.thumbnailUrl?.takeIf { it.isNotBlank() } ?: item.mediaUrl?.takeIf { it.isNotBlank() } ?: "").sanitizeMediaUrl()
                        Post(
                            id = item.id?.toString() ?: "",
                            creatorId = item.effectiveCreatorId,
                            creatorName = item.effectiveCreatorName,
                            creatorAvatar = item.effectiveCreatorAvatar,
                            contentUrl = thumb,
                            videoUrl = if (item.mediaType?.lowercase() == "video" || vid.endsWith(".mp4", true) || !vid.matches(Regex(".*\\.(jpg|jpeg|png|webp|gif)$", RegexOption.IGNORE_CASE))) vid else "",
                            caption = item.caption ?: item.title ?: "",
                            likes = item.likesCount ?: 0,
                            timeAgo = item.createdAt ?: item.publishedAt ?: "recently",
                            creatorHandle = item.creatorHandleFlat ?: "",
                            commentsCount = item.commentsCount ?: 0,
                            viewsCount = item.viewsCount ?: 0,
                            platform = item.platform ?: "instagram",
                            brandId = item.effectiveBrandId,
                            brandName = item.effectiveBrandName,
                            brandLogo = item.effectiveBrandLogo,
                            isBookmarked = item.isBookmarked ?: false,
                            mediaType = item.mediaType ?: "video"
                        )
                    }

                    if (isLoadMore) {
                        _contentFeed.value = _contentFeed.value + items
                        _liveSocialPosts.value = _liveSocialPosts.value + newPosts
                    } else {
                        _contentFeed.value = items
                        _liveSocialPosts.value = newPosts
                    }

                    _contentFeedPagination.value = ContentFeedPaginationState(
                        currentPage = pagination?.currentPage ?: page,
                        lastPage = pagination?.lastPage ?: 1,
                        perPage = pagination?.perPage ?: perPage,
                        total = pagination?.total ?: items.size,
                        hasMore = pagination?.hasMore ?: (page < (pagination?.lastPage ?: 1)),
                        isLoadingMore = false
                    )
                }
                is NetworkResult.Error -> {
                    _contentFeedPagination.update { it.copy(isLoadingMore = false) }
                }
                else -> {
                    _contentFeedPagination.update { it.copy(isLoadingMore = false) }
                }
            }
            _isContentFeedLoading.value = false
        }
    }

    fun loadMoreContentFeed() {
        val next = _contentFeedPagination.value.currentPage + 1
        fetchContentFeed(
            tab = currentFeedTab,
            platform = currentFeedPlatform,
            search = currentFeedSearch,
            category = currentFeedCategory,
            page = next,
            perPage = _contentFeedPagination.value.perPage,
            isLoadMore = true
        )
    }

    fun fetchContentFeedDetail(contentId: Any, onResult: ((Post) -> Unit)? = null) {
        viewModelScope.launch {
            when (val res = remoteDataSource.getContentFeedDetail(contentId)) {
                is NetworkResult.Success -> {
                    res.data.data?.let { item ->
                        val vid = (item.mediaUrl?.takeIf { it.isNotBlank() } ?: item.thumbnailUrl?.takeIf { it.isNotBlank() } ?: "").sanitizeMediaUrl()
                        val thumb = (item.thumbnailUrl?.takeIf { it.isNotBlank() } ?: item.mediaUrl?.takeIf { it.isNotBlank() } ?: "").sanitizeMediaUrl()
                        val post = Post(
                            id = item.id?.toString() ?: contentId.toString(),
                            creatorId = item.effectiveCreatorId,
                            creatorName = item.effectiveCreatorName,
                            creatorAvatar = item.effectiveCreatorAvatar,
                            contentUrl = thumb,
                            videoUrl = if (item.mediaType?.lowercase() == "video" || vid.endsWith(".mp4", true) || !vid.matches(Regex(".*\\.(jpg|jpeg|png|webp|gif)$", RegexOption.IGNORE_CASE))) vid else "",
                            caption = item.caption ?: item.title ?: "",
                            likes = item.likesCount ?: 0,
                            timeAgo = item.createdAt ?: item.publishedAt ?: "recently",
                            creatorHandle = item.creatorHandleFlat ?: "",
                            commentsCount = item.commentsCount ?: 0,
                            viewsCount = item.viewsCount ?: 0,
                            platform = item.platform ?: "instagram",
                            brandId = item.effectiveBrandId,
                            brandName = item.effectiveBrandName,
                            brandLogo = item.effectiveBrandLogo,
                            isBookmarked = item.isBookmarked ?: false,
                            mediaType = item.mediaType ?: "video"
                        )
                        _liveSocialPosts.update { list ->
                            list.map { if (it.id == post.id) post else it }
                        }
                        onResult?.invoke(post)
                    }
                }
                else -> {}
            }
        }
    }

    // ── Campaigns Discovery API (Module 14) ──────────────────────────────────
    fun fetchCampaigns(
        viewMode: String? = null,
        filterPreset: String? = null,
        category: String? = null,
        search: String? = null,
        page: Int = 1,
        limit: Int = 30
    ) {
        viewModelScope.launch {
            _isCampaignsLoading.value = true
            when (val result = remoteDataSource.getCampaigns(
                viewMode = viewMode,
                filterPreset = filterPreset,
                search = search,
                page = page,
                perPage = limit
            )) {
                is NetworkResult.Success -> {
                    val items = result.data.effectiveItems
                    _campaignDiscovery.value = items
                    if (items.isNotEmpty()) {
                        _liveCampaigns.value = items.map { it.toDomain() }
                    } else if (filterPreset != null) {
                        // Fallback: if filtered preset returned 0, fetch general campaigns
                        when (val fallbackRes = remoteDataSource.getCampaigns(page = page, perPage = limit)) {
                            is NetworkResult.Success -> {
                                val fallbackItems = fallbackRes.data.effectiveItems
                                _campaignDiscovery.value = fallbackItems
                                _liveCampaigns.value = fallbackItems.map { it.toDomain() }
                            }
                            else -> {}
                        }
                    } else {
                        _liveCampaigns.value = emptyList()
                    }
                }
                is NetworkResult.Error -> {
                    // Try fallback without filter parameters
                    if (filterPreset != null || viewMode != null) {
                        when (val fallbackRes = remoteDataSource.getCampaigns(page = 1, perPage = limit)) {
                            is NetworkResult.Success -> {
                                val fallbackItems = fallbackRes.data.effectiveItems
                                _campaignDiscovery.value = fallbackItems
                                _liveCampaigns.value = fallbackItems.map { it.toDomain() }
                            }
                            else -> {}
                        }
                    } else {
                        _liveCampaigns.value = emptyList()
                    }
                }
                else -> {}
            }
            _isCampaignsLoading.value = false
        }
    }

    private val _campaignDetail = MutableStateFlow<CampaignDetailDto?>(null)
    val campaignDetail: StateFlow<CampaignDetailDto?> = _campaignDetail.asStateFlow()

    private val _isCampaignDetailLoading = MutableStateFlow(false)
    val isCampaignDetailLoading: StateFlow<Boolean> = _isCampaignDetailLoading.asStateFlow()

    fun fetchCampaignDetail(campaignId: Any) {
        val cid = campaignId.toString().trim()
        if (cid.isBlank()) return
        viewModelScope.launch {
            _isCampaignDetailLoading.value = true
            when (val result = remoteDataSource.getCampaignDetail(cid)) {
                is NetworkResult.Success -> {
                    _campaignDetail.value = result.data.data
                }
                is NetworkResult.Error -> {}
                else -> {}
            }
            _isCampaignDetailLoading.value = false
        }
    }

    // ── Unified Inbox & Notifications API (Modules 4, 19, 20) ───────────────
    fun fetchUnifiedInbox(tab: String = "chat") {
        viewModelScope.launch {
            _isInboxLoading.value = true
            if (tab == "chat") {
                launch { fetchConversations() }
            }
            when (val result = remoteDataSource.getUnifiedInbox(tab)) {
                is NetworkResult.Success -> {
                    val items = result.data.effectiveItems
                    if (tab == "chat" && items.isEmpty()) {
                        when (val convRes = remoteDataSource.getConversations(perPage = 15, page = 1)) {
                            is NetworkResult.Success -> {
                                val convList = convRes.data.effectiveItems
                                _conversations.value = convList
                                if (convList.isNotEmpty()) {
                                    _inboxItems.value = convList.map { c ->
                                        InboxItemDto(
                                            id = c.id,
                                            type = "chat",
                                            title = c.participant?.name ?: c.campaignTitle ?: "Brand Chat",
                                            subtitle = c.latestMessage?.message ?: c.campaignTitle,
                                            message = c.latestMessage?.message ?: "No messages yet",
                                            avatar = c.participant?.avatar ?: c.campaignImage,
                                            imageUrl = c.campaignImage,
                                            isRead = (c.unreadCount ?: 0) == 0,
                                            unreadCount = c.unreadCount ?: 0,
                                            timestamp = c.latestMessage?.createdAt ?: c.updatedAt ?: c.createdAt,
                                            createdAt = c.createdAt,
                                            conversationId = c.id,
                                            campaignId = c.campaignId
                                        )
                                    }
                                } else {
                                    _inboxItems.value = emptyList()
                                }
                            }
                            else -> {
                                _inboxItems.value = items
                            }
                        }
                    } else {
                        _inboxItems.value = items
                    }

                    result.data.data?.unreadCounts?.let { counts ->
                        _inboxSummary.value = InboxSummaryDataDto(
                            unreadChats = counts.chat ?: 0,
                            unreadNotifications = counts.notifications ?: 0,
                            totalUnread = counts.total ?: 0
                        )
                    }
                }
                is NetworkResult.Error -> {
                    if (tab == "chat") {
                        when (val convRes = remoteDataSource.getConversations(perPage = 15, page = 1)) {
                            is NetworkResult.Success -> {
                                val convList = convRes.data.effectiveItems
                                _conversations.value = convList
                                _inboxItems.value = convList.map { c ->
                                    InboxItemDto(
                                        id = c.id,
                                        type = "chat",
                                        title = c.participant?.name ?: c.campaignTitle ?: "Brand Chat",
                                        subtitle = c.latestMessage?.message ?: c.campaignTitle,
                                        message = c.latestMessage?.message ?: "No messages yet",
                                        avatar = c.participant?.avatar ?: c.campaignImage,
                                        imageUrl = c.campaignImage,
                                        isRead = (c.unreadCount ?: 0) == 0,
                                        unreadCount = c.unreadCount ?: 0,
                                        timestamp = c.latestMessage?.createdAt ?: c.updatedAt ?: c.createdAt,
                                        createdAt = c.createdAt,
                                        conversationId = c.id,
                                        campaignId = c.campaignId
                                    )
                                }
                            }
                            else -> {}
                        }
                    }
                }
                else -> {}
            }
            fetchInboxSummary()
            _isInboxLoading.value = false
        }
    }

    fun fetchInboxSummary() {
        viewModelScope.launch {
            when (val result = remoteDataSource.getInboxSummary()) {
                is NetworkResult.Success -> {
                    _inboxSummary.value = result.data.data
                }
                is NetworkResult.Error -> {}
                else -> {}
            }
        }
    }

    fun markNotificationAsRead(notificationId: Any) {
        viewModelScope.launch {
            _inboxItems.value = _inboxItems.value.map { item ->
                if (item.id?.toString() == notificationId.toString()) item.copy(isRead = true) else item
            }
            remoteDataSource.markNotificationRead(notificationId)
            fetchInboxSummary()
        }
    }

    fun markAllNotificationsAsRead() {
        viewModelScope.launch {
            _inboxItems.value = _inboxItems.value.map { it.copy(isRead = true) }
            val currentSummary = _inboxSummary.value
            _inboxSummary.value = currentSummary?.copy(
                unreadNotifications = 0,
                notifications = 0,
                totalUnread = currentSummary.unreadChats ?: 0
            )
            remoteDataSource.markAllNotificationsRead()
            fetchUnifiedInbox("notifications")
        }
    }

    fun deleteNotification(notificationId: Any) {
        viewModelScope.launch {
            _inboxItems.value = _inboxItems.value.filterNot { it.id?.toString() == notificationId.toString() }
            remoteDataSource.deleteNotification(notificationId)
            fetchInboxSummary()
        }
    }

    // ── Chat & Messaging API (Module 20) ─────────────────────────────────────
    fun fetchConversations(perPage: Int = 15, page: Int = 1) {
        viewModelScope.launch {
            _isChatLoading.value = true
            when (val result = remoteDataSource.getConversations(perPage, page)) {
                is NetworkResult.Success -> {
                    _conversations.value = result.data.effectiveItems
                }
                is NetworkResult.Error -> {}
                else -> {}
            }
            _isChatLoading.value = false
        }
    }

    fun fetchMessages(conversationId: Any, perPage: Int = 25, page: Int = 1) {
        viewModelScope.launch {
            _isChatLoading.value = true
            when (val result = remoteDataSource.getConversationMessages(conversationId, perPage, page)) {
                is NetworkResult.Success -> {
                    _currentMessages.value = result.data.effectiveItems
                }
                is NetworkResult.Error -> {}
                else -> {}
            }
            _isChatLoading.value = false
        }
    }

    fun sendMessage(
        conversationId: Any,
        message: String,
        messageType: String = "text",
        attachment: okhttp3.MultipartBody.Part? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        if (message.isBlank() && attachment == null) return
        viewModelScope.launch {
            _isSendingMessage.value = true
            when (val result = remoteDataSource.sendMessage(conversationId, message, messageType, attachment)) {
                is NetworkResult.Success -> {
                    result.data.data?.let { newMsg ->
                        _currentMessages.value = _currentMessages.value + newMsg
                    }
                    onSuccess?.invoke()
                }
                is NetworkResult.Error -> {}
                else -> {}
            }
            _isSendingMessage.value = false
        }
    }

    fun startConversation(
        campaignId: Any? = null,
        recipientUserId: Any? = null,
        recipientBrandId: Any? = null,
        message: String? = null,
        onSuccess: ((ConversationDto?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            _isSendingMessage.value = true
            when (val result = remoteDataSource.startConversation(campaignId, recipientUserId, recipientBrandId, message)) {
                is NetworkResult.Success -> {
                    val convo = result.data.data
                    convo?.let { c ->
                        _conversations.value = listOf(c) + _conversations.value
                    }
                    onSuccess?.invoke(convo)
                }
                is NetworkResult.Error -> {}
                else -> {}
            }
            _isSendingMessage.value = false
        }
    }

    fun markConversationAsRead(conversationId: Any) {
        viewModelScope.launch {
            _conversations.value = _conversations.value.map { c ->
                if (c.id?.toString() == conversationId.toString()) c.copy(unreadCount = 0) else c
            }
            remoteDataSource.markConversationRead(conversationId)
            fetchInboxSummary()
        }
    }

    // ── Badges API (Module 21) ──────────────────────────────────────────────
    fun fetchMyBadges() {
        viewModelScope.launch {
            when (val result = remoteDataSource.getMyEarnedBadges()) {
                is NetworkResult.Success -> {
                    _badges.value = result.data.data ?: emptyList()
                }
                is NetworkResult.Error -> {}
                else -> {}
            }
        }
    }

    // ── Influencer Showcase API (Module 6) ──────────────────────────────────
    fun fetchInfluencerData(creatorId: Any) {
        val cid = creatorId.toString().trim()
        if (cid.isBlank()) return
        viewModelScope.launch {
            _isInfluencerLoading.value = true
            _influencerProfile.value = null
            _influencerInstagramFeed.value = emptyList()
            _influencerTiktokFeed.value = emptyList()
            _influencerCampaignContent.value = emptyList()

            launch {
                when (val res = remoteDataSource.getInfluencerProfile(cid)) {
                    is NetworkResult.Success -> {
                        _influencerProfile.value = res.data.data
                        if (_influencerCampaignContent.value.isEmpty()) {
                            res.data.data?.campaignContent?.items?.let { items ->
                                if (items.isNotEmpty()) {
                                    _influencerCampaignContent.value = items
                                }
                            }
                        }
                    }
                    else -> {}
                }
            }

            launch {
                when (val res = remoteDataSource.getInfluencerSocialFeed(cid, platform = "instagram")) {
                    is NetworkResult.Success -> {
                        _influencerInstagramFeed.value = res.data.data ?: emptyList()
                    }
                    else -> {}
                }
            }

            launch {
                when (val res = remoteDataSource.getInfluencerSocialFeed(cid, platform = "tiktok")) {
                    is NetworkResult.Success -> {
                        _influencerTiktokFeed.value = res.data.data ?: emptyList()
                    }
                    else -> {}
                }
            }

            launch {
                when (val res = remoteDataSource.getInfluencerCampaignContent(cid)) {
                    is NetworkResult.Success -> {
                        _influencerCampaignContent.value = res.data.data ?: emptyList()
                    }
                    else -> {}
                }
            }

            _isInfluencerLoading.value = false
        }
    }

    // ── Brand Discovery API (Module 8 & 12) ──────────────────────────────────
    fun fetchMyBrands() {
        viewModelScope.launch {
            when (val result = remoteDataSource.getMyFollowedBrands()) {
                is NetworkResult.Success -> {
                    _myBrands.value = result.data.data ?: emptyList()
                }
                is NetworkResult.Error -> {}
                else -> {}
            }
        }
    }

    // ── Academy Tutorials API (Module 7) ────────────────────────────────────
    fun fetchTutorials(category: String? = null, search: String? = null) {
        viewModelScope.launch {
            _isAcademyLoading.value = true
            // Fetch live academy categories if available
            launch {
                when (val catRes = remoteDataSource.getAcademyCategories()) {
                    is NetworkResult.Success -> {
                        val catList = catRes.data.data?.mapNotNull { it.name } ?: emptyList()
                        if (catList.isNotEmpty()) {
                            _academyCategories.value = catList
                        }
                    }
                    else -> {}
                }
            }

            when (val result = remoteDataSource.getAcademyVideos(category = if (category == "All") null else category, search = search)) {
                is NetworkResult.Success -> {
                    val tutorials = result.data.effectiveItems
                    _liveTutorials.value = tutorials.map { dto ->
                        Tutorial(
                            id = dto.id?.toString() ?: "",
                            title = dto.title ?: "",
                            description = dto.description ?: "",
                            thumbnailUrl = dto.thumbnailUrl ?: "",
                            category = dto.category ?: "Onboarding",
                            duration = dto.duration ?: "01:00",
                            videoUrl = dto.videoUrl ?: ""
                        )
                    }
                }
                is NetworkResult.Error -> {
                    _liveTutorials.value = emptyList()
                }
                else -> {}
            }
            _isAcademyLoading.value = false
        }
    }

    private val _selectedAcademyVideo = MutableStateFlow<Tutorial?>(null)
    val selectedAcademyVideo: StateFlow<Tutorial?> = _selectedAcademyVideo.asStateFlow()

    fun fetchAcademyVideoDetail(videoId: String) {
        viewModelScope.launch {
            when (val res = remoteDataSource.getAcademyVideoDetail(videoId)) {
                is NetworkResult.Success -> {
                    res.data.data?.let { dto ->
                        val tutorial = Tutorial(
                            id = dto.id?.toString() ?: videoId,
                            title = dto.title ?: "",
                            description = dto.description ?: "",
                            thumbnailUrl = dto.thumbnailUrl ?: "",
                            category = dto.category ?: "Onboarding",
                            duration = dto.duration ?: "01:00",
                            videoUrl = dto.videoUrl ?: ""
                        )
                        _selectedAcademyVideo.value = tutorial
                        _liveTutorials.update { list ->
                            if (list.any { it.id == tutorial.id }) {
                                list.map { if (it.id == tutorial.id) tutorial else it }
                            } else {
                                list + tutorial
                            }
                        }
                    }
                }
                else -> {}
            }
        }
    }

    // ── Your Campaigns API ───────────────────────────────────────────────────
    fun fetchYourCampaigns(tabIndex: Int = 0) {
        viewModelScope.launch {
            _isYourCampaignsLoading.value = true
            when (tabIndex) {
                0 -> { // Favorites
                    when (val res = remoteDataSource.getMyFavoriteCampaigns()) {
                        is NetworkResult.Success -> {
                            _favoriteCampaigns.value = res.data.effectiveItems
                        }
                        is NetworkResult.Error -> {
                            _favoriteCampaigns.value = emptyList()
                        }
                        else -> {}
                    }
                }
                1 -> { // Past
                    when (val res = remoteDataSource.getCampaigns(viewMode = "past")) {
                        is NetworkResult.Success -> {
                            _pastCampaigns.value = res.data.effectiveItems
                        }
                        is NetworkResult.Error -> {
                            _pastCampaigns.value = emptyList()
                        }
                        else -> {}
                    }
                }
                2 -> { // Hidden
                    when (val res = remoteDataSource.getCampaigns(viewMode = "hidden")) {
                        is NetworkResult.Success -> {
                            _hiddenCampaigns.value = res.data.effectiveItems
                        }
                        is NetworkResult.Error -> {
                            _hiddenCampaigns.value = emptyList()
                        }
                        else -> {}
                    }
                }
            }
            _isYourCampaignsLoading.value = false
        }
    }

    // ── Your Collection API ──────────────────────────────────────────────────
    fun fetchCollection(platform: String = "Instagram") {
        viewModelScope.launch {
            _isCollectionLoading.value = true
            when (val res = remoteDataSource.getContentFeed(tab = "collections", platform = platform.lowercase())) {
                is NetworkResult.Success -> {
                    _collectionItems.value = res.data.effectiveItems
                }
                is NetworkResult.Error -> {
                    // Fallback to bookmarked items from social feed
                    val currentSocial = _liveSocialPosts.value
                    _collectionItems.value = currentSocial.filter { it.isBookmarked }.map { p ->
                        ContentFeedItemDto(
                            id = p.id,
                            mediaUrl = p.contentUrl,
                            thumbnailUrl = p.contentUrl,
                            platform = p.platform,
                            creatorNameFlat = p.creatorName,
                            brandNameFlat = p.brandName,
                            isBookmarked = true
                        )
                    }
                }
                else -> {}
            }
            _isCollectionLoading.value = false
        }
    }

    private val _reportReasons = MutableStateFlow<List<ReportReasonItemDto>>(emptyList())
    val reportReasons: StateFlow<List<ReportReasonItemDto>> = _reportReasons.asStateFlow()

    fun fetchReportReasons() {
        viewModelScope.launch {
            when (val res = remoteDataSource.getContentReportReasons()) {
                is NetworkResult.Success -> {
                    _reportReasons.value = res.data.data ?: emptyList()
                }
                else -> {}
            }
        }
    }

    // ── Toggle Actions ───────────────────────────────────────────────────────
    fun toggleBookmark(contentId: String) {
        _liveSocialPosts.update { currentList ->
            currentList.map { post ->
                if (post.id == contentId) post.copy(isBookmarked = !post.isBookmarked) else post
            }
        }
        viewModelScope.launch {
            remoteDataSource.toggleBookmarkContent(contentId)
        }
    }

    fun reportContent(contentId: Any, reason: String, description: String?, onResult: (Boolean) -> Unit = {}) {
        viewModelScope.launch {
            when (remoteDataSource.reportContent(contentId, reason, description)) {
                is NetworkResult.Success -> onResult(true)
                else -> onResult(false)
            }
        }
    }

    fun toggleFavorite(campaignId: String) {
        viewModelScope.launch {
            remoteDataSource.toggleFavoriteCampaign(campaignId)
        }
    }

    fun hideCampaign(campaignId: Any, onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            val idStr = campaignId.toString()
            when (val res = remoteDataSource.hideCampaign(idStr)) {
                is NetworkResult.Success -> {
                    _liveCampaigns.update { list -> list.filter { it.id != idStr } }
                    _campaignDiscovery.update { list -> list.filter { it.id?.toString() != idStr } }
                    onComplete?.invoke(true, res.data.message ?: "Campaign hidden")
                }
                is NetworkResult.Error -> {
                    onComplete?.invoke(false, res.message ?: "Failed to hide campaign")
                }
                else -> {}
            }
        }
    }

    fun unhideCampaign(campaignId: Any, onComplete: ((Boolean, String) -> Unit)? = null) {
        viewModelScope.launch {
            val idStr = campaignId.toString()
            when (val res = remoteDataSource.unhideCampaign(idStr)) {
                is NetworkResult.Success -> {
                    onComplete?.invoke(true, res.data.message ?: "Campaign unhidden")
                }
                is NetworkResult.Error -> {
                    onComplete?.invoke(false, res.message ?: "Failed to unhide campaign")
                }
                else -> {}
            }
        }
    }

    fun reportCampaign(
        campaignId: Any,
        reason: String,
        description: String? = null,
        onComplete: ((Boolean, String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            when (val res = remoteDataSource.reportCampaign(campaignId.toString(), reason, description)) {
                is NetworkResult.Success -> {
                    onComplete?.invoke(true, res.data.message ?: "Campaign reported successfully.")
                }
                is NetworkResult.Error -> {
                    onComplete?.invoke(false, res.message ?: "Failed to report campaign.")
                }
                else -> {}
            }
        }
    }

    fun toggleBrandFollow(brandId: Any) {
        val idStr = brandId.toString()
        _brandDetail.update { current ->
            if (current != null && (current.id?.toString() == idStr || current.displayName.equals(idStr, ignoreCase = true))) {
                val nextState = !(current.isFollowing ?: false)
                current.copy(
                    isFollowing = nextState,
                    followersCount = ((current.followersCount ?: 0) + (if (nextState) 1 else -1)).coerceAtLeast(0)
                )
            } else current
        }
        _myBrands.update { list ->
            list.map { brand ->
                if (brand.id?.toString() == idStr || brand.displayName.equals(idStr, ignoreCase = true)) {
                    val nextState = !(brand.isFollowing ?: false)
                    brand.copy(
                        isFollowing = nextState,
                        followersCount = ((brand.followersCount ?: 0) + (if (nextState) 1 else -1)).coerceAtLeast(0)
                    )
                } else brand
            }
        }
        viewModelScope.launch {
            remoteDataSource.toggleFollowBrand(brandId)
            fetchHomeData()
            fetchMyBrands()
        }
    }

    fun fetchBrandDetail(brandId: String, brandName: String = "") {
        val id = brandId.trim()
        viewModelScope.launch {
            _isBrandDetailLoading.value = true
            
            // Check local caches first for immediate UI display
            val cached = _myBrands.value.find { it.id?.toString() == id || (brandName.isNotBlank() && it.displayName.equals(brandName, ignoreCase = true)) }
                ?: _homeData.value?.exploreBrands?.find { it.id?.toString() == id || (brandName.isNotBlank() && it.displayName.equals(brandName, ignoreCase = true)) }
            
            if (cached != null) {
                _brandDetail.value = cached
            } else if (brandName.isNotBlank()) {
                _brandDetail.value = BrandItemDto(
                    id = id.ifBlank { "1" },
                    companyName = brandName,
                    name = brandName,
                    isFollowing = false,
                    followersCount = 0,
                    activeCampaignsCount = 0,
                    rating = 0.0,
                    ratingsCount = 0
                )
            }

            if (id.isNotBlank()) {
                when (val result = remoteDataSource.getBrandByIdOrSlug(id)) {
                    is NetworkResult.Success -> {
                        result.data.data?.let {
                            _brandDetail.value = it
                        }
                    }
                    else -> {}
                }
            }

            val queryName = _brandDetail.value?.displayName ?: brandName
            when (val campRes = remoteDataSource.getCampaigns(search = if (queryName.isNotBlank()) queryName else null)) {
                is NetworkResult.Success -> {
                    val all: List<CampaignDetailDto> = campRes.data.effectiveItems
                    val filtered = if (queryName.isNotBlank()) {
                        all.filter { it.effectiveBrandName.contains(queryName, ignoreCase = true) }
                    } else all
                    _brandActiveCampaigns.value = filtered.filter { it.status?.lowercase() != "completed" && it.status?.lowercase() != "past" }
                    _brandPastCampaigns.value = filtered.filter { it.status?.lowercase() == "completed" || it.status?.lowercase() == "past" }
                }
                else -> {
                    _brandActiveCampaigns.value = emptyList<CampaignDetailDto>()
                    _brandPastCampaigns.value = emptyList<CampaignDetailDto>()
                }
            }

            _isBrandDetailLoading.value = false
        }
    }

    fun fetchBrandRatings(brandId: Any) {
        val id = brandId.toString().trim()
        viewModelScope.launch {
            _isBrandRatingsLoading.value = true
            if (id.isNotBlank()) {
                when (val result = remoteDataSource.getBrandPublicRatings(id)) {
                    is NetworkResult.Success -> {
                        val items = result.data.effectiveItems
                        _brandRatings.value = items
                        _brandRatingsSummary.value = result.data.data?.summary ?: BrandRatingsSummaryDto(
                            averageRating = result.data.averageRating,
                            totalRatings = result.data.totalRatings ?: items.size
                        )
                    }
                    is NetworkResult.Error -> {
                        _brandRatings.value = emptyList()
                        _brandRatingsSummary.value = null
                    }
                    else -> {}
                }
            } else {
                _brandRatings.value = emptyList()
                _brandRatingsSummary.value = null
            }
            _isBrandRatingsLoading.value = false
        }
    }

    fun fetchCreatorRatings(creatorId: Any) {
        val id = creatorId.toString().trim()
        viewModelScope.launch {
            _isBrandRatingsLoading.value = true
            if (id.isNotBlank()) {
                when (val result = remoteDataSource.getCreatorPublicRatings(id)) {
                    is NetworkResult.Success -> {
                        val items = result.data.effectiveItems
                        _brandRatings.value = items
                        _brandRatingsSummary.value = result.data.data?.summary ?: BrandRatingsSummaryDto(
                            averageRating = result.data.averageRating,
                            totalRatings = result.data.totalRatings ?: items.size
                        )
                    }
                    is NetworkResult.Error -> {
                        _brandRatings.value = emptyList()
                        _brandRatingsSummary.value = null
                    }
                    else -> {}
                }
            } else {
                _brandRatings.value = emptyList()
                _brandRatingsSummary.value = null
            }
            _isBrandRatingsLoading.value = false
        }
    }

    // ── Live Server Data Flows (100% Dynamic, no mock fallbacks) ─────────────
    val campaigns: StateFlow<List<Campaign>> = _liveCampaigns
        .combine(_selectedCategory) { campaigns, category ->
            if (category == "All") campaigns
            else campaigns.filter { it.category.equals(category, ignoreCase = true) }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val submissions: StateFlow<List<Submission>> = repository.getSubmissions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val tutorials: StateFlow<List<Tutorial>> = _liveTutorials
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val socialPosts: StateFlow<List<Post>> = _liveSocialPosts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val earnings: StateFlow<Earnings?> = repository.getEarnings()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun selectCategory(category: String) {
        if (_selectedCategory.value == category) return
        viewModelScope.launch {
            _selectedCategory.value = category
            fetchCampaigns(category = if (category == "All") null else category)
        }
    }

    fun triggerLoading() {
        viewModelScope.launch {
            _isLoading.value = true
            kotlinx.coroutines.delay(800)
            _isLoading.value = false
        }
    }

    fun applyToCampaign(
        campaignId: Any,
        pitch: String? = null,
        requestedCompensation: Double? = null,
        deliverablesProposal: String? = null,
        onResult: ((Boolean, String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            when (val result = remoteDataSource.applyToCampaign(campaignId, pitch, requestedCompensation, deliverablesProposal)) {
                is NetworkResult.Success -> {
                    onResult?.invoke(true, result.data?.message ?: "Application submitted successfully!")
                }
                is NetworkResult.Error -> {
                    onResult?.invoke(false, result.message)
                }
                else -> {}
            }
        }
    }

    fun withdrawApplication(applicationId: Any, onResult: ((Boolean, String?) -> Unit)? = null) {
        viewModelScope.launch {
            when (val result = remoteDataSource.withdrawApplication(applicationId)) {
                is NetworkResult.Success -> {
                    onResult?.invoke(true, result.data?.message ?: "Application withdrawn successfully")
                    fetchYourCampaigns(1)
                }
                is NetworkResult.Error -> {
                    onResult?.invoke(false, result.message)
                }
                else -> {}
            }
        }
    }

    fun respondToInvitation(invitationId: Any, accept: Boolean, onResult: ((Boolean, String?) -> Unit)? = null) {
        viewModelScope.launch {
            val result = if (accept) {
                remoteDataSource.acceptMyInvitation(invitationId)
            } else {
                remoteDataSource.declineMyInvitation(invitationId)
            }
            when (result) {
                is NetworkResult.Success -> {
                    onResult?.invoke(true, result.data?.message ?: if (accept) "Invitation accepted" else "Invitation declined")
                    fetchYourCampaigns(0)
                }
                is NetworkResult.Error -> {
                    onResult?.invoke(false, result.message)
                }
                else -> {}
            }
        }
    }

    fun submitRating(
        campaignId: Any,
        rating: Int,
        review: String? = null,
        creatorId: Any? = null,
        brandId: Any? = null,
        onResult: ((Boolean, String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            when (val result = remoteDataSource.submitRating(campaignId, rating, review, creatorId, brandId)) {
                is NetworkResult.Success -> {
                    onResult?.invoke(true, result.data?.message ?: "Rating submitted successfully!")
                }
                is NetworkResult.Error -> {
                    onResult?.invoke(false, result.message)
                }
                else -> {}
            }
        }
    }

    fun withdrawPayout(
        amount: Double,
        iban: String,
        onResult: ((Boolean, String?) -> Unit)? = null
    ) {
        viewModelScope.launch {
            when (val result = remoteDataSource.withdrawPayout(amount, iban)) {
                is NetworkResult.Success -> {
                    onResult?.invoke(true, result.data?.message ?: "Payout requested successfully!")
                }
                is NetworkResult.Error -> {
                    onResult?.invoke(false, result.message)
                }
                else -> {}
            }
        }
    }
}
