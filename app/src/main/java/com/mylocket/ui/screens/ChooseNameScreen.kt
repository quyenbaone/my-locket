package com.mylocket.ui.screens

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.mylocket.R
import com.mylocket.service.SupabaseAuthService
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.MyLocketTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseNameScreen(
    navController: NavController,
    authService: SupabaseAuthService
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    var name by remember { mutableStateOf("") }
    var avatarUri by remember { mutableStateOf<Uri?>(null) }
    var showAvatarOptions by remember { mutableStateOf(false) }
    val isNameValid = name.isNotBlank()

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        avatarUri = uri
        showAvatarOptions = false
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center
        ) {
            // Card content
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.05f)
                ),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
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

                    Spacer(modifier = Modifier.height(28.dp))

                    // Avatar image (clickable)
                    Box(
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(BlueOcean.copy(alpha = 0.2f))
                            .clickable { showAvatarOptions = true },
                        contentAlignment = Alignment.Center
                    ) {
                        if (avatarUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = avatarUri),
                                contentDescription = "Ảnh đại diện",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            // Default avatar using user icon
                            Icon(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "Ảnh đại diện mặc định",
                                modifier = Modifier.size(50.dp),
                                tint = BlueOcean
                            )
                        }

                        // Overlay to indicate clickable
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.1f)),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_setting),
                                contentDescription = "Chỉnh sửa",
                                modifier = Modifier
                                    .size(24.dp)
                                    .padding(4.dp),
                                tint = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Nhấn để chọn ảnh đại diện",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.6f),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Name input
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        singleLine = true,
                        placeholder = { Text("Tên của bạn") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.1f),
                            focusedBorderColor = BlueOcean,
                            unfocusedBorderColor = Color.White.copy(alpha = 0.3f),
                            cursorColor = BlueOcean,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White.copy(alpha = 0.9f),
                            focusedPlaceholderColor = Color.White.copy(alpha = 0.6f),
                            unfocusedPlaceholderColor = Color.White.copy(alpha = 0.5f)
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Continue button
            Button(
                onClick = {
                    scope.launch {
                        val result = authService.updateProfile(
                            displayName = name
                        )
                        if (result.isSuccess) {
                            navController.navigate("home")
                        } else {
                            Toast.makeText(context, "Cập nhật hồ sơ thất bại", Toast.LENGTH_SHORT).show()
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
                enabled = isNameValid,
                elevation = ButtonDefaults.buttonElevation(
                    defaultElevation = if (isNameValid) 4.dp else 0.dp
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

        // Avatar options dialog
        if (showAvatarOptions) {
            AlertDialog(
                onDismissRequest = { showAvatarOptions = false },
                title = {
                    Text(
                        text = "Chọn ảnh đại diện",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Bạn muốn sử dụng ảnh đại diện nào?",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.bodyMedium
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Default avatar option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    avatarUri = null
                                    showAvatarOptions = false
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.user),
                                contentDescription = "Avatar mặc định",
                                modifier = Modifier.size(40.dp),
                                tint = BlueOcean
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Sử dụng avatar mặc định",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }

                        // Choose from phone option
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    imagePickerLauncher.launch("image/*")
                                }
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_send),
                                contentDescription = "Chọn từ điện thoại",
                                modifier = Modifier.size(40.dp),
                                tint = BlueOcean
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = "Chọn từ điện thoại",
                                color = Color.White,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(
                        onClick = { showAvatarOptions = false }
                    ) {
                        Text(
                            text = "Hủy",
                            color = BlueOcean
                        )
                    }
                },
                containerColor = Color.Black,
                titleContentColor = Color.White,
                textContentColor = Color.White
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun ChooseNameScreenPreview() {
    MyLocketTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        val authService = SupabaseAuthService()
        ChooseNameScreen(navController, authService)
    }
}
