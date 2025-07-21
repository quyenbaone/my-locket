package com.mylocket.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mylocket.service.SupabaseAuthService
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.MyLocketTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseNameScreen(
    navController: NavController,
    authService: SupabaseAuthService
){
    val scope = rememberCoroutineScope()

    var lastname by remember {
        mutableStateOf("")
    }

    var firstname by remember {
        mutableStateOf("")
    }

    val isTrueName = remember(lastname, firstname) { lastname.isNotEmpty() && firstname.isNotEmpty() }

    val currentUser = authService.getCurrentUser()
    val context = LocalContext.current

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
            Spacer(modifier = Modifier.height(50.dp))

            // Main content section
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(
                    initialOffsetY = { it / 2 },
                    animationSpec = tween(500, delayMillis = 200)
                ) + fadeIn(animationSpec = tween(500, delayMillis = 200))
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 32.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.05f)
                    ),
                    shape = RoundedCornerShape(24.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        // Welcome text
                        Text(
                            text = "Hoàn tất hồ sơ",
                            style = MaterialTheme.typography.headlineSmall,
                            color = Color.White.copy(alpha = 0.9f),
                            fontWeight = FontWeight.Light,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Tên bạn là gì?",
                            style = MaterialTheme.typography.headlineMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // First name input field
                        OutlinedTextField(
                            value = firstname,
                            onValueChange = { firstname = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                                focusedBorderColor = BlueOcean.copy(alpha = 0.8f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                cursorColor = BlueOcean,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                                focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            placeholder = {
                                Text(
                                    text = "Tên của bạn",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Last name input field
                        OutlinedTextField(
                            value = lastname,
                            onValueChange = { lastname = it },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(64.dp),
                            colors = TextFieldDefaults.outlinedTextFieldColors(
                                containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                                focusedBorderColor = BlueOcean.copy(alpha = 0.8f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                                cursorColor = BlueOcean,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                                focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                                unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                            ),
                            shape = RoundedCornerShape(16.dp),
                            placeholder = {
                                Text(
                                    text = "Họ của bạn",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
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
                        scope.launch {
                            val result = authService.updateProfile(firstname + " " + lastname)
                            if (result.isSuccess) {
                                navController.navigate("home")
                            } else {
                                Toast.makeText(context, "Thêm tên người dùng thất bại", Toast.LENGTH_SHORT).show()
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
                    enabled = isTrueName,
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = if (isTrueName) 4.dp else 0.dp
                    )
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Hoàn tất",
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

// Preview cho ChooseNameScreen
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChooseNameScreenPreview() {
    MyLocketTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        val authService = com.mylocket.service.SupabaseAuthService()
        ChooseNameScreen(
            navController = navController,
            authService = authService
        )
    }
}
