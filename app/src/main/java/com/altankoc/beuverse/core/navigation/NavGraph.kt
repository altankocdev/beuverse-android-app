package com.altankoc.beuverse.core.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.altankoc.beuverse.core.ui.components.BottomNavBar
import com.altankoc.beuverse.feature.auth.presentation.screens.LoginScreen
import com.altankoc.beuverse.feature.auth.presentation.screens.RegisterScreen
import com.altankoc.beuverse.feature.auth.presentation.screens.SplashScreen
import com.altankoc.beuverse.feature.home.presentation.screens.HomeScreen
import com.altankoc.beuverse.feature.home.presentation.screens.PostDetailScreen
import com.altankoc.beuverse.feature.messages.presentation.screens.ChatScreen
import com.altankoc.beuverse.feature.messages.presentation.screens.ConversationsScreen
import com.altankoc.beuverse.feature.messages.presentation.viewmodel.MessagingViewModel
import com.altankoc.beuverse.feature.notification.presentation.screens.NotificationScreen
import com.altankoc.beuverse.feature.post.presentation.screens.CreatePostScreen
import com.altankoc.beuverse.feature.profile.presentation.screens.ProfileScreen
import com.altankoc.beuverse.feature.profile.presentation.screens.UserProfileScreen
import com.altankoc.beuverse.feature.search.presentation.screens.SearchScreen

val bottomNavRoutes = listOf(
    Routes.Home.route,
    Routes.Search.route,
    Routes.Messages.route,
    Routes.Profile.route
)

@Composable
fun NavGraph(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val messagingViewModel: MessagingViewModel = hiltViewModel()
    val hasUnreadMessages by messagingViewModel.hasUnreadConversation.collectAsState()

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            NavHost(
                navController = navController,
                startDestination = Routes.Splash.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = paddingValues.calculateTopPadding())
            ) {
                composable(Routes.Splash.route) {
                    SplashScreen(navController)
                }

                composable(Routes.Login.route) {
                    LoginScreen(navController)
                }
                composable(Routes.Register.route) {
                    RegisterScreen(navController)
                }
                composable(Routes.Home.route) {
                    HomeScreen(navController)
                }
                composable(Routes.Search.route) {
                    SearchScreen(navController)
                }
                composable(Routes.Messages.route) {
                    ConversationsScreen(
                        navController = navController,
                        viewModel = messagingViewModel
                    )
                }
                composable(Routes.Profile.route) {
                    ProfileScreen(navController)
                }
                composable(Routes.CreatePost.route) {
                    CreatePostScreen(navController)
                }
                composable(
                    route = Routes.PostDetail.route,
                    arguments = listOf(navArgument("postId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val postId = backStackEntry.arguments?.getString("postId")
                    PostDetailScreen(navController = navController, postId = postId)
                }
                composable(
                    route = Routes.UserProfile.route,
                    arguments = listOf(navArgument("userId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val userId = backStackEntry.arguments?.getString("userId")
                    UserProfileScreen(navController = navController, userId = userId)
                }
                composable(Routes.Notifications.route) {
                    NotificationScreen(navController)
                }
                composable(
                    route = Routes.Chat.route,
                    arguments = listOf(
                        navArgument("conversationId") { type = NavType.StringType },
                        navArgument("username") { type = NavType.StringType }
                    )
                ) { backStackEntry ->
                    val conversationId = backStackEntry.arguments?.getString("conversationId")
                    val username = backStackEntry.arguments?.getString("username")
                    ChatScreen(
                        navController = navController,
                        conversationId = conversationId,
                        username = username,
                        viewModel = messagingViewModel
                    )
                }
            }

            if (currentRoute in bottomNavRoutes) {
                Box(modifier = Modifier.align(Alignment.BottomCenter)) {
                    BottomNavBar(
                        currentRoute = currentRoute,
                        hasUnreadMessages = hasUnreadMessages,
                        onItemClick = { route ->
                            if (currentRoute == route) {
                                navController.currentBackStackEntry?.savedStateHandle?.set("scroll_to_top", true)
                            } else {
                                navController.navigate(route) {
                                    popUpTo(Routes.Home.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                            if (route == Routes.Messages.route) {
                                messagingViewModel.clearUnreadBadge()
                            }
                        },
                        onCreatePostClick = {
                            navController.navigate(Routes.CreatePost.route)
                        }
                    )
                }
            }
        }
    }
}