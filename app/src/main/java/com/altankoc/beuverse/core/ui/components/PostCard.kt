package com.altankoc.beuverse.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.SubcomposeAsyncImage
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.utils.toTimeAgo

fun tagColor(tag: String): Color = when (tag.uppercase()) {
    "SOSYAL", "SOCIAL"       -> Color(0xFF2196F3)
    "TICARI", "COMMERCIAL"   -> Color(0xFF059669)
    "SORU", "QUESTION"       -> Color(0xFF7C3AED)
    "SIKAYET", "COMPLAINT"   -> Color(0xFFDC2626)
    "DUYURU", "ANNOUNCEMENT" -> Color(0xFFD97706)
    else                     -> Color(0xFF64748B)
}

@Composable
fun tagLabel(tag: String): String {
    return when (tag.uppercase()) {
        "SOSYAL" -> androidx.compose.ui.res.stringResource(com.altankoc.beuverse.R.string.tag_social)
        "TICARI" -> androidx.compose.ui.res.stringResource(com.altankoc.beuverse.R.string.tag_commercial)
        "SORU" -> androidx.compose.ui.res.stringResource(com.altankoc.beuverse.R.string.tag_question)
        "SIKAYET" -> androidx.compose.ui.res.stringResource(com.altankoc.beuverse.R.string.tag_complaint)
        "DUYURU" -> androidx.compose.ui.res.stringResource(com.altankoc.beuverse.R.string.tag_announcement)
        else -> tag
    }
}

@Composable
fun PostCard(
    fullName: String,
    username: String,
    timeAgo: String,
    content: String,
    tag: String,
    likeCount: Int,
    commentCount: Int,
    onUserClick: () -> Unit,
    onPostClick: () -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLiked: Boolean = false,
    imageUrls: List<String> = emptyList(),
    currentUserId: Long? = null,
    postOwnerId: Long? = null,
    profilePhotoUrl: String? = null,
    onMessageClick: (() -> Unit)? = null,
    onDeleteClick: (() -> Unit)? = null,
    onImageClick: ((Int) -> Unit)? = null
) {
    val isDark = isSystemInDarkTheme()
    val tagColor = tagColor(tag)
    val context = LocalContext.current
    val darkNavy = Color(0xFF1A2A3A)
    var showMenu by remember { mutableStateOf(false) }

    val showMessageButton = onMessageClick != null &&
            (tag.uppercase() == "SORU" || tag.uppercase() == "TICARI") &&
            currentUserId != null && postOwnerId != null &&
            currentUserId != postOwnerId

    val isOwner = currentUserId != null && postOwnerId != null && currentUserId == postOwnerId

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClick() },
        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.85f),
        border = if (isDark) androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
        else androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(tagColor.copy(alpha = 0.2f), tagColor.copy(alpha = 0.4f))
                            ),
                            shape = CircleShape
                        )
                        .clip(CircleShape)
                        .clickable { onUserClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = fullName.take(2).uppercase(),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isDark) tagColor else darkNavy
                    )
                    
                    if (!profilePhotoUrl.isNullOrBlank()) {
                        SubcomposeAsyncImage(
                            model = profilePhotoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = tagColor,
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = fullName,
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = if (isDark) Color.White else darkNavy
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = "@$username • ${timeAgo.toTimeAgo(context)}",
                        fontFamily = JakartaSansFontFamily,
                        fontSize = 12.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray.copy(alpha = 0.7f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(100.dp),
                        color = tagColor.copy(alpha = 0.1f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.15f))
                    ) {
                        Text(
                            text = tagLabel(tag),
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp,
                            color = tagColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    if (isOwner && onDeleteClick != null) {
                        Spacer(modifier = Modifier.width(4.dp))

                        var showDeleteDialog by remember { mutableStateOf(false) }

                        if (showDeleteDialog) {
                            AlertDialog(
                                onDismissRequest = { showDeleteDialog = false },
                                containerColor = if (isDark) darkNavy else Color.White,
                                title = {
                                    Text(
                                        text = "Postu Sil",
                                        fontFamily = JakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isDark) Color.White else darkNavy
                                    )
                                },
                                text = {
                                    Text(
                                        text = "Bu postu silmek istiyor musunuz? Bu işlem geri alınamaz.",
                                        fontFamily = JakartaSansFontFamily,
                                        color = if (isDark) Color.White.copy(alpha = 0.7f) else Color.DarkGray
                                    )
                                },
                                confirmButton = {
                                    TextButton(onClick = {
                                        showDeleteDialog = false
                                        onDeleteClick()
                                    }) {
                                        Text("Sil", color = Color.Red, fontWeight = FontWeight.Bold)
                                    }
                                },
                                dismissButton = {
                                    TextButton(onClick = { showDeleteDialog = false }) {
                                        Text("Vazgeç", color = if (isDark) Color.White else darkNavy)
                                    }
                                }
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = null,
                                    tint = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false },
                                modifier = Modifier.background(if (isDark) darkNavy else Color.White)
                            ) {
                                DropdownMenuItem(
                                    text = {
                                        Text(
                                            text = "Sil",
                                            color = Color.Red,
                                            fontFamily = JakartaSansFontFamily
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        showDeleteDialog = true
                                    },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Rounded.DeleteOutline,
                                            contentDescription = null,
                                            tint = Color.Red
                                        )
                                    }
                                )
                            }
                        }
                    }
                }
            }

            if (content.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = content,
                    fontFamily = JakartaSansFontFamily,
                    fontSize = 15.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.9f) else darkNavy.copy(alpha = 0.9f),
                    lineHeight = 22.sp,
                    maxLines = 5,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            if (imageUrls.isNotEmpty()) {
                Spacer(modifier = Modifier.height(12.dp))
                PostImageGrid(imageUrls = imageUrls, onImageClick = onImageClick)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ActionItem(
                    icon = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    count = likeCount.toString(),
                    color = if (isLiked) Color(0xFFDC2626) else (if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray),
                    onClick = onLikeClick
                )

                Spacer(modifier = Modifier.width(24.dp))

                ActionItem(
                    icon = Icons.Rounded.ChatBubbleOutline,
                    count = commentCount.toString(),
                    color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray,
                    onClick = onCommentClick
                )


                if (showMessageButton) {
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        onClick = { onMessageClick?.invoke() },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(tagColor.copy(alpha = 0.12f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.MailOutline,
                                contentDescription = null,
                                tint = tagColor,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PostImageGrid(imageUrls: List<String>, onImageClick: ((Int) -> Unit)? = null) {
    val modifier = Modifier
        .fillMaxWidth()
        .clip(RoundedCornerShape(16.dp))
    
    when (imageUrls.size) {
        1 -> {
            Box(modifier = modifier.aspectRatio(16 / 9f)) {
                PostImage(url = imageUrls[0], onClick = { onImageClick?.invoke(0) })
            }
        }
        2 -> {
            Row(modifier = modifier.aspectRatio(16 / 9f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    PostImage(url = imageUrls[0], onClick = { onImageClick?.invoke(0) })
                }
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    PostImage(url = imageUrls[1], onClick = { onImageClick?.invoke(1) })
                }
            }
        }
        3 -> {
            Row(modifier = modifier.aspectRatio(16 / 9f), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                    PostImage(url = imageUrls[0], onClick = { onImageClick?.invoke(0) })
                }
                Column(modifier = Modifier.weight(1f).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        PostImage(url = imageUrls[1], onClick = { onImageClick?.invoke(1) })
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxWidth()) {
                        PostImage(url = imageUrls[2], onClick = { onImageClick?.invoke(2) })
                    }
                }
            }
        }
        4 -> {
            Column(modifier = modifier.aspectRatio(16 / 9f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        PostImage(url = imageUrls[0], onClick = { onImageClick?.invoke(0) })
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        PostImage(url = imageUrls[1], onClick = { onImageClick?.invoke(1) })
                    }
                }
                Row(modifier = Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        PostImage(url = imageUrls[2], onClick = { onImageClick?.invoke(2) })
                    }
                    Box(modifier = Modifier.weight(1f).fillMaxHeight()) {
                        PostImage(url = imageUrls[3], onClick = { onImageClick?.invoke(3) })
                    }
                }
            }
        }
    }
}

@Composable
private fun PostImage(url: String, onClick: () -> Unit) {
    SubcomposeAsyncImage(
        model = url,
        contentDescription = null,
        modifier = Modifier.fillMaxSize().clickable { onClick() },
        contentScale = ContentScale.Crop,
        loading = {
            Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
            }
        },
        error = {
            Box(Modifier.fillMaxSize().background(Color.White.copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
                Icon(Icons.Rounded.ErrorOutline, null, tint = Color.White.copy(alpha = 0.3f))
            }
        }
    )
}

@Composable
private fun ActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    count: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clickable { onClick() }
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = count, fontFamily = JakartaSansFontFamily, fontWeight = FontWeight.Medium, fontSize = 13.sp, color = color)
    }
}