package com.altankoc.beuverse.feature.search.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.theme.DarkNavy
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.ui.theme.beuverseBackgroundGradient
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.search.presentation.viewmodel.SearchViewModel

@Composable
fun SearchScreen(
    navController: NavController,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val searchState by viewModel.searchState.collectAsState()
    val searchHistory by viewModel.searchHistory.collectAsState(initial = emptyList())

    val isDark = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val darkNavy = DarkNavy
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            Spacer(modifier = Modifier.statusBarsPadding())

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.onSearchQueryChange(it) },
                placeholder = {
                    Text(
                        text = stringResource(R.string.search_students),
                        fontFamily = JakartaSansFontFamily,
                        color = if (isDark) Color.White.copy(alpha = 0.4f) else darkNavy.copy(alpha = 0.5f)
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = if (isDark) Color.White.copy(alpha = 0.4f) else darkNavy.copy(alpha = 0.5f)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { viewModel.onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Rounded.Close,
                                contentDescription = null,
                                tint = if (isDark) Color.White.copy(alpha = 0.4f) else darkNavy.copy(alpha = 0.5f)
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                shape = RoundedCornerShape(20.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = primaryColor,
                    unfocusedBorderColor = if (isDark) {
                        Color.White.copy(alpha = 0.1f)
                    } else {
                        Color.Black.copy(alpha = 0.1f)
                    },
                    focusedTextColor = if (isDark) Color.White else darkNavy,
                    unfocusedTextColor = if (isDark) Color.White else darkNavy,
                    focusedContainerColor = if (isDark) {
                        Color.White.copy(alpha = 0.05f)
                    } else {
                        Color.White.copy(alpha = 0.5f)
                    },
                    unfocusedContainerColor = if (isDark) {
                        Color.White.copy(alpha = 0.05f)
                    } else {
                        Color.White.copy(alpha = 0.5f)
                    },
                )
            )

            Box(modifier = Modifier.weight(1f)) {
                when {
                    searchQuery.isBlank() -> {
                        if (searchHistory.isEmpty()) {
                            EmptySearchHint(isDark, darkNavy)
                        } else {
                            RecentSearchesList(
                                searchHistory = searchHistory,
                                isDark = isDark,
                                darkNavy = darkNavy,
                                primaryColor = primaryColor,
                                onClearAll = { viewModel.clearHistory() },
                                onRemoveItem = { viewModel.removeFromHistory(it) },
                                onNavigate = {
                                    navController.navigate(Routes.UserProfile.createRoute(it.toString()))
                                }
                            )
                        }
                    }
                    searchState is Resource.Loading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = primaryColor)
                        }
                    }
                    searchState is Resource.Success -> {
                        val students = (searchState as Resource.Success).data
                        if (students.isEmpty()) {
                            NoResultsView(isDark, darkNavy)
                        } else {
                            LazyColumn(
                                modifier = Modifier.fillMaxSize(),
                                contentPadding = PaddingValues(bottom = 100.dp)
                            ) {
                                items(students, key = { it.id }) { student ->
                                    StudentItem(
                                        firstName = student.firstName,
                                        lastName = student.lastName,
                                        username = student.username,
                                        bio = student.bio,
                                        isDark = isDark,
                                        primaryColor = primaryColor,
                                        darkNavy = darkNavy,
                                        onClick = {
                                            viewModel.onStudentClick(student)
                                            navController.navigate(Routes.UserProfile.createRoute(student.id.toString()))
                                        }
                                    )
                                }
                            }
                        }
                    }
                    searchState is Resource.Error -> {
                        ErrorView()
                    }
                }
            }
        }
    }
}

@Composable
private fun StudentItem(
    firstName: String,
    lastName: String,
    username: String,
    bio: String?,
    isDark: Boolean,
    primaryColor: Color,
    darkNavy: Color,
    onClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.85f),
        border = if (isDark) {
            androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
        } else {
            androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f))
        }
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.2f),
                                primaryColor.copy(alpha = 0.4f)
                            )
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "${firstName.first()}${lastName.first()}".uppercase(),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = if (isDark) primaryColor else darkNavy
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$firstName $lastName",
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = if (isDark) Color.White else darkNavy
                )
                Text(
                    text = "@$username",
                    fontFamily = JakartaSansFontFamily,
                    fontSize = 12.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.DarkGray
                )
                if (!bio.isNullOrBlank()) {
                    Text(
                        text = bio,
                        fontFamily = JakartaSansFontFamily,
                        fontSize = 12.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.6f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Icon(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = null,
                tint = if (isDark) Color.White.copy(alpha = 0.2f) else darkNavy.copy(alpha = 0.2f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun RecentSearchesList(
    searchHistory: List<com.altankoc.beuverse.core.datastore.SearchHistoryItem>,
    isDark: Boolean,
    darkNavy: Color,
    primaryColor: Color,
    onClearAll: () -> Unit,
    onRemoveItem: (Long) -> Unit,
    onNavigate: (Long) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.recent_searches),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp,
                    letterSpacing = 1.sp,
                    color = if (isDark) Color.White.copy(alpha = 0.3f) else darkNavy.copy(alpha = 0.5f)
                )
                TextButton(onClick = onClearAll) {
                    Text(
                        text = stringResource(R.string.clear_all),
                        fontFamily = JakartaSansFontFamily,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }
        }
        items(searchHistory, key = { "history_${it.id}" }) { item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(item.id) }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Rounded.History,
                    contentDescription = null,
                    tint = if (isDark) Color.White.copy(alpha = 0.3f) else Color.DarkGray.copy(alpha = 0.5f),
                    modifier = Modifier.size(20.dp)
                )

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${item.firstName} ${item.lastName}",
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = if (isDark) Color.White else darkNavy
                    )
                    Text(
                        text = "@${item.username}",
                        fontFamily = JakartaSansFontFamily,
                        fontSize = 12.sp,
                        color = if (isDark) Color.White.copy(alpha = 0.4f) else Color.Gray
                    )
                }

                IconButton(onClick = { onRemoveItem(item.id) }) {
                    Icon(
                        imageVector = Icons.Rounded.Close,
                        contentDescription = null,
                        tint = if (isDark) Color.White.copy(alpha = 0.2f) else darkNavy.copy(alpha = 0.2f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptySearchHint(isDark: Boolean, darkNavy: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.search_hint),
            fontFamily = JakartaSansFontFamily,
            fontSize = 14.sp,
            color = if (isDark) Color.White.copy(alpha = 0.4f) else darkNavy.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun NoResultsView(isDark: Boolean, darkNavy: Color) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.no_results),
            fontFamily = JakartaSansFontFamily,
            fontSize = 14.sp,
            color = if (isDark) Color.White.copy(alpha = 0.4f) else darkNavy.copy(alpha = 0.5f)
        )
    }
}

@Composable
private fun ErrorView() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(R.string.error_unknown),
            color = Color.Red,
            fontFamily = JakartaSansFontFamily
        )
    }
}