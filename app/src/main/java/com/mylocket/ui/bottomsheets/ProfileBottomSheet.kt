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
import androidx.compose.foundation.clickable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.IconButton

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

    // State for inline editing
    var isEditingName by remember { mutableStateOf(false) }
    var editingName by remember { mutableStateOf("") }
    var isEditingEmail by remember { mutableStateOf(false) }
    var editingEmail by remember { mutableStateOf("") }

    // State for updated user info
    var updatedName by remember { mutableStateOf<String?>(null) }
    var updatedEmail by remember { mutableStateOf<String?>(null) }

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
            .background(Color.Black)
            .padding(horizontal = 24.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Drag Handle
        Box(
            modifier = Modifier
                .width(40.dp)
                .height(4.dp)
                .background(
                    color = Color.White.copy(alpha = 0.4f),
                    shape = RoundedCornerShape(2.dp)
                )
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Profile Avatar Section
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    Color.Gray.copy(alpha = 0.2f),
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
                modifier = Modifier.size(60.dp),
                tint = Color.White.copy(alpha = 0.8f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // User Name - prioritize updated name, then database name, then metadata
        val displayName = updatedName?.takeIf { it.isNotBlank() }
            ?: userFromDatabase?.name?.takeIf { it.isNotBlank() }
            ?: (currentUser.userMetadata?.get("display_name") as? String)
            ?: "User"

        // Initialize editing name when starting to edit
        LaunchedEffect(isEditingName) {
            if (isEditingName && editingName.isEmpty()) {
                editingName = displayName
            }
        }

        if (isEditingName) {
            // Inline editing mode
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = editingName,
                    onValueChange = { editingName = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BlueOcean,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                )

                // Save button
                IconButton(
                    onClick = {
                        if (editingName.isNotBlank()) {
                            // Update UI immediately for better UX
                            updatedName = editingName.trim()
                            isEditingName = false

                            // Save to database
                            scope.launch {
                                val result = databaseService.updateUserName(currentUser.id, editingName.trim())
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Cập nhật tên thành công", Toast.LENGTH_SHORT).show()

                                    // Refresh user data from database to get the latest info
                                    val userResult = databaseService.getUserById(currentUser.id)
                                    if (userResult.isSuccess) {
                                        userFromDatabase = userResult.getOrNull()
                                        // Clear the temporary updated name since we now have fresh data
                                        updatedName = null
                                    }
                                } else {
                                    // If database save fails, revert UI change
                                    updatedName = null
                                    Toast.makeText(context, "Lỗi lưu tên vào database", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Lưu",
                        tint = BlueOcean,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Cancel button
                IconButton(
                    onClick = {
                        isEditingName = false
                        editingName = ""
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Hủy",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        } else {
            // Display mode with click to edit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEditingName = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayName,
                    color = Color.White,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "Sửa tên",
                    tint = Color.White.copy(alpha = 0.6f),
                    modifier = Modifier.size(20.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // User Email - with inline editing
        val displayEmail = updatedEmail?.takeIf { it.isNotBlank() }
            ?: currentUser.email
            ?: "No email"

        // Initialize editing email when starting to edit
        LaunchedEffect(isEditingEmail) {
            if (isEditingEmail && editingEmail.isEmpty()) {
                editingEmail = displayEmail
            }
        }

        if (isEditingEmail) {
            // Inline editing mode for email
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = editingEmail,
                    onValueChange = { editingEmail = it },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = BlueOcean,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.5f),
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Center
                    )
                )

                // Save button
                IconButton(
                    onClick = {
                        if (editingEmail.isNotBlank() && android.util.Patterns.EMAIL_ADDRESS.matcher(editingEmail).matches()) {
                            // Update UI immediately for better UX
                            updatedEmail = editingEmail.trim()
                            isEditingEmail = false

                            // Save to database
                            scope.launch {
                                val result = databaseService.updateUserEmail(currentUser.id, editingEmail.trim())
                                if (result.isSuccess) {
                                    Toast.makeText(context, "Cập nhật email thành công", Toast.LENGTH_SHORT).show()

                                    // Refresh user data from database to get the latest info
                                    val userResult = databaseService.getUserById(currentUser.id)
                                    if (userResult.isSuccess) {
                                        userFromDatabase = userResult.getOrNull()
                                        // Clear the temporary updated email since we now have fresh data
                                        updatedEmail = null
                                    }
                                } else {
                                    // If database save fails, revert UI change
                                    updatedEmail = null
                                    Toast.makeText(context, "Lỗi lưu email vào database", Toast.LENGTH_SHORT).show()
                                }
                            }
                        } else {
                            Toast.makeText(context, "Email không hợp lệ", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_check),
                        contentDescription = "Lưu",
                        tint = BlueOcean,
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Cancel button
                IconButton(
                    onClick = {
                        isEditingEmail = false
                        editingEmail = ""
                    },
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_close),
                        contentDescription = "Hủy",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        } else {
            // Display mode with click to edit
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isEditingEmail = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = displayEmail,
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "Sửa email",
                    tint = Color.White.copy(alpha = 0.4f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Action Buttons
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {

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
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Gray.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.2f)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_send),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
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

            // Sign Out Button
            Button(
                onClick = {
                    scope.launch {
                        val result = authViewModel.signOut()
                        if (result.isSuccess) {
                            logOut()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Red.copy(alpha = 0.2f),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(1.dp, Color.Red.copy(alpha = 0.3f)),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 0.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_log_out),
                        contentDescription = null,
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đăng xuất",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
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
