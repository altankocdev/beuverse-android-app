package com.altankoc.beuverse.core.navigation

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.altankoc.beuverse.core.ui.components.BottomNavBar
import com.altankoc.beuverse.core.ui.components.CreatePostScreen
import com.altankoc.beuverse.feature.auth.presentation.LoginScreen
import com.altankoc.beuverse.feature.auth.presentation.RegisterScreen
import com.altankoc.beuverse.feature.home.presentation.HomeScreen
import com.altankoc.beuverse.feature.home.presentation.PostDetailScreen
import com.altankoc.beuverse.feature.profile.presentation.ProfileScreen

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

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                BottomNavBar(
                    currentRoute = currentRoute,
                    onItemClick = { route ->
                        navController.navigate(route) {
                            popUpTo(Routes.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Routes.Login.route,
            modifier = Modifier.padding(paddingValues)
        ) {
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
                // SearchScreen(navController)
            }
            composable(Routes.Messages.route) {
                // MessagesScreen(navController)
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
            ) {
                PostDetailScreen(navController)
            }
            composable(
                route = Routes.UserProfile.route,
                arguments = listOf(navArgument("userId") { type = NavType.StringType })
            ) { backStackEntry ->
                // UserProfileScreen(navController, userId)
            }
        }
    }
}