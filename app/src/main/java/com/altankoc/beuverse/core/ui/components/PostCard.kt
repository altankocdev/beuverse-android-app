package com.altankoc.beuverse.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily

fun tagColor(tag: String): Color = when (tag) {
    "Sosyal", "Social"       -> Color(0xFF4FC3F7)
    "Ticari", "Commercial"   -> Color(0xFF69F0AE)
    "Soru", "Question"       -> Color(0xFFB39DDB)
    "Şikayet", "Complaint"   -> Color(0xFFFF5252)
    "Duyuru", "Announcement" -> Color(0xFFFFAB40)
    else                     -> Color(0xFFDDE6ED)
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
    images: List<Int> = emptyList()
) {
    val neonColor = tagColor(tag)

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onPostClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier
                    .size(44.dp)
                    .clickable { onUserClick() },
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = fullName.take(2).uppercase(),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName,
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "@$username · $timeAgo",
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Surface(
                shape = RoundedCornerShape(100.dp),
                color = neonColor.copy(alpha = 0.12f)
            ) {
                Text(
                    text = tag,
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 11.sp,
                    color = neonColor,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        // Content
        if (content.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            Text(
                text = content,
                fontFamily = JakartaSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                lineHeight = 21.sp,
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Görseller
        if (images.isNotEmpty()) {
            Spacer(modifier = Modifier.height(10.dp))
            when (images.size) {
                1 -> {
                    Image(
                        painter = painterResource(id = images[0]),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(220.dp)
                            .clip(RoundedCornerShape(12.dp)),
                        contentScale = ContentScale.Crop
                    )
                }
                2 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        images.forEach { img ->
                            Image(
                                painter = painterResource(id = img),
                                contentDescription = null,
                                modifier = Modifier
                                    .weight(1f)
                                    .height(160.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
                3 -> {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Image(
                            painter = painterResource(id = images[0]),
                            contentDescription = null,
                            modifier = Modifier
                                .weight(1f)
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp)),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            modifier = Modifier.weight(1f),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            images.drop(1).forEach { img ->
                                Image(
                                    painter = painterResource(id = img),
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(98.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onLikeClick() }
            ) {
                Icon(
                    imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                    contentDescription = null,
                    tint = if (isLiked) Color(0xFFFF5252)
                    else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = likeCount.toString(),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }

            Spacer(modifier = Modifier.width(20.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onCommentClick() }
            ) {
                Icon(
                    imageVector = Icons.Rounded.ChatBubbleOutline,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = commentCount.toString(),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 10.dp),
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f)
        )
    }
}