package com.altankoc.beuverse.feature.auth.presentation.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.airbnb.lottie.compose.*
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.di.TokenManagerEntryPoint
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.theme.beuverseBackgroundGradient
import dagger.hilt.android.EntryPointAccessors
import kotlinx.coroutines.flow.first

@Composable
fun SplashScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.splash_anim))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 1,
        speed = 3.0f
    )

    val tokenManager = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            TokenManagerEntryPoint::class.java
        ).tokenManager()
    }

    LaunchedEffect(progress) {
        if (progress == 1f) {
            val userId = tokenManager.userId.first()

            val targetRoute = if (userId != null && userId != -1L) {
                Routes.Home.route
            } else {
                Routes.Login.route
            }

            navController.navigate(targetRoute) {
                popUpTo(Routes.Splash.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors)),
        contentAlignment = Alignment.Center
    ) {
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier.size(800.dp)
        )
    }
}