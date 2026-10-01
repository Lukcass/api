package com.example.persistencia.ui.components

import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.PendingActions
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.persistencia.ui.theme.AppColors


private data class NavTab(val label: String, val icon: ImageVector)

private val tabs = listOf(
    NavTab("Buscar", Icons.Outlined.Search),
    NavTab("Todas", Icons.AutoMirrored.Outlined.List),
    NavTab("Pendientes", Icons.Outlined.PendingActions),
    NavTab("Completadas", Icons.Outlined.CheckCircle),
    NavTab("Catálogo", Icons.Outlined.ShoppingCart),
    NavTab("Comunidad", Icons.Outlined.Groups)
)

@Composable
fun TaskBottomNavigation(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxWidth().navigationBarsPadding().padding(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(28.dp))
                .background(AppColors.Paper)
                .padding(horizontal = 6.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            tabs.forEachIndexed { index, tab ->
                val isSelected = index == selectedTab
                val pillBg by animateColorAsState(if (isSelected) AppColors.AccentStrong else Color.Transparent, label = "pillBg")
                val contentColor by animateColorAsState(if (isSelected) Color.White else AppColors.InkFaint, label = "contentColor")

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(18.dp))
                        .background(pillBg)
                        .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                            onTabSelected(index)
                        }
                        .padding(horizontal = 12.dp, vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tab.icon,
                        contentDescription = tab.label,
                        tint = contentColor,
                        modifier = Modifier.size(21.dp)
                    )
                }
            }
        }
    }
}