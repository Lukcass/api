package com.example.persistencia.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.persistencia.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditTaskScreen(
    title: String,
    initialTitulo: String,
    initialDescripcion: String,
    onBack: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var titulo by rememberSaveable { mutableStateOf(initialTitulo) }
    var descripcion by rememberSaveable { mutableStateOf(initialDescripcion) }
    val focusManager = LocalFocusManager.current

    val fieldColors = TextFieldDefaults.colors(
        focusedContainerColor = AppColors.Paper,
        unfocusedContainerColor = AppColors.Paper,
        focusedIndicatorColor = Color.Transparent,
        unfocusedIndicatorColor = Color.Transparent,
        cursorColor = AppColors.Accent
    )

    Scaffold(
        containerColor = AppColors.Paper,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        title,
                        color = AppColors.Ink,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = AppColors.Ink
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { onConfirm(titulo, descripcion) },
                        enabled = titulo.isNotBlank(),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = AppColors.AccentSoft,
                            contentColor = AppColors.AccentStrong,
                            disabledContainerColor = AppColors.Background,
                            disabledContentColor = AppColors.InkFaint
                        ),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(
                            Icons.Filled.Check,
                            contentDescription = "Guardar"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AppColors.Paper)
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(horizontal = 4.dp, vertical = 6.dp)
        ) {
            TextField(
                value = titulo,
                onValueChange = {
                    if (it.contains('\n')) {
                        titulo = it.replace("\n", "")
                        focusManager.moveFocus(FocusDirection.Down)
                    } else {
                        titulo = it
                    }
                },
                placeholder = {
                    Text(
                        "Título",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.InkFaint
                    )
                },
                textStyle = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.Ink
                ),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                colors = fieldColors,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "DESCRIPCIÓN",
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.InkFaint,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(6.dp))
            HorizontalDivider(
                color = AppColors.Line,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            TextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                placeholder = {
                    Text(
                        "Escribe una descripción",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.InkFaint
                    )
                },
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = AppColors.Ink,
                    lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.15
                ),
                colors = fieldColors,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            )
        }
    }
}