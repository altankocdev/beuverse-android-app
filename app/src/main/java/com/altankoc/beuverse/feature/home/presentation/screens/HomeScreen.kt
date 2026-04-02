package com.altankoc.beuverse.feature.home.presentation.screens

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.NotificationsNone
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.BeuverseTagChip
import com.altankoc.beuverse.core.ui.components.PostCard
import com.altankoc.beuverse.core.ui.theme.DarkNavy
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.ui.theme.beuverseBackgroundGradient
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.home.presentation.viewmodel.HomeViewModel
import com.altankoc.beuverse.feature.messages.presentation.viewmodel.MessagingViewModel
import com.altankoc.beuverse.feature.notification.presentation.viewmodel.NotificationViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel(),
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    messagingViewModel: MessagingViewModel = hiltViewModel()
) {
    val isDark = isSystemInDarkTheme()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val primaryColor = MaterialTheme.colorScheme.primary
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    val feedState by viewModel.feedState.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val isLastPage by viewModel.isLastPage.collectAsState()
    val selectedTag by viewModel.selectedTag.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val unreadCount by notificationViewModel.unreadCount.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val startConversationState by messagingViewModel.startConversationState.collectAsState()

    var showMessageRequestDialog by remember { mutableStateOf(false) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val scrollToTop by navBackStackEntry?.savedStateHandle
        ?.getStateFlow("scroll_to_top", false)
        ?.collectAsState() ?: remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        notificationViewModel.connectWebSocket()
    }

    LaunchedEffect(scrollToTop) {
        if (scrollToTop) {
            scope.launch {
                listState.animateScrollToItem(0)
                viewModel.loadFeed(refresh = true)
                navBackStackEntry?.savedStateHandle?.set("scroll_to_top", false)
            }
        }
    }

    LaunchedEffect(startConversationState) {
        if (startConversationState is Resource.Success) {
            val conversation = (startConversationState as Resource.Success).data
            if (conversation.accepted) {
                navController.navigate(
                    Routes.Chat.createRoute(
                        conversation.id.toString(),
                        conversation.otherStudent.username
                    )
                )
            } else {
                showMessageRequestDialog = true
            }
            messagingViewModel.resetStartConversationState()
        }
    }

    val isScrolled = remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 || listState.firstVisibleItemScrollOffset > 50 }
    }

    val topBarHeight by animateDpAsState(
        targetValue = if (isScrolled.value) 0.dp else 56.dp,
        label = "topBarHeight"
    )

    val searchBarHeight by animateDpAsState(
        targetValue = if (isScrolled.value) 0.dp else 52.dp,
        label = "searchBarHeight"
    )

    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastIndex ->
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastIndex != null && lastIndex >= totalItems - 3 && !isLastPage) {
                    viewModel.loadMore()
                }
            }
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = feedState is Resource.Loading && (feedState as? Resource.Success)?.data.isNullOrEmpty(),
        onRefresh = { viewModel.loadFeed(refresh = true) }
    )

    val tags = listOf(
        stringResource(R.string.tag_all),
        stringResource(R.string.tag_social),
        stringResource(R.string.tag_commercial),
        stringResource(R.string.tag_question),
        stringResource(R.string.tag_complaint),
        stringResource(R.string.tag_announcement),
    )

    val tagEnumMap = mapOf(
        stringResource(R.string.tag_all) to "",
        stringResource(R.string.tag_social) to "SOSYAL",
        stringResource(R.string.tag_commercial) to "TICARI",
        stringResource(R.string.tag_question) to "SORU",
        stringResource(R.string.tag_complaint) to "SIKAYET",
        stringResource(R.string.tag_announcement) to "DUYURU",
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors))
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
                        painter = painterResource(id = if (isDark) R.drawable.logo_dark else R.drawable.logo_light),
                        contentDescription = stringResource(R.string.app_name),
                        modifier = Modifier.height(28.dp)
                    )

                    BadgedBox(
                        badge = {
                            if (unreadCount > 0) {
                                Badge(containerColor = MaterialTheme.colorScheme.error) {
                                    Text(
                                        text = if (unreadCount > 9) "9+" else unreadCount.toString(),
                                        fontFamily = JakartaSansFontFamily,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onError
                                    )
                                }
                            }
                        }
                    ) {
                        IconButton(onClick = {
                            notificationViewModel.markAllAsRead()
                            navController.navigate(Routes.Notifications.route)
                        }) {
                            Icon(
                                imageVector = Icons.Rounded.NotificationsNone,
                                contentDescription = null,
                                tint = if (isDark) Color.White else DarkNavy
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.statusBarsPadding())
            }

            if (searchBarHeight > 0.dp) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { viewModel.onSearchQueryChange(it) },
                    placeholder = {
                        Text(
                            text = stringResource(R.string.search_posts),
                            color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            tint = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray
                        )
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                                Icon(
                                    imageVector = Icons.Rounded.Close,
                                    contentDescription = null,
                                    tint = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray
                                )
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(searchBarHeight)
                        .padding(horizontal = 16.dp),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = primaryColor,
                        unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                        focusedTextColor = if (isDark) Color.White else DarkNavy,
                        unfocusedTextColor = if (isDark) Color.White else DarkNavy,
                        focusedContainerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.5f),
                        unfocusedContainerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.5f),
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
                        isSelected = selectedTag == (tagEnumMap[tag] ?: ""),
                        onClick = { viewModel.selectTag(tagEnumMap[tag] ?: "") }
                    )
                }
            }

            when (feedState) {
                is Resource.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = primaryColor)
                    }
                }
                is Resource.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = stringResource(R.string.error_unknown),
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
                is Resource.Success -> {
                    val posts = (feedState as Resource.Success).data
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 100.dp)
                    ) {
                        items(posts, key = { it.id }) { post ->
                            PostCard(
                                fullName = "${post.student.firstName} ${post.student.lastName}",
                                username = post.student.username,
                                timeAgo = post.createdAt,
                                content = post.content,
                                tag = post.tag,
                                imageUrls = post.imageUrls,
                                likeCount = post.likeCount,
                                commentCount = post.commentCount,
                                isLiked = post.isLiked,
                                currentUserId = currentUserId,
                                postOwnerId = post.student.id,
                                profilePhotoUrl = post.student.profilePhotoUrl,
                                onUserClick = { navController.navigate(Routes.UserProfile.createRoute(post.student.id.toString())) },
                                onPostClick = { navController.navigate(Routes.PostDetail.createRoute(post.id.toString())) },
                                onLikeClick = { viewModel.toggleLike(post.id) },
                                onCommentClick = { navController.navigate(Routes.PostDetail.createRoute(post.id.toString())) },
                                onMessageClick = { messagingViewModel.startConversation(post.student.id) },
                                onDeleteClick = { viewModel.deletePost(post.id) }
                            )
                        }
                        if (isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(
                                        color = primaryColor,
                                        modifier = Modifier.size(24.dp)
                                    )
                                }
                            }
                        }
                    }
                }
                else -> {}
            }
        }

        if (showMessageRequestDialog) {
            AlertDialog(
                onDismissRequest = { showMessageRequestDialog = false },
                containerColor = Color(0xFF1E2A38),
                text = {
                    Text(
                        text = stringResource(R.string.message_request_sent),
                        fontFamily = JakartaSansFontFamily,
                        color = Color.White
                    )
                },
                confirmButton = {
                    TextButton(onClick = { showMessageRequestDialog = false }) {
                        Text(
                            text = stringResource(R.string.ok),
                            fontFamily = JakartaSansFontFamily,
                            color = Color(0xFF4FC3F7)
                        )
                    }
                }
            )
        }

        PullRefreshIndicator(
            refreshing = feedState is Resource.Loading,
            state = pullRefreshState,
            modifier = Modifier.align(Alignment.TopCenter),
            backgroundColor = MaterialTheme.colorScheme.surface,
            contentColor = primaryColor
        )
    }
}