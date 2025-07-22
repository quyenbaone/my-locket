package com.mylocket.ui.screens

import android.util.Patterns
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mylocket.R
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.MyLocketTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAndLoginScreen(
    navController: NavController,
    registerOrLogin: String?
) {
    var email by remember { mutableStateOf("") }
    val isEmailValid = remember(email) { Patterns.EMAIL_ADDRESS.matcher(email).matches() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
//            // Back button (top left corner)
//            Box(modifier = Modifier.fillMaxWidth()) {
//                IconButton(
//                    onClick = { navController.popBackStack() },
//                    modifier = Modifier
//                        .size(48.dp)
//                        .align(Alignment.TopStart),
//                    colors = IconButtonDefaults.iconButtonColors(
//                        contentColor = Color.White,
//                        containerColor = Color.Transparent
//                    )
//                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.back),
//                        contentDescription = "Quay lại",
//                        tint = Color.White,
//                        modifier = Modifier.size(24.dp)
//                    )
//                }
//            }

            Spacer(modifier = Modifier.height(24.dp))

            // Welcome text
            Text(
                text = if (registerOrLogin == "register") "Tạo tài khoản mới" else "Chào mừng trở lại",
                style = MaterialTheme.typography.headlineSmall,
                color = BlueOcean,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Bằng cách nhấn vào nút Tiếp tục, bạn đồng ý với chúng tôi",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 13.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp),
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            // Email input
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(68.dp),
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
                shape = RoundedCornerShape(20.dp),
                placeholder = {
                    Text(
                        text = "Nhập địa chỉ email của bạn",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                label = {
                    Text(
                        text = "Email",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                }
            )

            Spacer(modifier = Modifier.height(20.dp))


            Spacer(modifier = Modifier.height(4.dp))

            Row(horizontalArrangement = Arrangement.Center) {
                Text(
                    text = "Điều khoản dịch vụ",
                    color = BlueOcean,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = " và ",
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "Chính sách quyền riêng tư",
                    color = BlueOcean,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Continue button
            Button(
                onClick = {
                    if (registerOrLogin == "register") {
                        navController.navigate("choosePassword/{$email}")
                    } else if (registerOrLogin == "login") {
                        navController.navigate("enterPassword/{$email}")
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueOcean,
                    contentColor = Color.White,
                    disabledContainerColor = Color.Gray.copy(alpha = 0.2f),
                    disabledContentColor = Color.White.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(20.dp),
                enabled = isEmailValid,
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
                        text = "Tiếp tục",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 16.sp,
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

// Previews
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun RegisterScreenPreview() {
    MyLocketTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        RegisterAndLoginScreen(
            navController = navController,
            registerOrLogin = "register"
        )
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun LoginScreenPreview() {
    MyLocketTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        RegisterAndLoginScreen(
            navController = navController,
            registerOrLogin = "login"
        )
    }
}
