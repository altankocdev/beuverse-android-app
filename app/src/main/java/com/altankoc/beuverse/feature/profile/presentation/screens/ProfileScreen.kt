package com.altankoc.beuverse.feature.profile.presentation.screens

import android.Manifest
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.BeuverseTextField
import com.altankoc.beuverse.core.ui.components.PostCard
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.core.utils.createImageUri
import com.altankoc.beuverse.core.utils.getGalleryPermission
import com.altankoc.beuverse.core.utils.rememberCameraPicker
import com.altankoc.beuverse.core.utils.rememberGalleryPicker
import com.altankoc.beuverse.core.utils.rememberPermissionLauncher
import com.altankoc.beuverse.core.utils.toDepartmentName
import com.altankoc.beuverse.core.utils.toJoinedDate
import com.altankoc.beuverse.core.utils.toMultipartBody
import com.altankoc.beuverse.core.utils.toTimeAgo
import com.altankoc.beuverse.feature.auth.presentation.viewmodel.AuthViewModel
import com.altankoc.beuverse.feature.profile.domain.model.Student
import com.altankoc.beuverse.feature.profile.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    viewModel: ProfileViewModel = hiltViewModel(),
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val profileState by viewModel.profileState.collectAsState()
    val postsState by viewModel.postsState.collectAsState()
    val commentsState by viewModel.commentsState.collectAsState()
    val likedPostsState by viewModel.likedPostsState.collectAsState()
    val selectedTab by viewModel.selectedTab.collectAsState()
    val updateState by viewModel.updateState.collectAsState()
    val totalPosts by viewModel.totalPosts.collectAsState()
    val totalComments by viewModel.totalComments.collectAsState()
    val totalLiked by viewModel.totalLiked.collectAsState()
    val uploadPhotoState by viewModel.uploadPhotoState.collectAsState()

    var showDeleteAccountDialog by remember { mutableStateOf(false) }
    val deleteAccountState by viewModel.deleteAccountState.collectAsState()

    val context = LocalContext.current
    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val student = (profileState as? Resource.Success)?.data
    val initials = remember(student) {
        student?.let {
            val f = it.firstName.firstOrNull()?.toString() ?: ""
            val l = it.lastName.firstOrNull()?.toString() ?: ""
            (f + l).uppercase()
        } ?: ""
    }

    LaunchedEffect(navBackStackEntry) {
        if (navBackStackEntry?.destination?.route == Routes.Profile.route) {
            viewModel.refresh(showLoading = false)
        }
    }

    LaunchedEffect(deleteAccountState) {
        if (deleteAccountState is Resource.Success) {
            authViewModel.logout()
            navController.navigate(Routes.Login.route) {
                popUpTo(0) { inclusive = true }
            }
        }
    }

    var showSettingsSheet by remember { mutableStateOf(false) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    var showEditBioSheet by remember { mutableStateOf(false) }
    var showPhotoSheet by remember { mutableStateOf(false) }
    var showChangePhotoOptions by remember { mutableStateOf(false) }
    var showFullImage by remember { mutableStateOf(false) }
    var imageUriForCrop by remember { mutableStateOf<Uri?>(null) }

    LaunchedEffect(updateState) {
        if (updateState is Resource.Success) {
            showEditBioSheet = false
        }
    }

    LaunchedEffect(uploadPhotoState) {
        if (uploadPhotoState is Resource.Success) {
            showChangePhotoOptions = false
            showPhotoSheet = false
            imageUriForCrop = null
        }
    }

    val galleryPicker = rememberGalleryPicker { uri ->
        imageUriForCrop = uri
    }

    val cameraPicker = rememberCameraPicker { uri ->
        imageUriForCrop = uri
    }

    val galleryPermissionLauncher = rememberPermissionLauncher(
        onGranted = { galleryPicker() },
        onDenied = {
            Toast.makeText(
                context,
                context.getString(R.string.error_gallery_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    )

    val cameraPermissionLauncher = rememberPermissionLauncher(
        onGranted = {
            val uri = createImageUri(context)
            cameraPicker(uri)
        },
        onDenied = {
            Toast.makeText(
                context,
                context.getString(R.string.error_camera_permission),
                Toast.LENGTH_SHORT
            ).show()
        }
    )

    val tabs = listOf(
        stringResource(R.string.posts_tab),
        stringResource(R.string.comments_tab),
        stringResource(R.string.liked_tab)
    )
    val neonAccent = Color(0xFF4FC3F7)
    val sheetBg = Color(0xFF1E2A38)

    if (showSettingsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showSettingsSheet = false },
            containerColor = sheetBg,
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 36.dp, height = 4.dp)
                        .background(Color.White.copy(alpha = 0.2f), RoundedCornerShape(2.dp))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = stringResource(R.string.settings),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                SettingsItem(
                    icon = Icons.Rounded.Edit,
                    title = stringResource(R.string.edit_profile),
                    color = neonAccent
                ) {
                    showSettingsSheet = false
                    showEditBioSheet = true
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                SettingsItem(
                    icon = Icons.Rounded.Logout,
                    title = stringResource(R.string.logout),
                    color = MaterialTheme.colorScheme.error
                ) {
                    showSettingsSheet = false
                    showLogoutDialog = true
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                SettingsItem(
                    icon = Icons.Rounded.DeleteForever,
                    title = stringResource(R.string.delete_account),
                    color = MaterialTheme.colorScheme.error
                ) {
                    showSettingsSheet = false
                    showDeleteAccountDialog = true
                }
            }
        }
    }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            containerColor = sheetBg,
            title = {
                Text(
                    text = stringResource(R.string.logout),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.logout_confirm),
                    fontFamily = JakartaSansFontFamily,
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showLogoutDialog = false
                        authViewModel.logout()
                        navController.navigate(Routes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.logout),
                        fontFamily = JakartaSansFontFamily,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = JakartaSansFontFamily,
                        color = neonAccent
                    )
                }
            }
        )
    }

    if (showDeleteAccountDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteAccountDialog = false },
            containerColor = sheetBg,
            title = {
                Text(
                    text = stringResource(R.string.delete_account),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.delete_account_confirm),
                    fontFamily = JakartaSansFontFamily,
                    color = Color.White.copy(alpha = 0.7f)
                )
            },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteAccount() },
                    enabled = deleteAccountState !is Resource.Loading
                ) {
                    if (deleteAccountState is Resource.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = MaterialTheme.colorScheme.error,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = stringResource(R.string.delete_account),
                            fontFamily = JakartaSansFontFamily,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteAccountDialog = false }) {
                    Text(
                        text = stringResource(R.string.cancel),
                        fontFamily = JakartaSansFontFamily,
                        color = neonAccent
                    )
                }
            }
        )
    }

    if (showEditBioSheet && student != null) {
        var editBio by remember { mutableStateOf(student.bio ?: "") }
        var editUsername by remember { mutableStateOf(student.username ?: "") }
        ModalBottomSheet(
            onDismissRequest = { showEditBioSheet = false },
            containerColor = sheetBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.edit_profile),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = Color.White
                    )
                    if (updateState is Resource.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = neonAccent,
                            strokeWidth = 2.dp
                        )
                    } else {
                        TextButton(
                            onClick = {
                                viewModel.updateMe(
                                    username = editUsername,
                                    bio = editBio,
                                    profilePhotoUrl = null
                                )
                            }
                        ) {
                            Text(
                                text = stringResource(R.string.save_changes),
                                fontFamily = JakartaSansFontFamily,
                                color = neonAccent,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                BeuverseTextField(
                    value = editUsername,
                    onValueChange = { editUsername = it },
                    label = stringResource(R.string.username)
                )
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = editBio,
                    onValueChange = { if (it.length <= 160) editBio = it },
                    label = {
                        Text(
                            text = stringResource(R.string.edit_bio),
                            color = Color.White.copy(alpha = 0.5f)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = neonAccent,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    )
                )
            }
        }
    }

    if (showPhotoSheet) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoSheet = false },
            containerColor = sheetBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                SettingsItem(
                    icon = Icons.Rounded.Person,
                    title = stringResource(R.string.view_photo),
                    color = neonAccent
                ) {
                    showPhotoSheet = false
                    showFullImage = true
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                SettingsItem(
                    icon = Icons.Rounded.CameraAlt,
                    title = stringResource(R.string.change_photo),
                    color = neonAccent
                ) {
                    showPhotoSheet = false
                    showChangePhotoOptions = true
                }
            }
        }
    }

    if (showChangePhotoOptions) {
        ModalBottomSheet(
            onDismissRequest = { showChangePhotoOptions = false },
            containerColor = sheetBg
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = stringResource(R.string.change_photo),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                SettingsItem(
                    icon = Icons.Rounded.PhotoLibrary,
                    title = stringResource(R.string.gallery),
                    color = neonAccent
                ) {
                    galleryPermissionLauncher(getGalleryPermission())
                }
                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))
                SettingsItem(
                    icon = Icons.Rounded.PhotoCamera,
                    title = stringResource(R.string.camera),
                    color = neonAccent
                ) {
                    cameraPermissionLauncher(Manifest.permission.CAMERA)
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            item {
                ProfileHeader(
                    student = student,
                    initials = initials,
                    profileState = profileState,
                    uploadPhotoState = uploadPhotoState,
                    totalPosts = totalPosts,
                    totalComments = totalComments,
                    totalLiked = totalLiked,
                    onSettingsClick = { showSettingsSheet = true },
                    onPhotoClick = { showPhotoSheet = true },
                    onEditBioClick = { showEditBioSheet = true }
                )
            }

            item {
                ProfileTabs(
                    selectedTab = selectedTab,
                    onTabClick = { viewModel.selectTab(it) },
                    tabs = tabs,
                    neonAccent = neonAccent
                )
            }

            item {
                HorizontalDivider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f))
            }

            renderTabContent(
                selectedTab = selectedTab,
                postsState = postsState,
                commentsState = commentsState,
                likedPostsState = likedPostsState,
                currentUserId = student?.id,
                navController = navController,
                viewModel = viewModel,
                context = context,
                neonAccent = neonAccent
            )
        }

        FullImageOverlay(
            visible = showFullImage,
            photoUrl = student?.profilePhotoUrl,
            initials = initials,
            neonAccent = neonAccent,
            onDismiss = { showFullImage = false }
        )

        if (imageUriForCrop != null) {
            CropScreen(
                uri = imageUriForCrop!!,
                onCropConfirmed = { bitmap ->
                    viewModel.uploadProfilePhoto(bitmap.toMultipartBody(context, "file"))
                    imageUriForCrop = null
                },
                onDismiss = { imageUriForCrop = null }
            )
        }
    }
}

@Composable
private fun SettingsItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(20.dp)
            )
        }
        Spacer(modifier = Modifier.width(14.dp))
        Text(
            text = title,
            fontFamily = JakartaSansFontFamily,
            fontWeight = FontWeight.Medium,
            fontSize = 15.sp,
            color = if (color == MaterialTheme.colorScheme.error) color else Color.White
        )
    }
}

@Composable
private fun ProfileHeader(
    student: Student?,
    initials: String,
    profileState: Resource<Student>,
    uploadPhotoState: Resource<Student>?,
    totalPosts: Long,
    totalComments: Long,
    totalLiked: Long,
    onSettingsClick: () -> Unit,
    onPhotoClick: () -> Unit,
    onEditBioClick: () -> Unit
) {
    val neonAccent = Color(0xFF4FC3F7)
    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A2A3A),
            Color(0xFF213448),
            Color(0xFF1E3A5F),
            Color(0xFF222831).copy(alpha = 0.95f)
        )
    )

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
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                IconButton(
                    onClick = onSettingsClick,
                    modifier = Modifier.align(Alignment.CenterEnd)
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Settings,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            if (profileState is Resource.Loading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = neonAccent)
                }
            } else if (student != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(84.dp)
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(neonAccent, Color(0xFFB39DDB))
                                ),
                                shape = CircleShape
                            )
                            .padding(3.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1A2A3A))
                            .clickable { onPhotoClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = initials,
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 24.sp,
                                color = neonAccent,
                                textAlign = TextAlign.Center
                            )
                            
                            if (!student.profilePhotoUrl.isNullOrBlank()) {
                                SubcomposeAsyncImage(
                                    model = student.profilePhotoUrl,
                                    contentDescription = null,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(CircleShape),
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
                        if (uploadPhotoState is Resource.Loading) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(24.dp),
                                    color = neonAccent,
                                    strokeWidth = 2.dp
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(
                            text = "${student.firstName} ${student.lastName}",
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = Color.White
                        )
                        Text(
                            text = "@${student.username}",
                            fontFamily = JakartaSansFontFamily,
                            fontSize = 13.sp,
                            color = neonAccent.copy(alpha = 0.8f)
                        )
                        Text(
                            text = stringResource(R.string.joined, student.createdAt.toJoinedDate()),
                            fontFamily = JakartaSansFontFamily,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                        Text(
                            text = student.department.toDepartmentName(LocalContext.current),
                            fontFamily = JakartaSansFontFamily,
                            fontSize = 11.sp,
                            color = Color.White.copy(alpha = 0.4f)
                        )
                    }
                }
                if (!student.bio.isNullOrBlank()) {
                    Text(
                        text = student.bio,
                        fontFamily = JakartaSansFontFamily,
                        fontSize = 13.sp,
                        color = Color.White.copy(alpha = 0.65f),
                        lineHeight = 19.sp,
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                    )
                } else {
                    TextButton(
                        onClick = onEditBioClick,
                        modifier = Modifier.padding(horizontal = 12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.about_me),
                            fontFamily = JakartaSansFontFamily,
                            fontSize = 13.sp,
                            color = Color.White.copy(alpha = 0.3f)
                        )
                    }
                }
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
                StatItem(
                    count = totalPosts.toString(),
                    label = stringResource(R.string.posts_tab),
                    accentColor = neonAccent
                )
                VerticalDivider(
                    modifier = Modifier.height(32.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )
                StatItem(
                    count = totalComments.toString(),
                    label = stringResource(R.string.comments_tab),
                    accentColor = Color(0xFFFFD54F)
                )
                VerticalDivider(
                    modifier = Modifier.height(32.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )
                StatItem(
                    count = totalLiked.toString(),
                    label = stringResource(R.string.liked_tab),
                    accentColor = Color(0xFFFF7043)
                )
            }
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ProfileTabs(
    selectedTab: Int,
    onTabClick: (Int) -> Unit,
    tabs: List<String>,
    neonAccent: Color
) {
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
                onClick = { onTabClick(index) },
                text = {
                    Text(
                        text = title,
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = if (selectedTab == index) FontWeight.SemiBold else FontWeight.Normal,
                        fontSize = 13.sp,
                        color = if (selectedTab == index) tabColors[index] else MaterialTheme.colorScheme.onBackground.copy(
                            alpha = 0.4f
                        )
                    )
                }
            )
        }
    }
}

private fun LazyListScope.renderTabContent(
    selectedTab: Int,
    postsState: Resource<List<com.altankoc.beuverse.feature.home.domain.model.Post>>?,
    commentsState: Resource<List<com.altankoc.beuverse.feature.home.domain.model.Comment>>?,
    likedPostsState: Resource<List<com.altankoc.beuverse.feature.home.domain.model.Post>>?,
    currentUserId: Long?,
    navController: NavController,
    viewModel: ProfileViewModel,
    context: android.content.Context,
    neonAccent: Color
) {
    when (selectedTab) {
        0 -> renderPosts(postsState, currentUserId, navController, viewModel, neonAccent)
        1 -> renderComments(commentsState, navController, context, neonAccent)
        2 -> renderLikedPosts(likedPostsState, currentUserId, navController, viewModel, neonAccent)
    }
}

private fun LazyListScope.renderPosts(
    state: Resource<List<com.altankoc.beuverse.feature.home.domain.model.Post>>?,
    currentUserId: Long?,
    navController: NavController,
    viewModel: ProfileViewModel,
    neonAccent: Color
) {
    when (state) {
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
            val items = state.data
            if (items.isEmpty()) item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_posts_yet),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            } else items(items, key = { "post_${it.id}" }) { post ->
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
                    onUserClick = {},
                    onPostClick = {
                        navController.navigate(
                            Routes.PostDetail.createRoute(post.id.toString())
                        )
                    },
                    onLikeClick = { viewModel.toggleLikeOnPost(post.id) },
                    onCommentClick = {
                        navController.navigate(
                            Routes.PostDetail.createRoute(post.id.toString())
                        )
                    },
                    onDeleteClick = { viewModel.deletePost(post.id) }
                )
            }
        }

        else -> {}
    }
}

private fun LazyListScope.renderComments(
    state: Resource<List<com.altankoc.beuverse.feature.home.domain.model.Comment>>?,
    navController: NavController,
    context: android.content.Context,
    neonAccent: Color
) {
    when (state) {
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
            val items = state.data
            if (items.isEmpty()) item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_comments_yet),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            } else items(items, key = { "comment_${it.id}" }) { comment ->
                ProfileCommentItem(
                    fullName = "${comment.student.firstName} ${comment.student.lastName}",
                    username = comment.student.username,
                    content = comment.content,
                    timeAgo = comment.createdAt.toTimeAgo(context),
                    postOwnerUsername = comment.postOwnerUsername,
                    profilePhotoUrl = comment.student.profilePhotoUrl,
                    onPostClick = {
                        comment.postId?.let {
                            navController.navigate(Routes.PostDetail.createRoute(it.toString()))
                        }
                    }
                )
            }
        }

        else -> {}
    }
}

private fun LazyListScope.renderLikedPosts(
    state: Resource<List<com.altankoc.beuverse.feature.home.domain.model.Post>>?,
    currentUserId: Long?,
    navController: NavController,
    viewModel: ProfileViewModel,
    neonAccent: Color
) {
    when (state) {
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
            val items = state.data
            if (items.isEmpty()) item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_liked_posts_yet),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            } else items(items, key = { "liked_${it.id}" }) { post ->
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
                        navController.navigate(
                            Routes.UserProfile.createRoute(post.student.id.toString())
                        )
                    },
                    onPostClick = {
                        navController.navigate(
                            Routes.PostDetail.createRoute(post.id.toString())
                        )
                    },
                    onLikeClick = { viewModel.toggleLikeOnPost(post.id) },
                    onCommentClick = {
                        navController.navigate(
                            Routes.PostDetail.createRoute(post.id.toString())
                        )
                    },
                    onDeleteClick = if (post.student.id == currentUserId) {
                        { viewModel.deletePost(post.id) }
                    } else null
                )
            }
        }

        else -> {}
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
private fun CropScreen(
    uri: Uri,
    onCropConfirmed: (Bitmap) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale *= zoomChange
        offset += offsetChange
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offset.x,
                        translationY = offset.y
                    )
                    .transformable(state = state),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = uri,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }

            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
            ) {
                val circleRadius = size.width * 0.45f
                drawRect(color = Color.Black.copy(alpha = 0.8f))
                drawCircle(
                    color = Color.Transparent,
                    radius = circleRadius,
                    center = center,
                    blendMode = BlendMode.Clear
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(32.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text(
                            text = stringResource(R.string.cancel),
                            color = Color.White,
                            fontFamily = JakartaSansFontFamily
                        )
                    }
                    Button(
                        onClick = {
                            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                ImageDecoder.decodeBitmap(ImageDecoder.createSource(context.contentResolver, uri))
                            } else {
                                MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                            }
                            onCropConfirmed(bitmap)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4FC3F7)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = stringResource(R.string.save_changes),
                            color = Color.White,
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ProfileCommentItem(
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
            val annotatedText = buildAnnotatedString {
                withStyle(
                    style = SpanStyle(color = Color(0xFF03A9F4), fontWeight = FontWeight.Medium)
                ) {
                    append("@$postOwnerUsername")
                }; append(
                if (java.util.Locale.getDefault().language == "tr") "'e yanıt" else " in reply"
            )
            }
            Text(text = annotatedText, fontFamily = JakartaSansFontFamily, fontSize = 11.sp)
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

@Composable
private fun StatItem(count: String, label: String, accentColor: Color = Color.White) {
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
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.5f)
        )
    }
}
