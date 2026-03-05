package com.altankoc.beuverse.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material.icons.rounded.Visibility
import androidx.compose.material.icons.rounded.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily

@Composable
fun BeuverseTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    isPassword: Boolean = false,
    leadingIcon: ImageVector? = null,
    isError: Boolean = false,
    errorMessage: String? = null
) {
    var passwordVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = {
            Text(
                text = label,
                fontFamily = JakartaSansFontFamily
            )
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true,
        isError = isError,
        supportingText = {
            if (isError && errorMessage != null) {
                Text(
                    text = errorMessage,
                    fontFamily = JakartaSansFontFamily,
                    color = MaterialTheme.colorScheme.error
                )
            }
        },
        leadingIcon = leadingIcon?.let {
            {
                Icon(
                    imageVector = it,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        },
        trailingIcon = when {
            isPassword -> {
                {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible)
                                Icons.Rounded.VisibilityOff
                            else
                                Icons.Rounded.Visibility,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            value.isNotEmpty() -> {
                {
                    IconButton(onClick = { onValueChange("") }) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }
            else -> null
        },
        visualTransformation = if (isPassword && !passwordVisible)
            PasswordVisualTransformation()
        else
            VisualTransformation.None,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
        )
    )
}