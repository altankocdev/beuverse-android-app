package com.altankoc.beuverse.feature.home.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily

data class Comment(
    val fullName: String,
    val username: String,
    val content: String,
    val timeAgo: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(navController: NavController) {
    var commentText by remember { mutableStateOf("") }
    var isLiked by remember { mutableStateOf(false) }
    var likeCount by remember { mutableStateOf(248) }

    val comments = listOf(
        Comment("Ayşe Yılmaz", "ayseyilmaz", "Çok güzel bir paylaşım, teşekkürler!", "2 dk önce"),
        Comment("Kemal Aydın", "kemalaydın", "Katılıyorum, ben de aynı şeyi düşünüyordum.", "5 dk önce"),
        Comment("Selin Kara", "selinkara", "Bu konuda daha fazla bilgi paylaşır mısın?", "12 dk önce"),
        Comment("Tarık Şahin", "tariksahin", "Harika içerik, devam et!", "18 dk önce"),
        Comment("Merve Çelik", "mervecelik", "Teşekkürler, çok işe yaradı.", "25 dk önce"),
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.ArrowBackIosNew,
                            contentDescription = stringResource(R.string.back),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.surface,
                tonalElevation = 4.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                        .padding(horizontal = 16.dp, vertical = 10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = commentText,
                        onValueChange = { commentText = it },
                        placeholder = {
                            Text(
                                text = stringResource(R.string.write_comment),
                                fontFamily = JakartaSansFontFamily,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                            )
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    IconButton(
                        onClick = { commentText = "" },
                        enabled = commentText.isNotEmpty()
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Send,
                            contentDescription = stringResource(R.string.send),
                            tint = if (commentText.isNotEmpty())
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                        )
                    }
                }
            }
        },
        contentWindowInsets = WindowInsets(0)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(46.dp),
                            shape = CircleShape,
                            color = MaterialTheme.colorScheme.secondaryContainer
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Text(
                                    text = "AK",
                                    fontFamily = JakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSecondaryContainer
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Ahmet Koç",
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Text(
                                text = "@ahmetkoc · 14 dk önce",
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Bu bir örnek gönderi içeriğidir. Beuverse'de paylaşılan gönderiler burada görünecek. Backend bağlandığında gerçek veriler gelecek. Daha uzun bir içerik örneği olsun diye bu cümleyi de ekledim.",
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.85f),
                        lineHeight = 23.sp
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = stringResource(R.string.likes, likeCount),
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = stringResource(R.string.comments, comments.size),
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        TextButton(onClick = {
                            isLiked = !isLiked
                            likeCount = if (isLiked) likeCount + 1 else likeCount - 1
                        }) {
                            Icon(
                                imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                contentDescription = null,
                                tint = if (isLiked) Color(0xFFFF5252)
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.like),
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = if (isLiked) Color(0xFFFF5252)
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }

                        TextButton(onClick = { }) {
                            Icon(
                                imageVector = Icons.Rounded.ChatBubbleOutline,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = stringResource(R.string.comment),
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Medium,
                                fontSize = 14.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    }

                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = stringResource(R.string.comments_title),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }

            items(comments) { comment ->
                CommentItem(comment = comment)
            }
        }
    }
}

@Composable
private fun CommentItem(comment: Comment) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Surface(
            modifier = Modifier.size(36.dp),
            shape = CircleShape,
            color = MaterialTheme.colorScheme.secondaryContainer
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = comment.fullName.take(2).uppercase(),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = comment.fullName,
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = comment.timeAgo,
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Normal,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }
            Spacer(modifier = Modifier.height(3.dp))
            Text(
                text = comment.content,
                fontFamily = JakartaSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                lineHeight = 19.sp
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
    )
}