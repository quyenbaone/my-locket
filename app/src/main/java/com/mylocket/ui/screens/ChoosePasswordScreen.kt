package com.mylocket.ui.screens

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mylocket.service.SupabaseAuthService
import com.mylocket.viewmodel.AuthViewModel
import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.launch
import com.mylocket.R
import com.mylocket.ui.theme.BlueOcean
import kotlin.coroutines.coroutineContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosePasswordScreen(
    authService: SupabaseAuthService,
    navController: NavController,
    email: String?
){
    val scope = rememberCoroutineScope()
    val authViewModel: AuthViewModel = viewModel()

    var password by remember {
        mutableStateOf("")
    }

    val isTruePassword = remember (password) {
        password.length >= 8
    }

    var passwordVisible by remember { mutableStateOf(false) }

    val emailFix = email?.substring(1,email.length-1)

    var context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top section with back button
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(300))
            ) {
                IconButton(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .padding(top = 30.dp)
                        .size(48.dp),
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = Color.White,
                        containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                    )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.back),
                        contentDescription = "Quay lại",
                        tint = Color.White
                    )
                }
            }

            // Main content section - Clean design
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(500, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(500, delayMillis = 200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp, horizontal = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    // Welcome text - Clean design
                    Text(
                        text = "Tạo tài khoản mới",
                        style = MaterialTheme.typography.headlineSmall,
                        color = BlueOcean,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Chọn mật khẩu bảo mật",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onBackground,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(48.dp))

                    // Password input field - Clean design
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.Transparent,
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        shape = RoundedCornerShape(16.dp),
                        placeholder = {
                            Text(
                                text = "Tạo mật khẩu mạnh",
                                style = MaterialTheme.typography.bodyLarge
                            )
                        },
                        label = {
                            Text(
                                text = "Mật khẩu",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { passwordVisible = !passwordVisible }
                            ) {
                                Icon(
                                    painter = if (passwordVisible)
                                        painterResource(id = R.drawable.ic_visibility_off)
                                    else
                                        painterResource(id = R.drawable.ic_visibility),
                                    contentDescription = if (passwordVisible) "Ẩn mật khẩu" else "Hiện mật khẩu",
                                    tint = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Password requirement text
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Mật khẩu phải có ít nhất ",
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Text(
                            text = "8 ký tự",
                            color = BlueOcean,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Bottom section with continue button
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { it },
                    animationSpec = tween(500, delayMillis = 400)
                ) + fadeIn(animationSpec = tween(500, delayMillis = 400))
            ) {
                Button(
                    onClick = {
                        if (emailFix != null) {
                            scope.launch {
                                val result = authViewModel.signUp(emailFix, password)
                                if (result.isSuccess) {
                                    Log.d("ChoosePassword", "Sign up successful for: $emailFix")
                                    navController.navigate("chooseName")
                                } else {
                                    val exception = result.exceptionOrNull()
                                    Log.e("ChoosePassword", "Sign up failed for $emailFix: $exception")

                                    // Provide more specific error messages based on the exception
                                    val errorMessage = when {
                                        exception?.message?.contains("User already registered", ignoreCase = true) == true ->
                                            "Email này đã được đăng ký. Vui lòng sử dụng email khác hoặc đăng nhập."
                                        exception?.message?.contains("Password should be at least", ignoreCase = true) == true ->
                                            "Mật khẩu phải có ít nhất 6 ký tự."
                                        exception?.message?.contains("Invalid email", ignoreCase = true) == true ->
                                            "Email không hợp lệ. Vui lòng kiểm tra lại."
                                        exception?.message?.contains("network", ignoreCase = true) == true ->
                                            "Lỗi kết nối mạng. Vui lòng kiểm tra kết nối internet."
                                        exception?.message?.contains("timeout", ignoreCase = true) == true ->
                                            "Kết nối quá chậm. Vui lòng thử lại."
                                        else -> "Đăng ký thất bại. Vui lòng thử lại."
                                    }

                                    Toast.makeText(
                                        context,
                                        errorMessage,
                                        Toast.LENGTH_LONG,
                                    ).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueOcean,
                        contentColor = Color.White,
                        disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                        disabledContentColor = Color.White.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    enabled = isTruePassword,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 2.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Tạo tài khoản",
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyLarge,
                            fontSize = 16.sp
                        )

                        Spacer(modifier = Modifier.width(12.dp))

                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}

// Preview cho ChoosePasswordScreen
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChoosePasswordScreenPreview() {
    com.mylocket.ui.theme.MyLocketTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        val authService = com.mylocket.service.SupabaseAuthService()
        ChoosePasswordScreen(
            authService = authService,
            navController = navController,
            email = "demo@example.com"
        )
    }
}
