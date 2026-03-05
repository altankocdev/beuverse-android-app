package com.altankoc.beuverse.feature.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Email
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.BeuverseButton
import com.altankoc.beuverse.core.ui.components.BeuverseTextField
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily

@Composable
fun LoginScreen(navController: NavController) {
    var emailOrUsername by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val isDark = isSystemInDarkTheme()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(72.dp))

        // Logo
        Image(
            painter = painterResource(
                id = if (isDark) R.drawable.logo_dark else R.drawable.logo_light
            ),
            contentDescription = "Beuverse Logo",
            modifier = Modifier.size(200.dp)
        )

        Spacer(modifier = Modifier.height(5.dp))

        // Başlık
        Text(
            text = stringResource(R.string.app_name),
            fontFamily = JakartaSansFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 48.sp,
            color = MaterialTheme.colorScheme.onBackground
        )


        Spacer(modifier = Modifier.height(48.dp))

        // Email / Username
        BeuverseTextField(
            value = emailOrUsername,
            onValueChange = { emailOrUsername = it },
            label = stringResource(R.string.email_or_username),
            leadingIcon = Icons.Rounded.Person,
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Şifre
        BeuverseTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password),
            leadingIcon = Icons.Rounded.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Şifremi unuttum
        Box(modifier = Modifier.fillMaxWidth()) {
            TextButton(
                onClick = { },
                modifier = Modifier.align(Alignment.CenterEnd)
            ) {
                Text(
                    text = stringResource(R.string.forgot_password),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Medium,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Giriş Yap butonu
        BeuverseButton(
            text = stringResource(R.string.login),
            onClick = {
                navController.navigate(Routes.Home.route) {
                    popUpTo(Routes.Login.route) { inclusive = true }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Kayıt ol
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.no_account),
                fontFamily = JakartaSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            TextButton(onClick = {
                navController.navigate(Routes.Register.route)
            }) {
                Text(
                    text = stringResource(R.string.register),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}