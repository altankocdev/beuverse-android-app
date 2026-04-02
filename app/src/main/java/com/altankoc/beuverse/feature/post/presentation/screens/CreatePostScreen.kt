package com.altankoc.beuverse.feature.post.presentation.screens

import android.Manifest
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.ui.components.tagColor
import com.altankoc.beuverse.core.ui.theme.DarkNavy
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.ui.theme.beuverseBackgroundGradient
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.core.utils.createImageUri
import com.altankoc.beuverse.core.utils.getGalleryPermission
import com.altankoc.beuverse.core.utils.rememberCameraPicker
import com.altankoc.beuverse.core.utils.rememberMultipleGalleryPicker
import com.altankoc.beuverse.core.utils.rememberPermissionLauncher
import com.altankoc.beuverse.core.utils.toMultipartBodyList
import com.altankoc.beuverse.feature.post.presentation.viewmodel.PostViewModel
import com.altankoc.beuverse.feature.profile.presentation.viewmodel.ProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(
    navController: NavController,
    postViewModel: PostViewModel = hiltViewModel(),
    profileViewModel: ProfileViewModel = hiltViewModel()
) {
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("") }
    var selectedTagEnum by remember { mutableStateOf("") }
    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }

    val context = LocalContext.current
    val isDark = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val darkNavy = DarkNavy
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    val createPostState by postViewModel.createPostState.collectAsState()
    val profileState by profileViewModel.profileState.collectAsState()

    var showPhotoOptions by remember { mutableStateOf(false) }

    val galleryPicker = rememberMultipleGalleryPicker(maxItems = 4) { uris ->
        val currentSize = selectedImages.size
        val newUris = uris.take(4 - currentSize)
        selectedImages = (selectedImages + newUris).distinct().take(4)
    }

    val cameraPicker = rememberCameraPicker { uri ->
        if (selectedImages.size < 4) {
            selectedImages = selectedImages + uri
        }
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

    val tagMap = mapOf(
        stringResource(R.string.tag_social) to "SOSYAL",
        stringResource(R.string.tag_commercial) to "TICARI",
        stringResource(R.string.tag_question) to "SORU",
        stringResource(R.string.tag_complaint) to "SIKAYET",
        stringResource(R.string.tag_announcement) to "DUYURU",
    )

    val tags = tagMap.keys.toList()

    LaunchedEffect(createPostState) {
        if (createPostState is Resource.Success) {
            navController.popBackStack()
        }
    }

    val isReady = content.isNotBlank() && selectedTagEnum.isNotEmpty()
    val initials = if (profileState is Resource.Success) {
        val student = (profileState as Resource.Success).data
        "${student.firstName.first()}${student.lastName.first()}".uppercase()
    } else {
        "?"
    }

    if (showPhotoOptions) {
        ModalBottomSheet(
            onDismissRequest = { showPhotoOptions = false },
            containerColor = Color(0xFF1E2A38),
            dragHandle = {
                Box(
                    modifier = Modifier
                        .padding(top = 12.dp, bottom = 8.dp)
                        .size(width = 36.dp, height = 4.dp)
                        .background(Color.White.copy(alpha = 0.2f),
                            RoundedCornerShape(2.dp))
                )
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                Text(
                    text = stringResource(R.string.add_photo),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )

                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showPhotoOptions = false
                            galleryPermissionLauncher(getGalleryPermission())
                        }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(primaryColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PhotoLibrary, 
                            contentDescription = null, 
                            tint = primaryColor, 
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = stringResource(R.string.gallery), 
                        fontFamily = JakartaSansFontFamily, 
                        fontWeight = FontWeight.Medium, 
                        fontSize = 15.sp, 
                        color = Color.White
                    )
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.07f))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (selectedImages.size < 4) {
                                showPhotoOptions = false
                                cameraPermissionLauncher(Manifest.permission.CAMERA)
                            } else {
                                Toast.makeText(
                                    context, 
                                    context.getString(R.string.error_max_images), 
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(primaryColor.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.PhotoCamera, 
                            contentDescription = null, 
                            tint = primaryColor, 
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(14.dp))
                    Text(
                        text = stringResource(R.string.camera), 
                        fontFamily = JakartaSansFontFamily, 
                        fontWeight = FontWeight.Medium, 
                        fontSize = 15.sp, 
                        color = Color.White
                    )
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color.Transparent,
                tonalElevation = 0.dp
            ) {
                Row(
                    modifier = Modifier
                        .statusBarsPadding()
                        .padding(horizontal = 8.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = null,
                            tint = if (isDark) Color.White else darkNavy
                        )
                    }

                    Text(
                        text = stringResource(R.string.create_post_title),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = if (isDark) Color.White else darkNavy
                    )

                    Button(
                        onClick = {
                            if (isReady) {
                                val multipartList = selectedImages.toMultipartBodyList(context, "files")
                                postViewModel.createPost(content.trim(), selectedTagEnum, multipartList)
                            }
                        },
                        enabled = isReady && createPostState !is Resource.Loading,
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = primaryColor,
                            disabledContainerColor = if (isDark) {
                                Color.White.copy(alpha = 0.1f)
                            } else {
                                Color.Black.copy(alpha = 0.1f)
                            }
                        ),
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp),
                        modifier = Modifier.height(36.dp)
                    ) {
                        if (createPostState is Resource.Loading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = stringResource(R.string.publish),
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    color = if (isDark) {
                        Color.White.copy(alpha = 0.05f)
                    } else {
                        Color.White.copy(alpha = 0.85f)
                    },
                    border = if (isDark) {
                        androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
                    } else {
                        androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f))
                    }
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .background(primaryColor.copy(alpha = 0.2f), CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = initials,
                                    fontFamily = JakartaSansFontFamily,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = primaryColor
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            if (profileState is Resource.Success) {
                                val student = (profileState as Resource.Success).data
                                Column {
                                    Text(
                                        text = "${student.firstName} ${student.lastName}",
                                        fontFamily = JakartaSansFontFamily,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp,
                                        color = if (isDark) Color.White else darkNavy
                                    )
                                    Text(
                                        text = "@${student.username}",
                                        fontFamily = JakartaSansFontFamily,
                                        fontSize = 11.sp,
                                        color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        Box(modifier = Modifier.defaultMinSize(minHeight = 150.dp)) {
                            if (content.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.post_content_hint),
                                    fontFamily = JakartaSansFontFamily,
                                    fontSize = 16.sp,
                                    color = if (isDark) Color.White.copy(alpha = 0.3f) else Color.Gray
                                )
                            }
                            BasicTextField(
                                value = content,
                                onValueChange = {
                                    if (it.length <= 500) content = it
                                },
                                textStyle = TextStyle(
                                    fontFamily = JakartaSansFontFamily,
                                    fontSize = 16.sp,
                                    color = if (isDark) Color.White else darkNavy,
                                    lineHeight = 24.sp
                                ),
                                cursorBrush = SolidColor(primaryColor),
                                modifier = Modifier.fillMaxWidth()
                            )
                        }

                        if (selectedImages.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(16.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                items(selectedImages) { uri ->
                                    Box(
                                        modifier = Modifier
                                            .size(80.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(Color.White.copy(alpha = 0.1f))
                                    ) {
                                        AsyncImage(
                                            model = uri,
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        IconButton(
                                            onClick = { selectedImages = selectedImages - uri },
                                            modifier = Modifier
                                                .size(24.dp)
                                                .align(Alignment.TopEnd)
                                                .padding(4.dp)
                                                .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Rounded.Close,
                                                contentDescription = null,
                                                tint = Color.White,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "${content.length}/500",
                            fontFamily = JakartaSansFontFamily,
                            fontSize = 11.sp,
                            color = if (content.length > 450) {
                                Color.Red
                            } else if (isDark) {
                                Color.White.copy(alpha = 0.3f)
                            } else {
                                Color.Gray
                            },
                            modifier = Modifier.align(Alignment.End)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.85f)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = stringResource(R.string.select_tag),
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = if (isDark) Color.White.copy(alpha = 0.5f) else darkNavy.copy(alpha = 0.6f)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(tags) { tag ->
                                val tColor = tagColor(tag)
                                val isSelected = selectedTag == tag
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(if (isSelected) tColor.copy(alpha = 0.15f) else Color.Transparent)
                                        .border(
                                            width = 1.dp,
                                            color = if (isSelected) {
                                                tColor
                                            } else if (isDark) {
                                                Color.White.copy(alpha = 0.1f)
                                            } else {
                                                Color.Black.copy(alpha = 0.1f)
                                            },
                                            shape = RoundedCornerShape(12.dp)
                                        )
                                        .clickable {
                                            selectedTag = tag
                                            selectedTagEnum = tagMap[tag] ?: ""
                                        }
                                        .padding(horizontal = 14.dp, vertical = 8.dp)
                                ) {
                                    Text(
                                        text = tag,
                                        fontFamily = JakartaSansFontFamily,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) {
                                            tColor
                                        } else if (isDark) {
                                            Color.White.copy(alpha = 0.6f)
                                        } else {
                                            Color.Gray
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(20.dp))
                        .clickable { 
                            if (selectedImages.size < 4) {
                                showPhotoOptions = true
                            } else {
                                Toast.makeText(
                                    context, 
                                    context.getString(R.string.error_max_images), 
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.85f)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(primaryColor.copy(alpha = 0.1f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.AddPhotoAlternate,
                                contentDescription = null,
                                tint = primaryColor,
                                modifier = Modifier.size(22.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = stringResource(R.string.add_photo),
                                fontFamily = JakartaSansFontFamily,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (isDark) Color.White else darkNavy
                            )
                            Text(
                                text = "${selectedImages.size}/4 ${stringResource(R.string.add_photo)}",
                                fontFamily = JakartaSansFontFamily,
                                fontSize = 11.sp,
                                color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.weight(1f))

                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowRight,
                            contentDescription = null,
                            tint = if (isDark) {
                                Color.White.copy(alpha = 0.2f)
                            } else {
                                Color.Black.copy(alpha = 0.2f)
                            }
                        )
                    }
                }

                if (createPostState is Resource.Error) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = stringResource(R.string.error_unknown),
                        color = Color.Red,
                        fontSize = 13.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}