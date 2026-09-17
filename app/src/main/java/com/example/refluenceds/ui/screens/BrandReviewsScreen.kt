package com.example.refluenceds.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.ui.components.AppPullToRefreshBox
import com.example.refluenceds.data.remote.dto.RatingItemDto
import com.example.refluenceds.ui.theme.AppTheme
import com.example.refluenceds.ui.viewmodel.CampaignViewModel
import com.example.refluenceds.utils.SetStatusBarAppearance

private val StarGradientColors = listOf(
    Color(0xFF5B61F4),
    Color(0xFF7C3AED),
    Color(0xFF9333EA),
    Color(0xFFC026D3),
    Color(0xFFE11D48)
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BrandReviewsScreen(
    brandId: String,
    brandName: String = "",
    isCreator: Boolean = false,
    viewModel: CampaignViewModel,
    onBack: () -> Unit,
    onNavigateToCampaignDetail: (String) -> Unit = {}
) {
    SetStatusBarAppearance(isLightStatusBars = !AppTheme.isDark)

    LaunchedEffect(brandId, isCreator) {
        if (isCreator) {
            viewModel.fetchCreatorRatings(brandId)
        } else {
            viewModel.fetchBrandRatings(brandId)
        }
    }

    val liveReviews by viewModel.brandRatings.collectAsState()
    val ratingsSummary by viewModel.brandRatingsSummary.collectAsState()
    val isLoading by viewModel.isBrandRatingsLoading.collectAsState()

    val displayName = brandName.ifBlank { if (isCreator) "Creator" else "Brand" }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = AppTheme.colors.background,
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = AppTheme.colors.textPrimary
                    )
                }

                Text(
                    text = "${displayName}'s Reviews",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppTheme.colors.textPrimary,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
    ) { innerPadding ->
        AppPullToRefreshBox(
            isRefreshing = isLoading,
            onRefresh = {
                if (isCreator) {
                    viewModel.fetchCreatorRatings(brandId)
                } else {
                    viewModel.fetchBrandRatings(brandId)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            if (isLoading && liveReviews.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = AppTheme.colors.primary,
                        strokeWidth = 3.dp
                    )
                }
            } else if (liveReviews.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = AppTheme.colors.textSecondary.copy(alpha = 0.35f),
                            modifier = Modifier.size(56.dp)
                        )
                        Spacer(Modifier.height(14.dp))
                        Text(
                            text = "No reviews yet",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.colors.textPrimary
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            text = "There are no reviews for $displayName yet.",
                            fontSize = 14.sp,
                            color = AppTheme.colors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Summary Header Card if available
                    val totalRatings = ratingsSummary?.totalRatings ?: liveReviews.size
                    val avgRating = ratingsSummary?.averageRating ?: (liveReviews.map { it.rating }.average().takeIf { !it.isNaN() } ?: 5.0)
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
                            border = BorderStroke(1.dp, AppTheme.colors.border.copy(alpha = 0.8f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = "Average Rating",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppTheme.colors.textSecondary
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "${"%.1f".format(avgRating)} / 5.0",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.textPrimary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.End) {
                                    Text(
                                        text = "Total Reviews",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = AppTheme.colors.textSecondary
                                    )
                                    Spacer(Modifier.height(4.dp))
                                    Text(
                                        text = "$totalRatings",
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = AppTheme.colors.primary
                                    )
                                }
                            }
                        }
                    }

                    items(liveReviews, key = { it.id?.toString() ?: it.effectiveReviewerName }) { reviewItem ->
                        BrandReviewCard(
                            review = reviewItem,
                            brandName = displayName,
                            isCreator = isCreator,
                            onCampaignClick = {
                                reviewItem.campaignId?.toString()?.let { campId ->
                                    onNavigateToCampaignDetail(campId)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun BrandReviewCard(
    review: RatingItemDto,
    brandName: String,
    isCreator: Boolean = false,
    onCampaignClick: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.surface),
        border = BorderStroke(1.dp, AppTheme.colors.border.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Top Row: Avatar + Name & Campaign
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!review.effectiveReviewerAvatar.isNullOrBlank()) {
                    AsyncImage(
                        model = review.effectiveReviewerAvatar,
                        contentDescription = review.effectiveReviewerName,
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(AppTheme.colors.surfaceVariant),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = review.effectiveReviewerName,
                            tint = AppTheme.colors.textSecondary,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = review.effectiveReviewerName,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.colors.textPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = review.effectiveCampaignTitle,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF8B5CF6),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { onCampaignClick() }
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 14.dp),
                thickness = 1.dp,
                color = AppTheme.colors.border.copy(alpha = 0.6f)
            )

            // 3 Rating Criteria
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                RatingCriteriaRow(
                    label = if (isCreator) "Adherence to campaign specifications" else "Clear campaign specifications",
                    rating = review.effectiveClearSpecsRating
                )

                RatingCriteriaRow(
                    label = if (isCreator) "Likelihood of working together again" else "Likelihood of working with brand again",
                    rating = review.effectiveLikelihoodAgainRating
                )

                RatingCriteriaRow(
                    label = "Reliability of $brandName",
                    rating = review.effectiveReliabilityRating
                )
            }

            // Optional text review comment
            if (!review.review.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "\"${review.review}\"",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Normal,
                    color = AppTheme.colors.textSecondary,
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun RatingCriteriaRow(
    label: String,
    rating: Int
) {
    val clampedRating = rating.coerceIn(0, 5)
    
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = label,
            fontSize = 13.sp,
            fontWeight = FontWeight.Normal,
            color = AppTheme.colors.textPrimary
        )

        Spacer(modifier = Modifier.height(4.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            for (i in 0 until 5) {
                val isFilled = i < clampedRating
                val starColor = if (isFilled) {
                    StarGradientColors.getOrElse(i) { Color(0xFF7C3AED) }
                } else {
                    if (AppTheme.isDark) Color(0xFF334155) else Color(0xFFE2E8F0)
                }

                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = starColor,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Text(
                text = "$clampedRating/5",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = AppTheme.colors.textPrimary
            )
        }
    }
}
