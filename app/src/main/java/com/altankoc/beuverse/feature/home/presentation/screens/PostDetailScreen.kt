package com.altankoc.beuverse.feature.home.presentation.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.PostImageGrid
import com.altankoc.beuverse.core.ui.components.tagColor
import com.altankoc.beuverse.core.ui.theme.DarkNavy
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.ui.theme.beuverseBackgroundGradient
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.core.utils.toTimeAgo
import com.altankoc.beuverse.feature.post.presentation.viewmodel.PostViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(
    navController: NavController,
    postId: String?,
    viewModel: PostViewModel = hiltViewModel()
) {
    var commentText by remember { mutableStateOf("") }
    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val darkNavy = DarkNavy
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    val postState by viewModel.postState.collectAsState()
    val commentsState by viewModel.commentsState.collectAsState()
    val createCommentState by viewModel.createCommentState.collectAsState()
    val deletePostState by viewModel.deletePostState.collectAsState()
    val isLoadingMore by viewModel.isLoadingMore.collectAsState()
    val currentUserId by viewModel.currentUserId.collectAsState()

    var showMenu by remember { mutableStateOf(false) }
    var selectedImageIndex by remember { mutableIntStateOf(-1) }

    LaunchedEffect(postId) {
        postId?.toLongOrNull()?.let { id ->
            viewModel.loadPost(id)
            viewModel.loadComments(id)
        }
    }

    LaunchedEffect(createCommentState) {
        if (createCommentState is Resource.Success) {
            commentText = ""
        }
    }

    LaunchedEffect(deletePostState) {
        if (deletePostState is Resource.Success) {
            navController.popBackStack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors))
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.05f),
                    tonalElevation = 0.dp
                ) {
                    Row(
                        modifier = Modifier
                            .statusBarsPadding()
                            .padding(horizontal = 8.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.Rounded.ArrowBackIosNew,
                                contentDescription = null,
                                tint = if (isDark) Color.White else darkNavy
                            )
                        }
                        Text(
                            text = stringResource(R.string.comments_title),
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = if (isDark) Color.White else darkNavy
                        )
                    }
                }
            },
            bottomBar = {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    color = if (isDark) darkNavy.copy(alpha = 0.95f) else Color.White.copy(alpha = 0.95f),
                    tonalElevation = 8.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commentText,
                            onValueChange = { commentText = it },
                            placeholder = {
                                Text(
                                    text = stringResource(R.string.write_comment),
                                    fontFamily = JakartaSansFontFamily,
                                    color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray
                                )
                            },
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.8f),
                                    shape = RoundedCornerShape(28.dp)
                                ),
                            shape = RoundedCornerShape(28.dp),
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = primaryColor.copy(alpha = 0.5f),
                                unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                                focusedTextColor = if (isDark) Color.White else darkNavy,
                                unfocusedTextColor = if (isDark) Color.White else darkNavy
                            )
                        )
                        Spacer(modifier = Modifier.width(12.dp))

                        FloatingActionButton(
                            onClick = { postId?.toLongOrNull()?.let { id -> viewModel.createComment(id, commentText) } },
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            containerColor = if (commentText.isNotBlank()) primaryColor else if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                            contentColor = if (isDark) darkNavy else Color.White,
                            elevation = FloatingActionButtonDefaults.elevation(0.dp)
                        ) {
                            if (createCommentState is Resource.Loading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(18.dp),
                                    color = if (isDark) darkNavy else Color.White,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Icon(Icons.Rounded.Send, null, modifier = Modifier.size(20.dp))
                            }
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                item {
                    when (postState) {
                        null, is Resource.Loading -> {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(200.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = primaryColor)
                            }
                        }
                        is Resource.Success -> {
                            val post = (postState as Resource.Success).data
                            val tagColor = tagColor(post.tag)
                            val isOwner = currentUserId != null && currentUserId == post.student.id

                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (isDark) Color.White.copy(alpha = 0.02f) else Color.White.copy(alpha = 0.4f))
                                    .padding(20.dp)
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .weight(1f)
                                            .clickable {
                                                navController.navigate(Routes.UserProfile.createRoute(post.student.id.toString()))
                                            }
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(48.dp)
                                                .background(
                                                    brush = Brush.linearGradient(
                                                        colors = listOf(tagColor.copy(alpha = 0.2f), tagColor.copy(alpha = 0.4f))
                                                    ),
                                                    shape = CircleShape
                                                )
                                                .clip(CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = "${post.student.firstName.first()}${post.student.lastName.first()}".uppercase(),
                                                fontFamily = JakartaSansFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = if (isDark) tagColor else darkNavy
                                            )
                                            if (!post.student.profilePhotoUrl.isNullOrBlank()) {
                                                SubcomposeAsyncImage(
                                                    model = post.student.profilePhotoUrl,
                                                    contentDescription = null,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop,
                                                    loading = {
                                                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                                            CircularProgressIndicator(
                                                                modifier = Modifier.size(20.dp),
                                                                color = tagColor,
                                                                strokeWidth = 2.dp
                                                            )
                                                        }
                                                    }
                                                )
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(12.dp))

                                        Column {
                                            Text(
                                                text = "${post.student.firstName} ${post.student.lastName}",
                                                fontFamily = JakartaSansFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 16.sp,
                                                color = if (isDark) Color.White else darkNavy
                                            )
                                            Text(
                                                text = "@${post.student.username}",
                                                fontFamily = JakartaSansFontFamily,
                                                fontSize = 12.sp,
                                                color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray
                                            )
                                        }
                                    }

                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Surface(
                                            shape = RoundedCornerShape(100.dp),
                                            color = tagColor.copy(alpha = 0.1f),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, tagColor.copy(alpha = 0.15f))
                                        ) {
                                            Text(
                                                text = post.tag,
                                                fontFamily = JakartaSansFontFamily,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 10.sp,
                                                color = tagColor,
                                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                                            )
                                        }

                                        if (isOwner) {
                                            Box {
                                                IconButton(onClick = { showMenu = true }) {
                                                    Icon(
                                                        imageVector = Icons.Rounded.MoreVert,
                                                        contentDescription = null,
                                                        tint = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray
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
                                                            viewModel.deletePost(post.id)
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

                                Spacer(modifier = Modifier.height(16.dp))

                                if (post.content.isNotBlank()) {
                                    Text(
                                        text = post.content,
                                        fontFamily = JakartaSansFontFamily,
                                        fontSize = 16.sp,
                                        color = if (isDark) Color.White.copy(alpha = 0.95f) else darkNavy,
                                        lineHeight = 24.sp
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                }

                                if (post.imageUrls.isNotEmpty()) {
                                    PostImageGrid(
                                        imageUrls = post.imageUrls,
                                        onImageClick = { index -> selectedImageIndex = index }
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = stringResource(R.string.likes, post.likeCount),
                                        fontFamily = JakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Text(
                                        text = stringResource(R.string.comments, post.commentCount),
                                        fontFamily = JakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp,
                                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.DarkGray
                                    )
                                    Spacer(modifier = Modifier.weight(1f))
                                    Text(
                                        text = post.createdAt.toTimeAgo(context),
                                        fontFamily = JakartaSansFontFamily,
                                        fontSize = 11.sp,
                                        color = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Gray
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))
                                HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.Start
                                ) {
                                    IconButton(onClick = { postId?.toLongOrNull()?.let { viewModel.toggleLike(it) } }) {
                                        Icon(
                                            imageVector = if (post.isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                                            contentDescription = null,
                                            tint = if (post.isLiked) Color(0xFFDC2626) else (if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray)
                                        )
                                    }
                                }
                            }
                        }
                        else -> {}
                    }
                }

                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)) {
                        Text(
                            text = stringResource(R.string.comments_title).uppercase(),
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp,
                            color = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.4f)
                        )
                    }
                }

                if (commentsState is Resource.Success) {
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
                                    color = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Gray
                                )
                            }
                        }
                    } else {
                        items(comments, key = { it.id }) { comment ->
                            CommentItem(
                                fullName = "${comment.student.firstName} ${comment.student.lastName}",
                                username = comment.student.username,
                                content = comment.content,
                                timeAgo = comment.createdAt.toTimeAgo(context),
                                likeCount = comment.likeCount,
                                isLiked = comment.isLiked,
                                isDark = isDark,
                                primaryColor = primaryColor,
                                darkNavy = darkNavy,
                                profilePhotoUrl = comment.student.profilePhotoUrl,
                                onUserClick = { navController.navigate(Routes.UserProfile.createRoute(comment.student.id.toString())) },
                                onLikeClick = { viewModel.toggleCommentLike(comment.id) }
                            )
                        }
                    }
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
                                modifier = Modifier.size(24.dp),
                                color = primaryColor
                            )
                        }
                    }
                }
            }
        }

        val successPost = (postState as? Resource.Success)?.data
        if (selectedImageIndex != -1 && successPost != null) {
            FullscreenImageOverlay(
                imageUrls = successPost.imageUrls,
                initialIndex = selectedImageIndex,
                onDismiss = { selectedImageIndex = -1 }
            )
        }
    }
}

@Composable
fun FullscreenImageOverlay(
    imageUrls: List<String>,
    initialIndex: Int,
    onDismiss: () -> Unit
) {
    val pagerState = rememberPagerState(initialPage = initialIndex) { imageUrls.size }

    AnimatedVisibility(
        visible = true,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.9f))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onDismiss() }
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentPadding = PaddingValues(horizontal = 0.dp),
                    pageSpacing = 0.dp
                ) { page ->
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        AsyncImage(
                            model = imageUrls[page],
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .wrapContentHeight(),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                if (imageUrls.size > 1) {
                    Row(
                        Modifier
                            .height(50.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        repeat(imageUrls.size) { iteration ->
                            val color = if (pagerState.currentPage == iteration) Color.White else Color.White.copy(alpha = 0.3f)
                            Box(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .size(8.dp)
                            )
                        }
                    }
                }
            }

            IconButton(
                onClick = onDismiss,
                modifier = Modifier
                    .statusBarsPadding()
                    .padding(16.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.Black.copy(alpha = 0.3f), CircleShape)
            ) {
                Icon(Icons.Rounded.Close, null, tint = Color.White)
            }
        }
    }

    BackHandler { onDismiss() }
}

@Composable
private fun CommentItem(
    fullName: String,
    username: String,
    content: String,
    timeAgo: String,
    likeCount: Int,
    isLiked: Boolean,
    isDark: Boolean,
    primaryColor: Color,
    darkNavy: Color,
    profilePhotoUrl: String? = null,
    onUserClick: () -> Unit,
    onLikeClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
    ) {
        Row {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(primaryColor.copy(alpha = 0.1f), CircleShape)
                    .clip(CircleShape)
                    .clickable { onUserClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = fullName.take(2).uppercase(),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = if (isDark) primaryColor else darkNavy
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
                                    modifier = Modifier.size(14.dp),
                                    color = primaryColor,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(
                            text = fullName,
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = if (isDark) Color.White else darkNavy
                        )
                        Text(
                            text = "@$username",
                            fontFamily = JakartaSansFontFamily,
                            fontSize = 11.sp,
                            color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray
                        )
                    }
                    Text(
                        text = timeAgo,
                        fontFamily = JakartaSansFontFamily,
                        fontSize = 10.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Gray
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = content,
                    fontFamily = JakartaSansFontFamily,
                    fontSize = 14.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.85f) else darkNavy.copy(alpha = 0.9f),
                    lineHeight = 20.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { onLikeClick() }
                ) {
                    Icon(
                        imageVector = if (isLiked) Icons.Rounded.Favorite else Icons.Rounded.FavoriteBorder,
                        contentDescription = null,
                        tint = if (isLiked) Color(0xFFDC2626) else (if (isDark) Color.White.copy(alpha = 0.3f) else Color.Gray),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = likeCount.toString(),
                        fontFamily = JakartaSansFontFamily,
                        fontSize = 11.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Gray
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        HorizontalDivider(color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.03f))
    }
}