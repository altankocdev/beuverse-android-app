package com.altankoc.beuverse.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.KeyboardArrowDown
import androidx.compose.material.icons.rounded.KeyboardArrowUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BeuverseDropdown(
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    label: String,
    items: List<String>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val isDark = isSystemInDarkTheme()
    val primaryColor = MaterialTheme.colorScheme.primary
    val darkNavy = Color(0xFF1A2A3A)

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded },
        modifier = modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedItem,
            onValueChange = {},
            readOnly = true,
            label = {
                Text(
                    text = label,
                    fontFamily = JakartaSansFontFamily,
                    fontSize = 14.sp
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded)
                        Icons.Rounded.KeyboardArrowUp
                    else
                        Icons.Rounded.KeyboardArrowDown,
                    contentDescription = null,
                    tint = if (expanded) primaryColor else (if (isDark) Color.White.copy(alpha = 0.4f) else darkNavy.copy(alpha = 0.4f))
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(20.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = primaryColor,
                unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f),
                focusedLabelColor = primaryColor,
                unfocusedLabelColor = if (isDark) Color.White.copy(alpha = 0.4f) else darkNavy.copy(alpha = 0.5f),
                focusedTextColor = if (isDark) Color.White else darkNavy,
                unfocusedTextColor = if (isDark) Color.White else darkNavy
            )
        )

        MaterialTheme(
            shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(24.dp))
        ) {
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isDark) Color(0xFF1E2A38) else Color.White)
                    .border(
                        width = 1.dp,
                        color = if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(24.dp)
                    )
            ) {
                items.forEach { item ->
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = item,
                                fontFamily = JakartaSansFontFamily,
                                fontSize = 14.sp,
                                color = if (isDark) Color.White else darkNavy
                            )
                        },
                        onClick = {
                            onItemSelected(item)
                            expanded = false
                        },
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 20.dp, vertical = 12.dp)
                    )
                }
            }
        }
    }
}
