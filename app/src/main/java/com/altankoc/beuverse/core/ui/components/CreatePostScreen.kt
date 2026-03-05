package com.altankoc.beuverse.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.ui.components.tagColor
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreatePostScreen(navController: NavController) {
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("") }

    val tags = listOf(
        stringResource(R.string.tag_social),
        stringResource(R.string.tag_commercial),
        stringResource(R.string.tag_question),
        stringResource(R.string.tag_complaint),
        stringResource(R.string.tag_announcement),
    )

    val headerGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF1A2A3A),
            Color(0xFF213448),
        )
    )

    Scaffold(
        contentWindowInsets = WindowInsets(0),
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush = headerGradient)
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                // Vazgeç
                TextButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterStart)
                ) {
                    Text(
                        text = stringResource(R.string.discard),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }

                // Başlık
                Text(
                    text = stringResource(R.string.create_post_title),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )

                // Paylaş butonu
                val isReady = content.isNotBlank() && selectedTag.isNotEmpty()
                TextButton(
                    onClick = { if (isReady) navController.popBackStack() },
                    modifier = Modifier.align(Alignment.CenterEnd),
                    enabled = isReady
                ) {
                    Text(
                        text = stringResource(R.string.publish),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = if (isReady) Color(0xFF4FC3F7) else Color.White.copy(alpha = 0.3f)
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Avatar + TextField
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                verticalAlignment = Alignment.Top
            ) {
                // Avatar
                Surface(
                    modifier = Modifier.size(44.dp),
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = "AK",
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }

                Spacer(modifier = Modifier.width(12.dp))

                // İçerik alanı
                BasicTextFieldBox(
                    value = content,
                    onValueChange = { content = it },
                    hint = stringResource(R.string.post_content_hint),
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Etiket seçimi
            Text(
                text = stringResource(R.string.select_tag),
                fontFamily = JakartaSansFontFamily,
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(tags) { tag ->
                    val neonColor = tagColor(tag)
                    val isSelected = selectedTag == tag
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(100.dp))
                            .background(
                                if (isSelected) neonColor.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surface
                            )
                            .border(
                                width = 1.5.dp,
                                color = if (isSelected) neonColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(100.dp)
                            )
                            .clickable { selectedTag = tag }
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        Text(
                            text = tag,
                            fontFamily = JakartaSansFontFamily,
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                            fontSize = 13.sp,
                            color = if (isSelected) neonColor else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.07f)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Fotoğraf ekle butonu
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surface)
                    .clickable { }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF4FC3F7).copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = stringResource(R.string.add_photo),
                        tint = Color(0xFF4FC3F7),
                        modifier = Modifier.size(22.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = stringResource(R.string.add_photo),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                )
            }
        }
    }
}

@Composable
private fun BasicTextFieldBox(
    value: String,
    onValueChange: (String) -> Unit,
    hint: String,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier.defaultMinSize(minHeight = 120.dp)) {
        if (value.isEmpty()) {
            Text(
                text = hint,
                fontFamily = JakartaSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
            )
        }
        androidx.compose.foundation.text.BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = androidx.compose.ui.text.TextStyle(
                fontFamily = JakartaSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onBackground,
                lineHeight = 24.sp
            ),
            modifier = Modifier.fillMaxWidth()
        )
    }
}