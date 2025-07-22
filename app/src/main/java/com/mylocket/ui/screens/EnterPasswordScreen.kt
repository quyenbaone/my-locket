package com.mylocket.ui.screens

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mylocket.R
import com.mylocket.service.SupabaseAuthService
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.MyLocketTheme
import com.mylocket.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterPasswordScreen(
    authService: SupabaseAuthService,
    navController: NavController,
    email: String,
) {
    val scope = rememberCoroutineScope()
    val authViewModel: AuthViewModel = viewModel()
    val context = LocalContext.current

    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    val isPasswordValid = password.isNotBlank()
    val fixedEmail = email.removeSurrounding("{", "}")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top section with back button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            horizontalArrangement = Arrangement.Start
        ) {
            IconButton(
                onClick = { navController.popBackStack() },
                modifier = Modifier.size(48.dp),
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = Color.White,
                    containerColor = Color.Transparent
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.back),
                    contentDescription = "Quay lại",
                    tint = Color.White
                )
            }
        }

        // Main content centered
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
                Text(
                    text = "Chào mừng trở lại!",
                    style = MaterialTheme.typography.headlineSmall,
                    color = BlueOcean,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Nhập mật khẩu để tiếp tục",
                    style = MaterialTheme.typography.headlineLarge,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(40.dp))

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(68.dp),
                    shape = RoundedCornerShape(20.dp),
                    placeholder = {
                        Text(
                            text = "Nhập mật khẩu của bạn",
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    },
                    label = {
                        Text(
                            text = "Mật khẩu",
                            fontWeight = FontWeight.Medium
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.Transparent,
                        focusedBorderColor = BlueOcean,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                        cursorColor = BlueOcean,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                        unfocusedPlaceholderColor = Color.White.copy(alpha = 0.4f),
                        focusedLabelColor = BlueOcean,
                        unfocusedLabelColor = Color.White.copy(alpha = 0.6f)
                    ),
                    trailingIcon = {
                        IconButton(
                            onClick = { passwordVisible = !passwordVisible },
                            modifier = Modifier.clip(CircleShape)
                        ) {
                            Icon(
                                painter = painterResource(
                                    id = if (passwordVisible)
                                        R.drawable.ic_visibility_off
                                    else
                                        R.drawable.ic_visibility
                                ),
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Password),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Forgot password
            TextButton(
                onClick = {
                    scope.launch {
                        val result = authService.resetPassword(fixedEmail)
                        Toast.makeText(
                            context,
                            if (result.isSuccess)
                                "Đã gửi đường dẫn đổi mật khẩu vào email"
                            else
                                "Không thể gửi đến email",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            ) {
                Text(
                    text = "Quên mật khẩu?",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    color = BlueOcean
                )
            }

            Spacer(modifier = Modifier.height(32.dp)) // cách một đoạn

            Button(
                onClick = {
                    scope.launch {
                        SignIn(authViewModel, navController, password, fixedEmail, context)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                shape = RoundedCornerShape(20.dp),
                enabled = isPasswordValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueOcean,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                    disabledContentColor = Color.White.copy(alpha = 0.4f)
                ),
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = 0.dp,
                    pressedElevation = 4.dp
                )
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Đăng nhập",
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


// Sign in helper function
suspend fun SignIn(
    authViewModel: AuthViewModel,
    navController: NavController,
    password: String,
    email: String,
    context: Context
) {
    val result = authViewModel.signIn(email, password)
    if (result.isSuccess) {
        Log.d("SignIn", "Sign in successful for: $email")
        // Điều hướng có thể được xử lý tự động nếu bạn theo dõi auth state
    } else {
        val errorMessage = result.exceptionOrNull()?.message ?: "Đăng nhập thất bại. Vui lòng thử lại."
        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
        Log.e("SignIn", "Đăng nhập lỗi: $errorMessage")
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun EnterPasswordScreenPreview() {
    MyLocketTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        val authService = SupabaseAuthService()
        EnterPasswordScreen(
            authService = authService,
            navController = navController,
            email = "demo@example.com"
        )
    }
}
