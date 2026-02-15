package com.qrmailer.ui.common

import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/** Minimum height for primary action buttons (senior-friendly touch target). */
val PrimaryButtonHeight = 56.dp

/** Modifier for standard primary button height. */
fun Modifier.primaryButtonHeight(): Modifier = height(PrimaryButtonHeight)
