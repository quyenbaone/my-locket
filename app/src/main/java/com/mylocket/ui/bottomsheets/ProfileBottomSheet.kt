package com.mylocket.ui.bottomsheets

import android.annotation.SuppressLint
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavController
import com.mylocket.R
import com.mylocket.service.SupabaseAuthService
import com.mylocket.service.SupabaseDatabaseService
import com.mylocket.viewmodel.AuthViewModel
import com.mylocket.data.User
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.MyLocketTheme
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun ProfileBottomSheet(
    authService: SupabaseAuthService,
    navController: NavController,
    authViewModel: AuthViewModel,
    logOut: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val currentUser = authService.getCurrentUser()
    val databaseService = SupabaseDatabaseService()
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    // State for user data from database
    var userFromDatabase by remember { mutableStateOf<User?>(null) }

    // State for bottom sheets
    var showChangeNameSheet by remember { mutableStateOf(false) }
    var showChangeEmailSheet by remember { mutableStateOf(false) }

    // Load user data from database
    LaunchedEffect(currentUser?.id) {
        currentUser?.id?.let { userId ->
            val result = databaseService.getUserById(userId)
            if (result.isSuccess) {
                userFromDatabase = result.getOrNull()
            }
        }
    }

    // Handle null user case
    if (currentUser == null) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Vui lòng đăng nhập để xem profile",
                style = MaterialTheme.typography.bodyLarge
            )
        }
        return
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag Handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(
                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(2.dp)
                )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Avatar Section
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(
                    BlueOcean.copy(alpha = 0.1f),
                    shape = CircleShape
                )
                .border(
                    width = 3.dp,
                    color = BlueOcean,
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                painter = painterResource(id = R.drawable.user),
                contentDescription = "Profile Avatar",
                modifier = Modifier.size(50.dp),
                tint = BlueOcean
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Name - prioritize database name over metadata
        val displayName = userFromDatabase?.name?.takeIf { it.isNotBlank() }
            ?: (currentUser.userMetadata?.get("display_name") as? String)
            ?: "User"
        Text(
            text = displayName,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        // User Email
        Text(
            text = currentUser.email ?: "No email",
            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Edit Profile Button - Primary Action
            Button(
                onClick = {
                    showChangeNameSheet = true
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueOcean,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(16.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_setting),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Sửa thông tin",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }

            // Share Button - Secondary Action
            Button(
                onClick = {
                    // Share user ID functionality
                    currentUser?.id?.let { userId ->
                        // Create a shorter, user-friendly ID from the UUID
                        val shortId = userId.take(8).uppercase()
                        clipboardManager.setText(AnnotatedString(shortId))
                        Toast.makeText(
                            context,
                            "ID của bạn đã được sao chép: $shortId",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = MaterialTheme.colorScheme.onSurface
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Chia sẻ",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Settings Section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 0.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
            ),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Settings Header
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_setting),
                        contentDescription = "",
                        modifier = Modifier.size(24.dp),
                        tint = BlueOcean
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Text(
                        text = "Cài đặt",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }

                // Settings buttons
                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
            // Change Email Button
            SettingButton(
                title = "Thay đổi địa chỉ Email",
                icon = R.drawable.ic_email,
                onClick = {
                    showChangeEmailSheet = true
                }
            )

            // Sign Out Button
            SettingButton(
                title = "Đăng xuất",
                icon = R.drawable.ic_log_out,
                onClick = {
                    scope.launch {
                        val result = authViewModel.signOut()
                        if (result.isSuccess) {
                            logOut()
                        }
                    }
                }
            )

            // Delete Account Button
            SettingButton(
                title = "Xóa tài khoản",
                icon = R.drawable.ic_delele,
                onClick = {
                    scope.launch {
                        val result = authService.deleteAccount()
                        if (result.isSuccess) {
                            navController.navigate("welcome")
                        }
                    }
                },
                isDestructive = true
            )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }

    // Change Name Bottom Sheet
    if (showChangeNameSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChangeNameSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ChangeNameBottomSheet(
                authService = authService,
                sheetStateChangeName = rememberModalBottomSheetState(),
                onSheetClosed = {
                    showChangeNameSheet = false
                    // Refresh user data after name change
                    scope.launch {
                        currentUser?.id?.let { userId ->
                            val result = databaseService.getUserById(userId)
                            if (result.isSuccess) {
                                userFromDatabase = result.getOrNull()
                            }
                        }
                    }
                }
            )
        }
    }

    // Change Email Bottom Sheet
    if (showChangeEmailSheet) {
        ModalBottomSheet(
            onDismissRequest = { showChangeEmailSheet = false },
            containerColor = MaterialTheme.colorScheme.surface
        ) {
            ChangeEmailBottomSheet(
                authService = authService,
                email = currentUser?.email
            )
        }
    }
}

@Composable
fun SettingButton(
    title: String,
    icon: Int,
    onClick: () -> Unit,
    isDestructive: Boolean = false
) {
    val contentColor = if (isDestructive) Color.Red else MaterialTheme.colorScheme.onSurface
    
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = contentColor
        ),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(
            1.dp, 
            if (isDestructive) Color.Red.copy(alpha = 0.3f) 
            else MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    painter = painterResource(id = icon),
                    contentDescription = "",
                    modifier = Modifier.size(24.dp),
                    tint = contentColor
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                    color = contentColor
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.ic_next),
                contentDescription = "",
                modifier = Modifier.size(20.dp),
                tint = contentColor.copy(alpha = 0.6f)
            )
        }
    }
}

// Preview cho ProfileBottomSheet
@Preview(showBackground = true)
@Composable
fun ProfileBottomSheetPreview() {
    MyLocketTheme {
        val authService = SupabaseAuthService()
        val authViewModel = AuthViewModel()
        val navController = androidx.navigation.compose.rememberNavController()
        ProfileBottomSheet(
            authService = authService,
            navController = navController,
            authViewModel = authViewModel,
            logOut = { /* Preview only */ }
        )
    }
}
