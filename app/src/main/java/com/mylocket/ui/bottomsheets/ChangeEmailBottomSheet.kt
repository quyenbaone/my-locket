package com.mylocket.ui.bottomsheets

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.mylocket.service.SupabaseAuthService
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangeEmailBottomSheet(
    authService: SupabaseAuthService,
    email: String?
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val user = authService.getCurrentUser()

    // Handle null user case
    if (user == null) {
        Text("Please log in to change email")
        return
    }

    var newEmail by remember { mutableStateOf(email ?: "") }

    // Simplified email change UI for Supabase
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Change Email",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Text(
            text = "Current email: ${user!!.email ?: "No email"}",
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        OutlinedTextField(
            value = newEmail,
            onValueChange = { newEmail = it },
            label = { Text("New Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        
        Text(
            text = "Note: Email change requires server-side implementation with Supabase Edge Functions.",
            style = MaterialTheme.typography.bodySmall,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 24.dp)
        )
        
        Button(
            onClick = {
                scope.launch {
                    val result = authService.updateEmail(newEmail)
                    if (result.isSuccess) {
                        Toast.makeText(context, "Email update request sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Email update failed: ${result.exceptionOrNull()?.message}", Toast.LENGTH_LONG).show()
                    }
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = newEmail.isNotEmpty() && newEmail != user!!.email
        ) {
            Text("Request Email Change")
        }
    }
}
