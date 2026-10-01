package com.example.persistencia.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.persistencia.ui.theme.AppColors

@Composable
fun UserBadge(username: String, onLogout: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RoundedCornerShape(50), color = AppColors.AccentSoft) {
        Row(
            modifier = Modifier.padding(start = 12.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "@$username",
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = AppColors.AccentStrong
            )
            IconButton(onClick = onLogout, modifier = Modifier.size(32.dp)) {
                Icon(
                    Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Cerrar sesión",
                    tint = AppColors.AccentStrong,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}