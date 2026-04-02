package com.altankoc.beuverse.feature.auth.presentation.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.altankoc.beuverse.R
import com.altankoc.beuverse.core.navigation.Routes
import com.altankoc.beuverse.core.ui.components.BeuverseButton
import com.altankoc.beuverse.core.ui.components.BeuverseDropdown
import com.altankoc.beuverse.core.ui.components.BeuverseTextField
import com.altankoc.beuverse.core.ui.theme.DarkNavy
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily
import com.altankoc.beuverse.core.ui.theme.beuverseBackgroundGradient
import com.altankoc.beuverse.core.utils.Resource
import com.altankoc.beuverse.feature.auth.presentation.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordAgain by remember { mutableStateOf("") }
    var selectedDepartment by remember { mutableStateOf("") }
    var selectedDepartmentEnum by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    val isDark = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val darkNavy = DarkNavy
    val bgColors = MaterialTheme.colorScheme.beuverseBackgroundGradient

    var firstNameError by remember { mutableStateOf(false) }
    var lastNameError by remember { mutableStateOf(false) }
    var usernameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordAgainError by remember { mutableStateOf(false) }
    var departmentError by remember { mutableStateOf(false) }

    val registerState by viewModel.registerState.collectAsState()

    val departmentMap = mapOf(
        stringResource(R.string.dept_muhendislik) to "MUHENDISLIK_FAKULTESI",
        stringResource(R.string.dept_tip) to "TIP_FAKULTESI",
        stringResource(R.string.dept_iktisadi) to "IKTISADI_IDARI_BILIMLER",
        stringResource(R.string.dept_fen) to "FEN_FAKULTESI",
        stringResource(R.string.dept_dis_hekimligi) to "DIS_HEKIMLIGI_FAKULTESI",
        stringResource(R.string.dept_ilahiyat) to "ILAHIYAT_FAKULTESI",
        stringResource(R.string.dept_saglik) to "SAGLIK_BILIMLERI_FAKULTESI",
        stringResource(R.string.dept_iletisim) to "ILETISIM_FAKULTESI",
        stringResource(R.string.dept_spor) to "SPOR_BILIMLERI_FAKULTESI",
        stringResource(R.string.dept_diger) to "DIGER",
    )

    val departments = departmentMap.keys.toList()

    LaunchedEffect(registerState) {
        if (registerState is Resource.Success) {
            showSuccessDialog = true
        }
    }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            containerColor = if (isDark) Color(0xFF1E2A38) else Color.White,
            title = {
                Text(
                    text = stringResource(R.string.register),
                    fontFamily = JakartaSansFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color.White else darkNavy
                )
            },
            text = {
                Text(
                    text = stringResource(R.string.register_success),
                    fontFamily = JakartaSansFontFamily,
                    color = if (isDark) Color.White.copy(alpha = 0.7f) else Color.DarkGray
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showSuccessDialog = false
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Register.route) { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.ok),
                        fontFamily = JakartaSansFontFamily,
                        color = primaryColor
                    )
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(colors = bgColors))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(48.dp))

            Image(
                painter = painterResource(id = if (isDark) R.drawable.logo_dark else R.drawable.logo_light),
                contentDescription = "Beuverse Logo",
                modifier = Modifier.size(120.dp)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(R.string.register),
                fontFamily = JakartaSansFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                color = if (isDark) Color.White else darkNavy
            )

            Spacer(modifier = Modifier.height(24.dp))

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                color = if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.85f),
                border = if (isDark) {
                    androidx.compose.foundation.BorderStroke(0.5.dp, Color.White.copy(alpha = 0.1f))
                } else {
                    androidx.compose.foundation.BorderStroke(0.5.dp, Color.Black.copy(alpha = 0.05f))
                }
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        BeuverseTextField(
                            value = firstName,
                            onValueChange = {
                                firstName = it
                                firstNameError = false
                            },
                            label = stringResource(R.string.first_name),
                            modifier = Modifier.weight(1f),
                            isError = firstNameError
                        )
                        BeuverseTextField(
                            value = lastName,
                            onValueChange = {
                                lastName = it
                                lastNameError = false
                            },
                            label = stringResource(R.string.last_name),
                            modifier = Modifier.weight(1f),
                            isError = lastNameError
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BeuverseTextField(
                        value = username,
                        onValueChange = {
                            username = it
                            usernameError = false
                        },
                        label = stringResource(R.string.username),
                        leadingIcon = Icons.Rounded.AlternateEmail,
                        isError = usernameError
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BeuverseTextField(
                        value = email,
                        onValueChange = {
                            email = it
                            emailError = false
                        },
                        label = stringResource(R.string.email),
                        leadingIcon = Icons.Rounded.Email,
                        isError = emailError
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BeuverseDropdown(
                        selectedItem = selectedDepartment,
                        onItemSelected = { display ->
                            selectedDepartment = display
                            selectedDepartmentEnum = departmentMap[display] ?: ""
                            departmentError = false
                        },
                        label = stringResource(R.string.department),
                        items = departments
                    )

                    if (departmentError) {
                        Text(
                            text = stringResource(R.string.error_field_required),
                            fontFamily = JakartaSansFontFamily,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    BeuverseTextField(
                        value = password,
                        onValueChange = {
                            password = it
                            passwordError = false
                        },
                        label = stringResource(R.string.password),
                        leadingIcon = Icons.Rounded.Lock,
                        isPassword = true,
                        isError = passwordError
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    BeuverseTextField(
                        value = passwordAgain,
                        onValueChange = {
                            passwordAgain = it
                            passwordAgainError = false
                        },
                        label = stringResource(R.string.password_again),
                        leadingIcon = Icons.Rounded.Lock,
                        isPassword = true,
                        isError = passwordAgainError
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    BeuverseButton(
                        text = stringResource(R.string.register),
                        onClick = {
                            firstNameError = firstName.isBlank()
                            lastNameError = lastName.isBlank()
                            usernameError = username.isBlank()
                            emailError = email.isBlank()
                            departmentError = selectedDepartmentEnum.isBlank()
                            passwordError = password.isBlank()
                            passwordAgainError = passwordAgain.isBlank()

                            if (!firstNameError && !lastNameError && !usernameError && !emailError && !departmentError && !passwordError && !passwordAgainError) {
                                viewModel.register(
                                    firstName = firstName,
                                    lastName = lastName,
                                    username = username,
                                    email = email,
                                    password = password,
                                    passwordAgain = passwordAgain,
                                    department = selectedDepartmentEnum
                                )
                            }
                        },
                        enabled = registerState !is Resource.Loading
                    )
                }
            }

            if (registerState is Resource.Error) {
                Spacer(modifier = Modifier.height(8.dp))
                val errorMessage = when ((registerState as Resource.Error).message) {
                    "error_field_required" -> stringResource(R.string.error_field_required)
                    "error_invalid_email_domain" -> stringResource(R.string.error_invalid_email_domain)
                    "error_email_name_mismatch" -> stringResource(R.string.error_email_name_mismatch)
                    "error_password_too_short" -> stringResource(R.string.error_password_too_short)
                    "error_passwords_not_match" -> stringResource(R.string.error_passwords_not_match)
                    "error_username_invalid" -> stringResource(R.string.error_username_invalid)
                    "error_already_exists" -> stringResource(R.string.error_already_exists)
                    "error_network" -> stringResource(R.string.error_network)
                    else -> stringResource(R.string.error_unknown)
                }

                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    fontFamily = JakartaSansFontFamily
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.padding(bottom = 32.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.already_have_account),
                    fontFamily = JakartaSansFontFamily,
                    color = if (isDark) Color.White.copy(alpha = 0.5f) else darkNavy.copy(alpha = 0.6f)
                )
                TextButton(
                    onClick = {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Register.route) { inclusive = true }
                        }
                    }
                ) {
                    Text(
                        text = stringResource(R.string.login),
                        fontFamily = JakartaSansFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = primaryColor
                    )
                }
            }
        }

        if (registerState is Resource.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = primaryColor
            )
        }
    }
}