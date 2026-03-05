package com.altankoc.beuverse.feature.auth.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.BeuverseButton
import com.altankoc.beuverse.core.ui.components.BeuverseDropdown
import com.altankoc.beuverse.core.ui.components.BeuverseTextField
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily

@Composable
fun RegisterScreen(navController: NavController) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordAgain by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("") }

    val departments = listOf(
        stringResource(R.string.dept_computer_engineering),
        stringResource(R.string.dept_electrical_engineering),
        stringResource(R.string.dept_mechanical_engineering),
        stringResource(R.string.dept_civil_engineering),
        stringResource(R.string.dept_business),
        stringResource(R.string.dept_economics),
        stringResource(R.string.dept_law),
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(56.dp))

        Text(
            text = stringResource(R.string.app_name),
            fontFamily = JakartaSansFontFamily,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 32.sp,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = stringResource(R.string.register),
            fontFamily = JakartaSansFontFamily,
            fontWeight = FontWeight.Normal,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            BeuverseTextField(
                value = firstName,
                onValueChange = { firstName = it },
                label = stringResource(R.string.first_name),
                leadingIcon = Icons.Rounded.Person,
                modifier = Modifier.weight(1f)
            )
            BeuverseTextField(
                value = lastName,
                onValueChange = { lastName = it },
                label = stringResource(R.string.last_name),
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        BeuverseTextField(
            value = username,
            onValueChange = { username = it },
            label = stringResource(R.string.username),
            leadingIcon = Icons.Rounded.AlternateEmail
        )

        Spacer(modifier = Modifier.height(16.dp))

        BeuverseTextField(
            value = email,
            onValueChange = { email = it },
            label = stringResource(R.string.email),
            leadingIcon = Icons.Rounded.Email
        )

        Spacer(modifier = Modifier.height(16.dp))

        BeuverseDropdown(
            selectedItem = selectedDepartment,
            onItemSelected = { selectedDepartment = it },
            label = stringResource(R.string.department),
            items = departments
        )

        Spacer(modifier = Modifier.height(16.dp))

        BeuverseTextField(
            value = password,
            onValueChange = { password = it },
            label = stringResource(R.string.password),
            leadingIcon = Icons.Rounded.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(16.dp))

        BeuverseTextField(
            value = passwordAgain,
            onValueChange = { passwordAgain = it },
            label = stringResource(R.string.password_again),
            leadingIcon = Icons.Rounded.Lock,
            isPassword = true
        )

        Spacer(modifier = Modifier.height(24.dp))

        BeuverseButton(
            text = stringResource(R.string.register),
            onClick = {
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Register.route) { inclusive = true }
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = stringResource(R.string.already_have_account),
                fontFamily = JakartaSansFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
            )
            TextButton(onClick = {
                navController.navigate(Routes.Login.route) {
                    popUpTo(Routes.Register.route) { inclusive = true }
                }
            }) {
                Text(
                    text = stringResource(R.string.login),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}