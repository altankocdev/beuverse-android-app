package com.altankoc.beuverse.feature.auth.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.BeuverseButton
import com.altankoc.beuverse.core.ui.components.BeuverseTextField
import com.altankoc.beuverse.core.ui.theme.DarkNavy
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.ui.theme.Mist
import com.altankoc.beuverse.core.ui.theme.beuverseBackgroundGradient
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.auth.presentation.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val isDark = isSystemInDarkTheme()
    val loginState by viewModel.loginState.collectAsState()
    val primaryColor = MaterialTheme.colorScheme.primary
    val darkNavy = DarkNavy
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    LaunchedEffect(loginState) {
        if (loginState is Resource.Success) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Login.route) { inclusive = true }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(64.dp))

            Image(
                painter = painterResource(
                    id = if (isDark) R.drawable.logo_dark else R.drawable.logo_light
                ),
                contentDescription = "Beuverse Logo",
                modifier = Modifier.size(200.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            brush = Brush.horizontalGradient(
                                colors = if (isDark) {
                                    listOf(darkNavy, Mist, Color.White)
                                } else {
                                    listOf(darkNavy, primaryColor, Color(0xFF78909C))
                                }
                            )
                        )
                    ) {
                        append("beu")
                    }
                    
                    withStyle(
                        style = SpanStyle(
                            color = if (isDark) Color.White.copy(alpha = 0.8f) else darkNavy.copy(alpha = 0.6f)
                        )
                    ) {
                        append("verse")
                    }
                },
                fontFamily = JakartaSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 42.sp,
                letterSpacing = (-1.5).sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = if (isDark) {
                    Color.White.copy(alpha = 0.05f)
                } else {
                    Color.White.copy(alpha = 0.85f)
                },
                border = if (isDark) {
                    androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        Color.White.copy(alpha = 0.1f)
                    )
                } else {
                    androidx.compose.foundation.BorderStroke(
                        0.5.dp,
                        Color.Black.copy(alpha = 0.05f)
                    )
                }
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    BeuverseTextField(
                        value = emailOrUsername,
                        onValueChange = { emailOrUsername = it },
                        label = stringResource(R.string.email_or_username),
                        leadingIcon = Icons.Rounded.Person,
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    BeuverseTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = stringResource(R.string.password),
                        leadingIcon = Icons.Rounded.Lock,
                        isPassword = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text(
                            text = stringResource(R.string.forgot_password),
                            fontFamily = JakartaSansFontFamily,
                            fontSize = 13.sp,
                            color = if (isDark) primaryColor else darkNavy.copy(alpha = 0.7f)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    BeuverseButton(
                        text = stringResource(R.string.login),
                        onClick = { viewModel.login(emailOrUsername, password) },
                        enabled = loginState !is Resource.Loading
                    )
                }
            }

            if (loginState is Resource.Error) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(R.string.error_invalid_credentials),
                    fontFamily = JakartaSansFontFamily,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.weight(1f))

            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.no_account),
                    fontFamily = JakartaSansFontFamily,
                    color = if (isDark) Color.White.copy(alpha = 0.5f) else darkNavy.copy(alpha = 0.6f)
                )
                TextButton(onClick = { navController.navigate(Routes.Register.route) }) {
                    Text(
                        text = stringResource(R.string.register),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }
        }

        if (loginState is Resource.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = primaryColor
            )
        }
    }
}