package com.altankoc.beuverse.core.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.altankoc.beuverse.core.ui.theme.JakartaSansFontFamily

@Composable
fun BeuverseTagChip(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    FilterChip(
        selected = isSelected,
        onClick = onClick,
        label = {
            Text(
                text = label,
                fontFamily = JakartaSansFontFamily,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                fontSize = 13.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        },
        modifier = modifier,
        shape = RoundedCornerShape(100.dp),
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primary,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimary,
            containerColor = MaterialTheme.colorScheme.surface,
            labelColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = isSelected,
            borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
            selectedBorderColor = MaterialTheme.colorScheme.primary
        )
    )
}