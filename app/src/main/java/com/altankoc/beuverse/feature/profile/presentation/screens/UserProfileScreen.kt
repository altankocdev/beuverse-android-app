package com.altankoc.beuverse.feature.profile.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.PostCard
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.core.utils.toDepartmentName
import com.altankoc.beuverse.core.utils.toJoinedDate
import com.altankoc.beuverse.core.utils.toTimeAgo
import com.altankoc.beuverse.feature.messages.presentation.viewmodel.MessagingViewModel
import com.altankoc.beuverse.feature.profile.presentation.viewmodel.UserProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(
    navController: NavController,
    userId: String?,
    viewModel: UserProfileViewModel = hiltViewModel(),
    messagingViewModel: MessagingViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val postsState by viewModel.postsState.collectAsState()
    val commentsState by viewModel.commentsState.collectAsState()
    val likedPostsState by viewModel.likedPostsState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val totalPosts by viewModel.totalPosts.collectAsState()
    val totalComments by viewModel.totalComments.collectAsState()
    val totalLiked by viewModel.totalLiked.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()
    val startConversationState by messagingViewModel.startConversationState.collectAsState()
    val context = LocalContext.current

    var showFullImage by remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        userId?.toLongOrNull()?.let { id ->
            viewModel.loadProfile(id)
        }
    }

    var showMessageRequestDialog by remember { mutableStateOf(false) }
    val messageRequestSentText = stringResource(R.string.message_request_sent)

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

    if (showMessageRequestDialog) {
        AlertDialog(
            onDismissRequest = { showMessageRequestDialog = false },
            containerColor = Color(0xFF1E2A38),
            text = {
                Text(
                    text = messageRequestSentText,
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
    val student = (profileState as? Resource.Success)?.data
    val initials = remember(student) {
        student?.let {
            val f = it.firstName.firstOrNull()?.toString() ?: ""
            val l = it.lastName.firstOrNull()?.toString() ?: ""
            (f + l).uppercase()
        } ?: ""
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 16.dp)
        ) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(brush = headerGradient)
                ) {
                    Column {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .statusBarsPadding()
                                .padding(horizontal = 4.dp, vertical = 4.dp)
                        ) {
                            IconButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.align(Alignment.CenterStart)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.ArrowBackIosNew,
                                    contentDescription = stringResource(R.string.back),
                                    tint = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }

                        when (profileState) {
                            is Resource.Loading -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = neonAccent)
                                }
                            }
                            is Resource.Success -> {
                                val s = (profileState as Resource.Success).data
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 20.dp, vertical = 8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        contentAlignment = Alignment.Center,
                                        modifier = Modifier
                                            .size(76.dp)
                                            .background(
                                                brush = Brush.linearGradient(
                                                    colors = listOf(neonAccent, Color(0xFFB39DDB))
                                                ),
                                                shape = CircleShape
                                            )
                                            .padding(3.dp)
                                            .clip(CircleShape)
                                            .clickable { showFullImage = true }
                                    ) {
                                        Surface(
                                            modifier = Modifier.fillMaxSize(),
                                            shape = CircleShape,
                                            color = Color(0xFF1A2A3A)
                                        ) {
                                            Box(contentAlignment = Alignment.Center) {
                                                Text(
                                                    text = initials,
                                                    fontFamily = JakartaSansFontFamily,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 24.sp,
                                                    color = neonAccent
                                                )
                                                if (!s.profilePhotoUrl.isNullOrBlank()) {
                                                    SubcomposeAsyncImage(
                                                        model = s.profilePhotoUrl,
                                                        contentDescription = null,
                                                        modifier = Modifier.fillMaxSize(),
                                                        contentScale = ContentScale.Crop,
                                                        loading = {
                                                            Box(
                                                                Modifier.fillMaxSize(),
                                                                contentAlignment = Alignment.Center
                                                            ) {
                                                                CircularProgressIndicator(
                                                                    modifier = Modifier.size(24.dp),
                                                                    color = neonAccent,
                                                                    strokeWidth = 2.dp
                                                                )
                                                            }
                                                        }
                                                    )
                                                }
                                            }
                                        }
                                    }

                                    Spacer(modifier = Modifier.width(16.dp))

                                    Column {
                                        Text(
                                            text = "${s.firstName} ${s.lastName}",
                                            fontFamily = JakartaSansFontFamily,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 19.sp,
                                            color = Color.White
                                        )
                                        Spacer(modifier = Modifier.height(3.dp))
                                        Text(
                                            text = "@${s.username}",
                                            fontFamily = JakartaSansFontFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 13.sp,
                                            color = neonAccent.copy(alpha = 0.8f)
                                        )
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(
                                            text = stringResource(R.string.joined, s.createdAt.toJoinedDate()),
                                            fontFamily = JakartaSansFontFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.4f)
                                        )
                                        Text(
                                            text = s.department.toDepartmentName(context),
                                            fontFamily = JakartaSansFontFamily,
                                            fontWeight = FontWeight.Normal,
                                            fontSize = 11.sp,
                                            color = Color.White.copy(alpha = 0.4f)
                                        )
                                    }
                                }

                                if (!s.bio.isNullOrBlank()) {
                                    Text(
                                        text = s.bio,
                                        fontFamily = JakartaSansFontFamily,
                                        fontWeight = FontWeight.Normal,
                                        fontSize = 13.sp,
                                        color = Color.White.copy(alpha = 0.65f),
                                        lineHeight = 19.sp,
                                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                                    )
                                }
                            }
                            is Resource.Error -> {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(120.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = stringResource(R.string.error_unknown),
                                        color = Color.White
                                    )
                                }
                            }
                            else -> {}
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 20.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White.copy(alpha = 0.06f))
                                .padding(vertical = 14.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            UserStatItem(
                                count = totalPosts.toString(),
                                label = stringResource(R.string.posts_tab),
                                accentColor = neonAccent
                            )
                            VerticalDivider(
                                modifier = Modifier.height(32.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            )
                            UserStatItem(
                                count = totalComments.toString(),
                                label = stringResource(R.string.comments_tab),
                                accentColor = Color(0xFFFFD54F)
                            )
                            VerticalDivider(
                                modifier = Modifier.height(32.dp),
                                color = Color.White.copy(alpha = 0.1f)
                            )
                            UserStatItem(
                                count = totalLiked.toString(),
                                label = stringResource(R.string.liked_tab),
                                accentColor = Color(0xFFFF7043)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }
                }
            }

            item {
                val tabColors = listOf(neonAccent, Color(0xFFFFD54F), Color(0xFFFF7043))

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = tabColors[selectedTab],
                    divider = {},
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = tabColors[selectedTab]
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { viewModel.selectTab(index) },
                            text = {
                                Text(
                                    text = title,
                                    fontFamily = JakartaSansFontFamily,
                                    fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                                    fontSize = 13.sp,
                                    color = if (selectedTab == index) {
                                        tabColors[index]
                                    } else {
                                        MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                    }
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
                    when (postsState) {
                        null, is Resource.Loading -> item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = neonAccent)
                            }
                        }
                        is Resource.Success -> {
                            val posts = (postsState as Resource.Success).data
                            if (posts.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.no_posts_yet),
                                            fontFamily = JakartaSansFontFamily,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            } else {
                                items(posts, key = { "upost_${it.id}" }) { post ->
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
                                        onUserClick = { },
                                        onPostClick = {
                                            navController.navigate(Routes.PostDetail.createRoute(post.id.toString()))
                                        },
                                        onLikeClick = { viewModel.toggleLikeOnPost(post.id) },
                                        onCommentClick = {
                                            navController.navigate(Routes.PostDetail.createRoute(post.id.toString()))
                                        },
                                        onMessageClick = {
                                            messagingViewModel.startConversation(post.student.id)
                                        }
                                    )
                                }
                            }
                        }
                        is Resource.Error -> item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.error_unknown),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
                1 -> {
                    when (commentsState) {
                        null, is Resource.Loading -> item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = neonAccent)
                            }
                        }
                        is Resource.Success -> {
                            val comments = (commentsState as Resource.Success).data
                            if (comments.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.no_comments_yet),
                                            fontFamily = JakartaSansFontFamily,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            } else {
                                items(comments, key = { "ucomment_${it.id}" }) { comment ->
                                    UserCommentItem(
                                        fullName = "${comment.student.firstName} ${comment.student.lastName}",
                                        username = comment.student.username,
                                        content = comment.content,
                                        timeAgo = comment.createdAt.toTimeAgo(context),
                                        postOwnerUsername = comment.postOwnerUsername,
                                        profilePhotoUrl = comment.student.profilePhotoUrl,
                                        onPostClick = {
                                            comment.postId?.let { postId ->
                                                navController.navigate(Routes.PostDetail.createRoute(postId.toString()))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                        is Resource.Error -> item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.error_unknown),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
                2 -> {
                    when (likedPostsState) {
                        null, is Resource.Loading -> item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(32.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = neonAccent)
                            }
                        }
                        is Resource.Success -> {
                            val likedPosts = (likedPostsState as Resource.Success).data
                            if (likedPosts.isEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(48.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = stringResource(R.string.no_liked_posts_yet),
                                            fontFamily = JakartaSansFontFamily,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                                        )
                                    }
                                }
                            } else {
                                items(likedPosts, key = { "uliked_${it.id}" }) { post ->
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
                                        onUserClick = {
                                            navController.navigate(Routes.UserProfile.createRoute(post.student.id.toString()))
                                        },
                                        onPostClick = {
                                            navController.navigate(Routes.PostDetail.createRoute(post.id.toString()))
                                        },
                                        onLikeClick = { viewModel.toggleLikeOnPost(post.id) },
                                        onCommentClick = {
                                            navController.navigate(Routes.PostDetail.createRoute(post.id.toString()))
                                        },
                                        onMessageClick = {
                                            messagingViewModel.startConversation(post.student.id)
                                        }
                                    )
                                }
                            }
                        }
                        is Resource.Error -> item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.error_unknown),
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    }
                }
            }
        }

        FullImageOverlay(
            visible = showFullImage,
            photoUrl = student?.profilePhotoUrl,
            initials = initials,
            neonAccent = neonAccent,
            onDismiss = { showFullImage = false }
        )
    }
}

@Composable
private fun FullImageOverlay(
    visible: Boolean,
    photoUrl: String?,
    initials: String,
    neonAccent: Color,
    onDismiss: () -> Unit
) {
    AnimatedVisibility(visible = visible, enter = fadeIn(), exit = fadeOut()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() },
            contentAlignment = Alignment.Center
        ) {
            AnimatedVisibility(visible = visible, enter = scaleIn(), exit = scaleOut()) {
                Box(
                    modifier = Modifier
                        .size(320.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(neonAccent, Color(0xFFB39DDB))
                            ),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF1A2A3A)),
                    contentAlignment = Alignment.Center
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text(
                            text = initials,
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 80.sp,
                            color = neonAccent,
                            textAlign = TextAlign.Center
                        )
                        if (!photoUrl.isNullOrBlank()) {
                            AsyncImage(
                                model = photoUrl,
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    }
                }
            }
        }
        BackHandler { onDismiss() }
    }
}

@Composable
private fun UserStatItem(
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

@Composable
private fun UserCommentItem(
    fullName: String,
    username: String,
    content: String,
    timeAgo: String,
    postOwnerUsername: String?,
    profilePhotoUrl: String? = null,
    onPostClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPostClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
                modifier = Modifier.size(32.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.secondaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = fullName.take(2).uppercase(),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    if (!profilePhotoUrl.isNullOrBlank()) {
                        SubcomposeAsyncImage(
                            model = profilePhotoUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize().clip(CircleShape),
                            contentScale = ContentScale.Crop,
                            loading = {
                                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(16.dp),
                                        color = Color(0xFF4FC3F7),
                                        strokeWidth = 2.dp
                                    )
                                }
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = fullName,
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "@$username",
                    fontFamily = JakartaSansFontFamily,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                )
            }

            Text(
                text = timeAgo,
                fontFamily = JakartaSansFontFamily,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        if (!postOwnerUsername.isNullOrBlank()) {
            val isTurkish = java.util.Locale.getDefault().language == "tr"
            val annotatedText = androidx.compose.ui.text.buildAnnotatedString {
                withStyle(
                    style = androidx.compose.ui.text.SpanStyle(
                        color = Color(0xFF03A9F4),
                        fontWeight = FontWeight.Medium
                    )
                ) {
                    append("@$postOwnerUsername")
                }
                append(if (isTurkish) "'e yanıt" else " in reply")
            }
            Text(
                text = annotatedText,
                fontFamily = JakartaSansFontFamily,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
        }

        Text(
            text = content,
            fontFamily = JakartaSansFontFamily,
            fontSize = 13.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
            lineHeight = 19.sp
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.05f)
    )
}