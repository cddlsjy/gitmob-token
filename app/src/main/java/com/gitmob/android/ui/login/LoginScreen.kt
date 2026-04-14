package com.gitmob.android.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gitmob.android.R
import com.gitmob.android.ui.theme.Coral
import com.gitmob.android.ui.theme.LocalGmColors
import com.gitmob.android.ui.theme.RedColor

@Composable
fun LoginScreen(
    onSuccess: () -> Unit,
    vm: LoginViewModel = viewModel(),
) {
    val c = LocalGmColors.current
    val state by vm.state.collectAsState()
    var token by remember { mutableStateOf("") }
    var showToken by remember { mutableStateOf(false) }

    // 成功后跳转
    LaunchedEffect(state) {
        if (state is LoginUiState.Success) {
            onSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        // Logo
        Icon(
            painter = painterResource(R.drawable.ic_app_logo),
            contentDescription = null,
            tint = androidx.compose.ui.graphics.Color.Unspecified,
            modifier = Modifier.size(80.dp),
        )

        Spacer(Modifier.height(24.dp))

        Text(
            "GitMob",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.primary,
        )

        Text(
            "使用 Personal Access Token 登录",
            fontSize = 14.sp,
            color = c.textSecondary,
        )

        Spacer(Modifier.height(32.dp))

        // Token 输入框
        OutlinedTextField(
            value = token,
            onValueChange = { token = it },
            label = { Text("GitHub Token") },
            placeholder = { Text("ghp_xxxxxxxxxxxx", color = c.textTertiary) },
            visualTransformation = if (showToken) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    if (token.isNotBlank()) {
                        vm.loginWithToken(token.trim())
                    }
                }
            ),
            trailingIcon = {
                IconButton(onClick = { showToken = !showToken }) {
                    Icon(
                        if (showToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                        contentDescription = if (showToken) "隐藏 Token" else "显示 Token",
                        tint = c.textTertiary,
                    )
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Coral,
                unfocusedBorderColor = c.border,
                focusedTextColor = c.textPrimary,
                unfocusedTextColor = c.textPrimary,
                focusedContainerColor = c.bgCard,
                unfocusedContainerColor = c.bgCard,
                focusedLabelColor = Coral,
                unfocusedLabelColor = c.textTertiary,
                cursorColor = Coral,
            ),
            singleLine = true,
        )

        Spacer(Modifier.height(16.dp))

        // 状态显示
        when (val s = state) {
            is LoginUiState.Loading -> {
                CircularProgressIndicator(
                    color = Coral,
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp,
                )
                Spacer(Modifier.height(8.dp))
                Text("正在验证...", fontSize = 13.sp, color = c.textSecondary)
            }
            is LoginUiState.Error -> {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer,
                    ),
                    shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        s.msg,
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(14.dp),
                    )
                }
                Spacer(Modifier.height(8.dp))
            }
            else -> Unit
        }

        Spacer(Modifier.height(16.dp))

        // 登录按钮
        Button(
            onClick = { vm.loginWithToken(token.trim()) },
            enabled = token.isNotBlank() && state !is LoginUiState.Loading,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Coral),
            shape = androidx.compose.foundation.shape.RoundedCornerShape(14.dp),
        ) {
            Text("登录", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        }

        Spacer(Modifier.height(24.dp))

        // 提示信息
        Text(
            "生成 Token 时请勾选以下权限：\nrepo, workflow, user, delete_repo,\nadmin:public_key, notifications",
            fontSize = 11.sp,
            color = c.textTertiary,
            textAlign = TextAlign.Center,
            lineHeight = 17.sp,
        )
    }
}