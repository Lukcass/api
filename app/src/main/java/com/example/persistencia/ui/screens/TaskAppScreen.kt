package com.example.persistencia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.persistencia.data.Task
import com.example.persistencia.ui.CatalogScreen
import com.example.persistencia.ui.community.CommunityScreen
import com.example.persistencia.ui.components.TaskBottomNavigation
import com.example.persistencia.ui.theme.AppColors
import com.example.persistencia.ui.utils.DateUtils

@Composable
fun TaskAppScreen(
    tasks: List<Task>,
    onToggleTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    onAddClick: () -> Unit,
    onEditTask: (Task) -> Unit,
    onViewTask: (Task) -> Unit,
    onAddTemplate: (String, String) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val (todayYear, todayMonth, todayDay) = remember { DateUtils.currentYearMonthDay() }
    var searchYear by rememberSaveable { mutableIntStateOf(todayYear) }
    var searchMonth by rememberSaveable { mutableIntStateOf(todayMonth) }
    var searchSelectedDay by rememberSaveable { mutableIntStateOf(todayDay) }

    Scaffold(
        containerColor = AppColors.Background,
        bottomBar = {
            TaskBottomNavigation(selectedTab = selectedTab, onTabSelected = { selectedTab = it })
        },
        floatingActionButton = {
            // El botón "+" solo tiene sentido en las pestañas de tareas (0 a 3)
            if (selectedTab < 4) {
                FloatingActionButton(
                    onClick = onAddClick,
                    containerColor = AppColors.AccentStrong,
                    contentColor = Color.White
                ) {
                    Icon(Icons.Filled.Add, contentDescription = "Agregar tarea")
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fillMaxSize()
        ) {
            Box(modifier = if (selectedTab == 0) Modifier.fillMaxSize() else Modifier.size(0.dp)) {
                TaskSearchScreen(
                    tasks = tasks,
                    year = searchYear,
                    month = searchMonth,
                    selectedDay = searchSelectedDay,
                    onYearMonthChange = { y, m ->
                        searchYear = y
                        searchMonth = m
                        searchSelectedDay = 1
                    },
                    onDaySelected = { searchSelectedDay = it },
                    onToggleTask = onToggleTask,
                    onViewTask = onViewTask,
                    onEditTask = onEditTask,
                    onDeleteTask = onDeleteTask
                )
            }
            Box(modifier = if (selectedTab == 1) Modifier.fillMaxSize() else Modifier.size(0.dp)) {
                AllTasksScreen(
                    tasks = tasks,
                    onToggleTask = onToggleTask,
                    onViewTask = onViewTask,
                    onEditTask = onEditTask,
                    onDeleteTask = onDeleteTask
                )
            }
            Box(modifier = if (selectedTab == 2) Modifier.fillMaxSize() else Modifier.size(0.dp)) {
                PendingTasksScreen(
                    tasks = tasks,
                    onToggleTask = onToggleTask,
                    onViewTask = onViewTask,
                    onEditTask = onEditTask,
                    onDeleteTask = onDeleteTask
                )
            }
            Box(modifier = if (selectedTab == 3) Modifier.fillMaxSize() else Modifier.size(0.dp)) {
                CompletedTasksScreen(
                    tasks = tasks,
                    onToggleTask = onToggleTask,
                    onViewTask = onViewTask,
                    onEditTask = onEditTask,
                    onDeleteTask = onDeleteTask
                )
            }
            // Pestañas de la nube: solo se componen cuando se abren
            if (selectedTab == 4) CatalogScreen(onAddToTasks = onAddTemplate)
            if (selectedTab == 5) CommunityScreen()
        }
    }
}