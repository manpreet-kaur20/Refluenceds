package com.example.refluenceds.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.material.icons.rounded.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.refluenceds.domain.model.Tutorial
import com.example.refluenceds.ui.components.SkeletonItem
import com.example.refluenceds.ui.viewmodel.CampaignViewModel

import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView

import com.example.refluenceds.utils.SetStatusBarAppearance

@OptIn(UnstableApi::class)
@Composable
fun AcademyDetailScreen(
    tutorialId: String,
    viewModel: CampaignViewModel,
    onBack: () -> Unit
) {
    SetStatusBarAppearance(isLightStatusBars = false)

    LaunchedEffect(tutorialId) {
        viewModel.fetchAcademyVideoDetail(tutorialId)
    }

    val tutorials by viewModel.tutorials.collectAsState()
    val selectedVideo by viewModel.selectedAcademyVideo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    
    val tutorial = selectedVideo?.takeIf { it.id == tutorialId }
        ?: tutorials.find { it.id == tutorialId }
        ?: tutorials.firstOrNull()
        ?: Tutorial(
            id = tutorialId,
            title = "Content Synchronisation",
            description = "Learn how to sync your content across platforms.",
            thumbnailUrl = "android.resource://com.example.refluenceds/${com.example.refluenceds.R.drawable.fashion_woman}",
            category = "Onboarding",
            duration = "01:00",
            videoUrl = "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/ForBiggerBlazes.mp4"
        )

    var isBookmarked by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(true) }
    var isBuffering by remember { mutableStateOf(true) }
    var videoProgress by remember { mutableFloatStateOf(0f) }

    val context = androidx.compose.ui.platform.LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_ONE
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = (playbackState == Player.STATE_BUFFERING || playbackState == Player.STATE_IDLE)
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                isBuffering = false
                isPlaying = false
                android.util.Log.e("AcademyDetail", "ExoPlayer Error for URL ${tutorial.videoUrl}: ${error.message}", error)
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    LaunchedEffect(isPlaying, isBuffering) {
        if (isPlaying && !isBuffering) {
            while (true) {
                val duration = exoPlayer.duration
                if (duration > 0) {
                    videoProgress = exoPlayer.currentPosition.toFloat() / duration.toFloat()
                }
                kotlinx.coroutines.delay(16)
            }
        }
    }

    DisposableEffect(tutorial.videoUrl) {
        if (tutorial.videoUrl.isNotBlank()) {
            isBuffering = true
            val mediaItem = MediaItem.fromUri(Uri.parse(tutorial.videoUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = isPlaying
        } else {
            isBuffering = false
        }
        onDispose {
            exoPlayer.stop()
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            exoPlayer.release()
        }
    }

    LaunchedEffect(isPlaying) {
        exoPlayer.playWhenReady = isPlaying
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = Color.Black,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
        ) {
            if (isLoading) {
                AcademyDetailSkeleton(onBack = onBack)
            } else {
                // Background Thumbnail Image (always shown behind player while video loads)
                if (tutorial.thumbnailUrl.isNotBlank()) {
                    AsyncImage(
                        model = tutorial.thumbnailUrl,
                        contentDescription = tutorial.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                if (tutorial.videoUrl.isNotBlank()) {
                    AndroidView(
                        factory = { ctx ->
                            PlayerView(ctx).apply {
                                player = exoPlayer
                                useController = false
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            }
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Top gradient overlay for readability of back button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(140.dp)
                        .align(Alignment.TopCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Black.copy(alpha = 0.7f), Color.Transparent)
                            )
                        )
                )

                // Bottom gradient overlay for title & buttons
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(240.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                            )
                        )
                )

                // Center Play / Buffering Overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { isPlaying = !isPlaying },
                    contentAlignment = Alignment.Center
                ) {
                    if (isBuffering && isPlaying && tutorial.videoUrl.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(24.dp),
                            color = Color.Black.copy(alpha = 0.65f),
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = Color.White,
                                    strokeWidth = 2.5.dp
                                )
                                Text(
                                    text = "Loading video...",
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    } else if (!isPlaying) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.35f),
                            modifier = Modifier.size(64.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.PlayArrow,
                                    contentDescription = "Play",
                                    tint = Color.White,
                                    modifier = Modifier.size(40.dp)
                                )
                            }
                        }
                    }
                }

                // Bottom Bar Row Content: Title (Left) + Floating Action Buttons (Right)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .padding(horizontal = 20.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // Left Column: Category Badge / Circle Icon & Title
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = tutorial.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            lineHeight = 22.sp
                        )

                        // Small circle icon at bottom left
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = Color.White.copy(alpha = 0.2f),
                                modifier = Modifier.size(32.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = Icons.Rounded.Bolt,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Right Column: Vertical Action Buttons (Share & Bookmark)
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Floating Share Button
                        Surface(
                            onClick = {
                                val shareText = "Refluenced Academy: ${tutorial.title}\n${tutorial.videoUrl.ifBlank { tutorial.thumbnailUrl }}"
                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                    type = "text/plain"
                                }
                                val shareIntent = Intent.createChooser(sendIntent, "Share Video").apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(shareIntent)
                            },
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(44.dp),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Rounded.IosShare,
                                    contentDescription = "Share",
                                    tint = Color(0xFF1D1B36),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        // Floating Bookmark Button
                        Surface(
                            onClick = {
                                isBookmarked = !isBookmarked
                                viewModel.toggleBookmark(tutorial.id)
                            },
                            shape = CircleShape,
                            color = Color.White,
                            modifier = Modifier.size(44.dp),
                            shadowElevation = 4.dp
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Bookmark",
                                    tint = if (isBookmarked) Color(0xFF4B4FE4) else Color(0xFF1D1B36),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }
                }

                // Top Bar Back Button
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Bottom Progress Bar Line
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .height(2.dp)
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(videoProgress.coerceIn(0f, 1f))
                            .fillMaxHeight()
                            .background(Color.White)
                    )
                }
            }
        }
    }
}

@Composable
fun AcademyDetailSkeleton(onBack: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        SkeletonItem(modifier = Modifier.fillMaxSize())
        
        // Bottom Content Skeleton
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SkeletonItem(
                modifier = Modifier
                    .width(250.dp)
                    .height(24.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            SkeletonItem(
                modifier = Modifier
                    .width(150.dp)
                    .height(16.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Back Button
        IconButton(
            onClick = onBack,
            modifier = Modifier
                .align(Alignment.TopStart)
                .statusBarsPadding()
                .padding(horizontal = 8.dp, vertical = 8.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                contentDescription = "Back",
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}
