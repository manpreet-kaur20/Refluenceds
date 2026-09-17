package com.example.refluenceds.ui.components

import android.content.Intent
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.rounded.IosShare
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import com.example.refluenceds.utils.SetStatusBarAppearance

data class StoryItem(
    val id: String,
    val title: String = "",
    val imageUrl: String? = null,
    val videoUrl: String? = null,
    val imageRes: Int? = null,
    val durationSeconds: Int = 5,
    val isBookmarked: Boolean = false
)

@OptIn(UnstableApi::class)
@Composable
fun StoryViewerDialog(
    stories: List<StoryItem>,
    initialIndex: Int = 0,
    onDismiss: () -> Unit,
    onBookmarkClick: ((StoryItem) -> Unit)? = null
) {
    if (stories.isEmpty()) {
        onDismiss()
        return
    }

    SetStatusBarAppearance(isLightStatusBars = false)

    var currentIndex by remember { mutableStateOf(initialIndex.coerceIn(0, stories.size - 1)) }
    var isPaused by remember { mutableStateOf(false) }
    var isBuffering by remember { mutableStateOf(false) }
    val progress = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    val currentStory = stories[currentIndex]

    val context = androidx.compose.ui.platform.LocalContext.current
    val exoPlayer = remember {
        ExoPlayer.Builder(context).build().apply {
            repeatMode = Player.REPEAT_MODE_OFF
        }
    }

    DisposableEffect(exoPlayer) {
        val listener = object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                isBuffering = (playbackState == Player.STATE_BUFFERING)
                if (playbackState == Player.STATE_ENDED) {
                    if (currentIndex < stories.size - 1) {
                        currentIndex++
                    } else {
                        onDismiss()
                    }
                }
            }
            override fun onPlayerError(error: androidx.media3.common.PlaybackException) {
                isBuffering = false
                android.util.Log.e("StoryViewer", "ExoPlayer Error for URL ${stories[currentIndex].videoUrl}: ${error.message}", error)
                // Optionally skip to next story on error
                if (currentIndex < stories.size - 1) {
                    currentIndex++
                } else {
                    onDismiss()
                }
            }
        }
        exoPlayer.addListener(listener)
        onDispose {
            exoPlayer.removeListener(listener)
        }
    }

    DisposableEffect(currentIndex) {
        val story = stories[currentIndex]
        if (!story.videoUrl.isNullOrBlank()) {
            isBuffering = true
            val mediaItem = MediaItem.fromUri(Uri.parse(story.videoUrl))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = !isPaused
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

    LaunchedEffect(isPaused) {
        exoPlayer.playWhenReady = !isPaused
    }

    LaunchedEffect(currentIndex) {
        progress.snapTo(0f)
    }

    LaunchedEffect(currentIndex, isPaused, isBuffering) {
        if (!isPaused && !isBuffering) {
            val remainingFraction = (1f - progress.value).coerceIn(0f, 1f)
            val targetDuration = (currentStory.durationSeconds * 1000 * remainingFraction).toInt()

            if (targetDuration > 0) {
                progress.animateTo(
                    targetValue = 1f,
                    animationSpec = tween(
                        durationMillis = targetDuration,
                        easing = LinearEasing
                    )
                )
            }

            if (progress.value >= 1f) {
                if (currentIndex < stories.size - 1) {
                    currentIndex++
                } else {
                    onDismiss()
                }
            }
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .pointerInput(currentIndex) {
                    detectTapGestures(
                        onPress = {
                            isPaused = true
                            tryAwaitRelease()
                            isPaused = false
                        },
                        onTap = { offset ->
                            val screenWidth = size.width
                            if (offset.x < screenWidth * 0.35f) {
                                if (currentIndex > 0) {
                                    currentIndex--
                                } else {
                                    scope.launch { progress.snapTo(0f) }
                                }
                            } else {
                                if (currentIndex < stories.size - 1) {
                                    currentIndex++
                                } else {
                                    onDismiss()
                                }
                            }
                        }
                    )
                }
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        if (dragAmount.y > 20) {
                            onDismiss()
                        }
                    }
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Progress Bar Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(start = 16.dp, top = 14.dp, end = 16.dp, bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    stories.forEachIndexed { index, _ ->
                        val itemProgress = when {
                            index < currentIndex -> 1f
                            index == currentIndex -> progress.value
                            else -> 0f
                        }

                        LinearProgressIndicator(
                            progress = { itemProgress },
                            modifier = Modifier
                                .weight(1f)
                                .height(2.5.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.35f)
                        )
                    }
                }
            }

            // Center Rounded Video / Story Content Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 4.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF111111))
            ) {
                // Thumbnail background while buffering
                if (!currentStory.imageUrl.isNullOrBlank()) {
                    AsyncImage(
                        model = currentStory.imageUrl,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                if (!currentStory.videoUrl.isNullOrBlank()) {
                    key(currentStory.id) {
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

                    if (isBuffering) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(36.dp),
                                color = Color.White,
                                strokeWidth = 3.dp
                            )
                        }
                    }
                } else if (currentStory.imageRes != null) {
                    Image(
                        painter = painterResource(id = currentStory.imageRes),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                // Floating Action Buttons at bottom-right (Bookmark & Share)
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    if (onBookmarkClick != null) {
                        var isStoryBookmarked by remember(currentStory.id, currentStory.isBookmarked) {
                            mutableStateOf(currentStory.isBookmarked)
                        }
                        Surface(
                            onClick = {
                                isStoryBookmarked = !isStoryBookmarked
                                onBookmarkClick(currentStory.copy(isBookmarked = isStoryBookmarked))
                            },
                            shape = CircleShape,
                            color = Color.Black.copy(alpha = 0.45f),
                            contentColor = Color.White,
                            modifier = Modifier.size(44.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = if (isStoryBookmarked) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = "Save to Collection",
                                    tint = if (isStoryBookmarked) Color(0xFF6366F1) else Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        }
                    }

                    // Floating Share Button
                    Surface(
                        onClick = {
                            val shareText = buildString {
                                if (currentStory.title.isNotBlank()) append(currentStory.title).append("\n")
                                val url = currentStory.videoUrl ?: currentStory.imageUrl
                                if (!url.isNullOrBlank()) append(url)
                            }
                            if (shareText.isNotBlank()) {
                                val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                    type = "text/plain"
                                    putExtra(Intent.EXTRA_SUBJECT, currentStory.title)
                                    putExtra(Intent.EXTRA_TEXT, shareText)
                                }
                                val chooserIntent = Intent.createChooser(shareIntent, "Share").apply {
                                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                }
                                context.startActivity(chooserIntent)
                            }
                        },
                        shape = CircleShape,
                        color = Color.Black.copy(alpha = 0.45f),
                        contentColor = Color.White,
                        modifier = Modifier.size(44.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.Rounded.IosShare,
                                contentDescription = "Share",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }

                // Bottom Progress Bar Line (added for consistent video progress look)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .height(2.dp)
                        .background(Color.White.copy(alpha = 0.3f))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(progress.value)
                            .fillMaxHeight()
                            .background(Color.White)
                    )
                }
            }

            // Bottom Navigation Margin
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .height(14.dp)
            )
        }
    }
}
