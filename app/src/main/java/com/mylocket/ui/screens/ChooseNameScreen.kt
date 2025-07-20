package com.mylocket.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mylocket.service.SupabaseAuthService
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.mylocket.ui.theme.BlueOcean

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

    Column(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            Text(
                text = "Tên bạn là gì?",
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.SansSerif
            )

            TextField(
                value = firstname,
                onValueChange = {input->
                    run{
                        firstname = input
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 15.dp, bottom = 5.dp)
                    .clip(shape = RoundedCornerShape(10.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = Color.White
                ),
                placeholder = {
                    Text(text = "Tên")
                },
                singleLine = true
            )
            TextField(
                value = lastname,
                onValueChange = {input->
                    run{
                        lastname = input
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 5.dp, bottom = 15.dp)
                    .clip(shape = RoundedCornerShape(10.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    focusedTextColor = Color.White,
                    cursorColor = Color.White,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = Color.White
                ),
                placeholder = {
                    Text(text = "Họ")
                },
                singleLine = true
            )
        }

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 30.dp, end = 30.dp, bottom = 30.dp),
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
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueOcean,
                contentColor = Color.Black,
                disabledContainerColor = MaterialTheme.colorScheme.onSurface,
                disabledContentColor = Color(0xFF4E4E50)
            ),
            enabled = isTrueName
        ) {
            Text(
                text = "Tiếp tục",
                fontWeight = FontWeight.Bold
            )
        }
    }
}
