package com.mylocket.ui.bottomsheets

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import com.mylocket.service.SupabaseAuthService
import androidx.compose.runtime.rememberCoroutineScope
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.MyLocketTheme
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun  ChangeNameBottomSheet(
    authService: SupabaseAuthService,
    sheetStateChangeName: SheetState,
    onSheetClosed: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val user = authService.getCurrentUser()

    // Handle null user case
    if (user == null) {
        Text("Please log in to change name")
        return
    }

    val displayName = user.userMetadata?.get("display_name") as? String
    var firstname by remember {
        mutableStateOf(displayName?.split(" ", limit = 2)?.getOrNull(1))
    }

    var lastname by remember {
        mutableStateOf(displayName?.split(" ", limit = 2)?.getOrNull(0))
    }

    var enabled = (lastname?.isNotEmpty() == true) && (firstname?.isNotEmpty() == true)

    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ){
        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Sửa tên của bạn",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            lastname?.let {
                TextField(
                    value = it,
                    onValueChange = { input -> run{
                        lastname = input
                    }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .border(
                            width = 3.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray
                    ),
                    placeholder = {
                        Text(
                            text = "Tên",
                            textAlign = TextAlign.Center
                        )
                    },
                    singleLine = true,

                    )
            }
            firstname?.let {
                TextField(
                    value = it,
                    onValueChange = { input -> run{
                        firstname = input
                    }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 30.dp, end = 30.dp, top = 10.dp, bottom = 10.dp)
                        .clip(shape = RoundedCornerShape(10.dp))
                        .border(
                            width = 3.dp,
                            color = Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        ),
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = MaterialTheme.colorScheme.onSecondary,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        cursorColor = Color.White,
                        unfocusedPlaceholderColor = Color.Gray,
                        focusedPlaceholderColor = Color.Gray
                    ),
                    placeholder = {
                        Text(
                            text = "Họ",
                            textAlign = TextAlign.Center
                        )
                    },
                    singleLine = true,

                    )
            }
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, bottom = 100.dp)
                .height(56.dp),
            onClick = {
                scope.launch {
                    val result = authService.updateProfile(lastname + " " + firstname)
                    if (result.isSuccess) {
                        Toast.makeText(context, "Cập nhật tên thành công", Toast.LENGTH_SHORT).show()
                        onSheetClosed()
                    } else {
                        Toast.makeText(context, "Thêm tên người dùng thất bại", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueOcean,
                contentColor = Color.White,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f),
                disabledContentColor = Color.White.copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(16.dp),
            enabled = enabled,
            elevation = ButtonDefaults.buttonElevation(
                defaultElevation = if (enabled) 4.dp else 0.dp
            )
        ) {
            Text(
                text = "Lưu",
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp
            )
        }
    }
}

// Preview cho ChangeNameBottomSheet
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ChangeNameBottomSheetPreview() {
    MyLocketTheme {
        val authService = SupabaseAuthService()
        val sheetState = androidx.compose.material3.rememberModalBottomSheetState()
        ChangeNameBottomSheet(
            authService = authService,
            sheetStateChangeName = sheetState,
            onSheetClosed = { /* Preview only */ }
        )
    }
}
