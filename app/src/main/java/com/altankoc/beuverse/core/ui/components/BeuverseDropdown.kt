package com.altankoc.beuverse.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowDropDown
import androidx.compose.material.icons.rounded.ArrowDropUp
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
                    fontFamily = JakartaSansFontFamily
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = if (expanded)
                        Icons.Rounded.ArrowDropUp
                    else
                        Icons.Rounded.ArrowDropDown,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                focusedLabelColor = MaterialTheme.colorScheme.primary,
                unfocusedLabelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
            )
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier.fillMaxWidth()
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = item,
                            fontFamily = JakartaSansFontFamily
                        )
                    },
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    }
                )
            }
        }
    }
}