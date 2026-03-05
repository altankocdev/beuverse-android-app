package com.altankoc.beuverse.feature.profile.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.PostCard
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.feature.home.presentation.PostData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(navController: NavController) {
    var selectedTab by remember { mutableStateOf(0) }

    val tabs = listOf(
        stringResource(R.string.posts_tab),
        stringResource(R.string.comments_tab),
        stringResource(R.string.liked_tab),
    )

    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A2A3A),
            Color(0xFF213448),
            Color(0xFF1E3A5F),
            Color(0xFF222831).copy(alpha = 0.95f)
        )
    )

    val neonAccent = Color(0xFF4FC3F7)

    val userPosts = listOf(
        PostData(
            fullName = "Altan Koç",
            username = "altankoc",
            tag = "Sosyal",
            content = "Bugün harika bir gün geçirdim!",
            timeAgo = "1 saat önce"
        ),
        PostData(
            fullName = "Altan Koç",
            username = "altankoc",
            tag = "Soru",
            content = "Spring Boot için en iyi kaynak nedir?",
            timeAgo = "2 gün önce"
        ),
        PostData(
            fullName = "Altan Koç",
            username = "altankoc",
            tag = "Sosyal",
            content = "AWS sertifikasına çalışıyorum, tavsiye var mı?",
            timeAgo = "5 gün önce"
        ),
    )

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        // Header gradient bölümü
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = headerGradient)
            ) {
                Column {
                    // Ayarlar butonu
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        IconButton(
                            onClick = { },
                            modifier = Modifier.align(Alignment.CenterEnd)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = stringResource(R.string.settings),
                                tint = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }

                    // Avatar + bilgiler
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Avatar - neon border
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .size(76.dp)
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(
                                            neonAccent,
                                            Color(0xFFB39DDB)
                                        )
                                    ),
                                    shape = CircleShape
                                )
                                .padding(3.dp)
                        ) {
                            Surface(
                                modifier = Modifier.fillMaxSize(),
                                shape = CircleShape,
                                color = Color(0xFF1A2A3A)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = "AK",
                                        fontFamily = JakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = neonAccent
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "Altan Koç",
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 19.sp,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = "@altankoc",
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 13.sp,
                                color = neonAccent.copy(alpha = 0.8f)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = stringResource(R.string.joined, "Ocak 2025"),
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Normal,
                                fontSize = 11.sp,
                                color = Color.White.copy(alpha = 0.4f)
                            )
                        }
                    }

                    // Hakkında
                    Text(
                        text = "Bilgisayar Mühendisliği öğrencisi. Android & Backend geliştirici. AWS öğreniyorum. ☁️",
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // İstatistikler
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.White.copy(alpha = 0.06f))
                            .padding(vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        StatItem(count = "12", label = stringResource(R.string.posts_tab), accentColor = neonAccent)
                        VerticalDivider(
                            modifier = Modifier.height(32.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        )
                        StatItem(count = "248", label = stringResource(R.string.liked_tab), accentColor = Color(0xFFFF5252))
                        VerticalDivider(
                            modifier = Modifier.height(32.dp),
                            color = Color.White.copy(alpha = 0.1f)
                        )
                        StatItem(count = "36", label = stringResource(R.string.comments_tab), accentColor = Color(0xFFB39DDB))
                    }

                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }

        // Sekmeler
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = neonAccent,
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                fontSize = 13.sp,
                                color = if (selectedTab == index) neonAccent
                                else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                            )
                        }
                    )
                }
            }
        }

        item {
            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f)
            )
        }

        when (selectedTab) {
            0 -> {
                items(userPosts) { post ->
                    PostCard(
                        fullName = post.fullName,
                        username = post.username,
                        timeAgo = post.timeAgo,
                        content = post.content,
                        tag = post.tag,
                        images = post.images,
                        likeCount = (10..300).random(),
                        commentCount = (1..30).random(),
                        isLiked = false,
                        onUserClick = { },
                        onPostClick = {
                            navController.navigate(Routes.PostDetail.createRoute(post.username))
                        },
                        onLikeClick = { },
                        onCommentClick = { }
                    )
                }
            }
            1 -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz yorum yapılmadı.",
                            fontFamily = JakartaSansFontFamily,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            }
            2 -> {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Henüz beğenilen gönderi yok.",
                            fontFamily = JakartaSansFontFamily,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun StatItem(
    count: String,
    label: String,
    accentColor: Color = Color.White
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = count,
            fontFamily = JakartaSansFontFamily,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            color = accentColor
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontFamily = JakartaSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}