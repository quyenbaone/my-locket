package com.mylocket.ui.screens

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.SheetState
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mylocket.service.SupabaseAuthService
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.mylocket.R
import com.mylocket.ui.theme.BlueOcean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnterPasswordScreen(
    authService: SupabaseAuthService,
    navController: NavController,
    email: String?,
) {
    val scope = rememberCoroutineScope()

    var password by remember {
        mutableStateOf("")
    }

    val isPassword = remember(password) {
        !password.isEmpty()
    }

    var passwordVisible by remember { mutableStateOf(false) }

    val fixedEmail = email?.substring(1, email.length-1)

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 20.dp, bottom = 20.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(start = 20.dp, top = 30.dp)
                .clip(shape = CircleShape)
                .size(50.dp),
            colors = IconButtonDefaults.iconButtonColors(
                contentColor = Color.White,
                containerColor = MaterialTheme.colorScheme.onSurface
            )
        ) {
            Icon(painter = painterResource(id = R.drawable.back), contentDescription = null)
        }

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Điền mật khẩu của bạn",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.White,
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = password,
                onValueChange = { input ->
                    run {
                        password = input
                    }
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                    .clip(shape = RoundedCornerShape(10.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                placeholder = {
                    Text(text = "Mật khẩu")
                },
                trailingIcon = {

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            painter = if (passwordVisible) painterResource(id = R.drawable.ic_visibility_off) else painterResource(
                                id = R.drawable.ic_visibility
                            ), contentDescription = ""
                        )
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            )

            Button(
                onClick = {

                        if (fixedEmail != null) {
                            scope.launch {
                                val result = authService.resetPassword(email)
                                if (result.isSuccess) {
                                    Toast.makeText(
                                        context,
                                        "Đã gửi đường dẫn đổi mật khẩu vào email",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(context, "Không thể gửi đến email", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = Color.White
                )
            ) {
                Text(
                    text = "Bạn đã quên mật khẩu",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Button(
            onClick = {
                if (fixedEmail != null) {

                    scope.launch {
                        SignIn(authService, navController, password, fixedEmail, context)
                    }

                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, bottom = 10.dp)
                .height(60.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueOcean,
                contentColor = Color.Black,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = Color(0xFF4E4E50)
            ),
            enabled = isPassword,

            ) {
            Row {
                Text(
                    text = "Tiếp tục",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,

                    )
            }
        }

    }
}

suspend fun SignIn(
    authService: SupabaseAuthService,
    navController: NavController,
    password: String,
    email: String,
    context: Context
) {
    val result = authService.signIn(email, password)
    if (result.isSuccess) {
        navController.navigate("home")
    } else {
        Log.e("SignIn", "Sign in failed: ${result.exceptionOrNull()}")
        Toast.makeText(
            context,
            "Authentication failed.",
            Toast.LENGTH_SHORT,
        ).show()
    }
}


