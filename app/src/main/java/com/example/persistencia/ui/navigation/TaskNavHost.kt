package com.example.persistencia.ui.navigation

import android.app.Application
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.persistencia.ui.components.UserBadge
import com.example.persistencia.ui.screens.AddEditTaskScreen
import com.example.persistencia.ui.screens.TaskAppScreen
import com.example.persistencia.ui.screens.ViewTaskScreen
import com.example.persistencia.ui.theme.TaskViewModel

private object Routes {
    const val LIST = "list"
    const val ADD = "add"
    const val EDIT = "edit/{taskId}"
    const val VIEW = "view/{taskId}"
    fun edit(taskId: Int) = "edit/$taskId"
    fun view(taskId: Int) = "view/$taskId"
}

@Composable
fun TaskNavHost(username: String, onLogout: () -> Unit) {
    val navController = rememberNavController()

    // Un ViewModel por usuario (la clave incluye el nombre)
    val taskViewModel: TaskViewModel = viewModel(
        key = "tasks_$username",
        factory = viewModelFactory {
            initializer {
                TaskViewModel(
                    checkNotNull(this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]) as Application,
                    username
                )
            }
        }
    )
    val tasks by taskViewModel.tasks.collectAsState()

    NavHost(
        navController = navController,
        startDestination = Routes.LIST,
        enterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(250)
            )
        },
        exitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Left,
                animationSpec = tween(250)
            )
        },
        popEnterTransition = {
            slideIntoContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(250)
            )
        },
        popExitTransition = {
            slideOutOfContainer(
                towards = AnimatedContentTransitionScope.SlideDirection.Right,
                animationSpec = tween(250)
            )
        }
    ) {
        composable(Routes.LIST) {
            Box(Modifier.fillMaxSize()) {
                TaskAppScreen(
                    tasks = tasks,
                    onToggleTask = { taskViewModel.toggleTaskState(it) },
                    onDeleteTask = { taskViewModel.deleteTask(it) },
                    onAddClick = { navController.navigate(Routes.ADD) },
                    onEditTask = { navController.navigate(Routes.edit(it.id)) },
                    onViewTask = { navController.navigate(Routes.view(it.id)) }
                )
                UserBadge(
                    username = username,
                    onLogout = onLogout,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .statusBarsPadding()
                        .padding(top = 10.dp, end = 16.dp)
                )
            }
        }

        composable(Routes.ADD) {
            AddEditTaskScreen(
                title = "Nueva Tarea",
                initialTitulo = "",
                initialDescripcion = "",
                onBack = { navController.popBackStack() },
                onConfirm = { titulo, descripcion ->
                    taskViewModel.addTask(titulo, descripcion)
                    navController.popBackStack()
                }
            )
        }

        composable(
            route = Routes.EDIT,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            val task = tasks.firstOrNull { it.id == taskId }
            if (task != null) {
                AddEditTaskScreen(
                    title = "Editar Tarea",
                    initialTitulo = task.titulo,
                    initialDescripcion = task.descripcion,
                    onBack = { navController.popBackStack() },
                    onConfirm = { titulo, descripcion ->
                        taskViewModel.updateTask(task, titulo, descripcion)
                        navController.popBackStack()
                    }
                )
            }
        }

        composable(
            route = Routes.VIEW,
            arguments = listOf(navArgument("taskId") { type = NavType.IntType })
        ) { backStackEntry ->
            val taskId = backStackEntry.arguments?.getInt("taskId")
            val task = tasks.firstOrNull { it.id == taskId }
            if (task != null) {
                ViewTaskScreen(
                    task = task,
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}