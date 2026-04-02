package com.altankoc.beuverse.core.navigation

sealed class Routes(val route: String) {

    // Splash Screen
    data object Splash : Routes("splash")

    // Auth
    data object Login : Routes("login")
    data object Register : Routes("register")

    // Bottom Nav
    data object Home : Routes("home")
    data object Search : Routes("search")
    data object Messages : Routes("messages")
    data object Profile : Routes("profile")

    // Post
    data object CreatePost : Routes("create_post")
    data object PostDetail : Routes("post_detail/{postId}") {
        fun createRoute(postId: String) = "post_detail/$postId"
    }

    // Other user profile
    data object UserProfile : Routes("user_profile/{userId}") {
        fun createRoute(userId: String) = "user_profile/$userId"
    }
    // Notifications
    data object Notifications : Routes("notifications")

    data object Chat : Routes("chat/{conversationId}/{username}") {
        fun createRoute(conversationId: String, username: String) = "chat/$conversationId/$username"
    }
}