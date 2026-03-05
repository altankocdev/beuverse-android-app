package com.altankoc.beuverse.feature.home.presentation

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.BeuverseTagChip
import com.altankoc.beuverse.core.ui.components.PostCard
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class PostData(
    val fullName: String,
    val username: String,
    val tag: String,
    val content: String,
    val images: List<Int> = emptyList(),
    val timeAgo: String = "14 dk önce"
)

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(navController: NavController) {
    var selectedTag by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var isRefreshing by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()

    val isScrolled = remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 50 }
    }
    val topBarHeight by animateDpAsState(
        targetValue = if (isScrolled.value) 0.dp else 56.dp,
        label = "topBarHeight"
    )
    val searchBarHeight by animateDpAsState(
        targetValue = if (isScrolled.value) 0.dp else 64.dp,
        label = "searchBarHeight"
    )

    val tags = listOf(
        stringResource(R.string.tag_all),
        stringResource(R.string.tag_social),
        stringResource(R.string.tag_commercial),
        stringResource(R.string.tag_question),
        stringResource(R.string.tag_complaint),
        stringResource(R.string.tag_announcement),
    )

    val tagAll = stringResource(R.string.tag_all)
    LaunchedEffect(Unit) { selectedTag = tagAll }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = isRefreshing,
        onRefresh = {
            scope.launch {
                isRefreshing = true
                delay(1500)
                isRefreshing = false
            }
        }
    )

    val posts = remember {  listOf(
        PostData(
            fullName = "Ahmet Koç",
            username = "ahmetkoc",
            tag = "Sosyal",
            content = "Bugün kampüste harika bir etkinlik vardı! Herkese duyuruyorum, gelecek hafta tekrar yapılacak.",
            images = emptyList(),
            timeAgo = "2 dk önce"
        ),
        PostData(
            fullName = "Zeynep Arslan",
            username = "zeyneparslan",
            tag = "Soru",
            content = "Veri tabanı dersi için iyi kaynak önerebilir misiniz?",
            images = emptyList(),
            timeAgo = "8 dk önce"
        ),
        PostData(
            fullName = "Mert Kaya",
            username = "mertkaya",
            tag = "Duyuru",
            content = "Japonya gezisinden kareler 🇯🇵",
            images = listOf(R.drawable.japanese_photo),
            timeAgo = "15 dk önce"
        ),
        PostData(
            fullName = "Ece Demir",
            username = "ecedemir",
            tag = "Şikayet",
            content = "Kütüphane çalışma saatleri çok kısıtlı, uzatılması lazım.",
            images = emptyList(),
            timeAgo = "22 dk önce"
        ),
        PostData(
            fullName = "Burak Tekin",
            username = "buraktekin",
            tag = "Ticari",
            content = "İkinci el ders kitapları satıyorum, iletişime geçin.",
            images = listOf(R.drawable.manzara, R.drawable.chinese_photo),
            timeAgo = "35 dk önce"
        ),
        PostData(
            fullName = "Selin Yıldız",
            username = "selinyildiz",
            tag = "Sosyal",
            content = "Çin sokaklarında bir gün ✨",
            images = listOf(R.drawable.chinese_photo, R.drawable.japanese_photo, R.drawable.manzara),
            timeAgo = "1 saat önce"
        ),
        PostData(
            fullName = "Tarık Şahin",
            username = "tariksahin",
            tag = "Duyuru",
            content = "",
            images = listOf(R.drawable.manzara),
            timeAgo = "2 saat önce"
        ),
        PostData(
            fullName = "Merve Çelik",
            username = "mervecelik",
            tag = "Soru",
            content = "Staj başvuruları için LinkedIn profilini nasıl güçlendirirsiniz? Önerileriniz var mı?",
            images = emptyList(),
            timeAgo = "3 saat önce"
        ),
        PostData(
            fullName = "Kemal Aydın",
            username = "kemalaydın",
            tag = "Ticari",
            content = "Grafik tasarım hizmetleri sunuyorum, uygun fiyat.",
            images = listOf(R.drawable.japanese_photo),
            timeAgo = "5 saat önce"
        ),
    )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            if (topBarHeight > 0.dp) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .statusBarsPadding()
                        .height(topBarHeight)
                        .padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Image(
                        painter = painterResource(
                            id = if (isDark) R.drawable.logo_dark else R.drawable.logo_light
                        ),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.height(28.dp)
                    )
                    IconButton(onClick = { }) {
                        Icon(
                            imageVector = Icons.Rounded.NotificationsNone,
                            contentDescription = stringResource(R.string.notifications),
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.statusBarsPadding())
            }

            if (searchBarHeight > 0.dp) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_posts),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(searchBarHeight)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                        unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                    )
                )
            }

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(tags) { tag ->
                    BeuverseTagChip(
                        label = tag,
                        isSelected = selectedTag == tag,
                        onClick = { selectedTag = tag }
                    )
                }
            }

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(posts) { post ->
                    PostCard(
                        fullName = post.fullName,
                        username = post.username,
                        timeAgo = post.timeAgo,
                        content = post.content,
                        tag = post.tag,
                        images = post.images,
                        likeCount = (10..500).random(),
                        commentCount = (1..50).random(),
                        isLiked = false,
                        onUserClick = {
                            navController.navigate(Routes.UserProfile.createRoute(post.username))
                        },
                        onPostClick = {
                            navController.navigate(Routes.PostDetail.createRoute(post.username))
                        },
                        onLikeClick = { },
                        onCommentClick = { }
                    )
                }
            }
        }

        FloatingActionButton(
            onClick = { navController.navigate(Routes.CreatePost.route) },
            shape = RoundedCornerShape(16.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Edit,
                contentDescription = stringResource(R.string.create_post)
            )
        }

        PullRefreshIndicator(
            refreshing = isRefreshing,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.primary
        )
    }
}