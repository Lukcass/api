package com.example.persistencia.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.persistencia.ui.theme.AppColors

@Composable
fun LoginScreen(vm: AuthViewModel) {
    val s = vm.uiState
    val isLogin = s.mode == AuthMode.LOGIN
    val cardShape = RoundedCornerShape(28.dp)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.Background)
            .statusBarsPadding()
            .imePadding(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .clip(cardShape)
                .background(AppColors.Paper)
                .border(1.dp, AppColors.Line, cardShape)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // bhh Avatar: muestra la inicial del usuario; si aún no escribe nada, el ícono de persona
            Box(
                modifier = Modifier.size(52.dp).clip(CircleShape).background(AppColors.AccentSoft),
                contentAlignment = Alignment.Center
            ) {
                if (s.username.isNotEmpty()) {
                    Text(
                        text = s.username.first().uppercaseChar().toString(),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.AccentStrong
                    )
                } else {
                    Icon(Icons.Outlined.Person, contentDescription = null, tint = AppColors.Accent)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                text = if (isLogin) "Entra con tu usuario" else "Crea tu usuario",
                fontFamily = FontFamily.Serif,
                fontSize = 26.sp,
                color = AppColors.Ink,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = "Tus tareas se guardan con este nombre y vuelven si reinstalas la app.",
                fontSize = 13.sp,
                color = AppColors.InkSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(20.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(AppColors.Background)
                    .padding(4.dp)
            ) {
                Segment("Entrar", isLogin, Modifier.weight(1f)) { vm.setMode(AuthMode.LOGIN) }
                Segment("Crear usuario", !isLogin, Modifier.weight(1f)) { vm.setMode(AuthMode.REGISTER) }
            }

            Spacer(Modifier.height(20.dp))
            Text(
                "Nombre de usuario",
                fontSize = 13.sp,
                color = AppColors.Ink,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = s.username,
                onValueChange = vm::onUsernameChange,
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Text("@", color = AppColors.InkSoft) },
                placeholder = { Text("tu_usuario", color = AppColors.InkFaint) },
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AppColors.Accent,
                    unfocusedBorderColor = AppColors.Line,
                    focusedContainerColor = AppColors.CardWhite,
                    unfocusedContainerColor = AppColors.CardWhite
                ),
                keyboardOptions = KeyboardOptions(
                    capitalization = KeyboardCapitalization.None,
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(onDone = { vm.submit() })
            )
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (isLogin) "El mismo nombre con el que creaste tu usuario."
                else "Elige un nombre único: 3 a 20 caracteres (letras, números, _ y .).",
                fontSize = 12.sp,
                color = AppColors.InkSoft,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            s.error?.let {
                Spacer(Modifier.height(10.dp))
                Text(
                    it,
                    fontSize = 13.sp,
                    color = AppColors.Danger,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(Modifier.height(20.dp))
            Button(
                onClick = { vm.submit() },
                enabled = s.username.isNotBlank() && !s.loading,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.Accent,
                    contentColor = Color.White,
                    disabledContainerColor = Color(0xFFA9C2B8),
                    disabledContentColor = Color.White
                )
            ) {
                if (s.loading) {
                    CircularProgressIndicator(Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text(if (isLogin) "Entrar" else "Crear usuario", fontWeight = FontWeight.Medium)
                }
            }

            Spacer(Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = if (isLogin) "¿No tienes usuario? " else "¿Ya tienes usuario? ",
                    fontSize = 13.sp,
                    color = AppColors.InkSoft
                )
                Text(
                    text = if (isLogin) "Crear uno" else "Entrar",
                    fontSize = 13.sp,
                    color = AppColors.AccentStrong,
                    fontWeight = FontWeight.Medium,
                    textDecoration = TextDecoration.Underline,
                    modifier = Modifier.clickable {
                        vm.setMode(if (isLogin) AuthMode.REGISTER else AuthMode.LOGIN)
                    }
                )
            }
        }
    }
}

@Composable
private fun Segment(text: String, selected: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .clip(shape)
            .background(if (selected) AppColors.Paper else Color.Transparent)
            .then(if (selected) Modifier.border(2.dp, AppColors.Accent, shape) else Modifier)
            .clickable(onClick = onClick)
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            color = if (selected) AppColors.Ink else AppColors.InkSoft
        )
    }
}