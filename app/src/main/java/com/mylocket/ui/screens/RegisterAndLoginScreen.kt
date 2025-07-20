package com.mylocket.ui.screens

import android.util.Patterns
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mylocket.R
import com.mylocket.ui.theme.*
import com.mylocket.ui.theme.BlueOcean

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAndLoginScreen(
    navController: NavController,
    registerOrLogin : String?,
){
    var email by remember {
        mutableStateOf("")
    }

    val isEmailValid = remember(email) { Patterns.EMAIL_ADDRESS.matcher(email).matches() }



    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.SpaceBetween,
    ){
        IconButton(
            onClick = { navController.popBackStack() },
            modifier = Modifier
                .padding(start = 20.dp, top = 100.dp)
                .size(50.dp)
                .clip(CircleShape),
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = MaterialTheme.colorScheme.onTertiary,
                contentColor = MaterialTheme.colorScheme.secondary
            )
        ) {
            Icon(
                painter = painterResource(id = R.drawable.back) ,
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }



        Column(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Email của bạn là gì?",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                modifier = Modifier
                    .padding(start = 30.dp)

            )

            Spacer(modifier = Modifier.height(16.dp))

            TextField(
                value = email,
                onValueChange = { input ->
                    run {
                        email = input
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp)
                    .clip(shape = RoundedCornerShape(10.dp)),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.onTertiary,
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.tertiary, // Màu con trỏ
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary
                ),
                placeholder = {
                    Text(text = "Địa chỉ email")
                },
                singleLine = true,
            )


        }
        Column(
            modifier = Modifier
                .padding(start = 10.dp, end = 10.dp, bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ){
            Text(
                text = "Bằng cách nhấn vào nút Tiếp tục, bạn đồng ý với chúng tôi",
                color = Color(0xFF8D8A8B),
                style = MaterialTheme.typography.bodyMedium
            )
            Row {
                Text(
                    text = "Điều khoản dịch vụ",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = " và ",
                    color = Color(0xFF8D8A8B),
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = "Chính sách quyền riêng tư",
                    color = Color.White,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    if (registerOrLogin == "register"){
                        navController.navigate("choosePassword/{$email}")
                    }else if (registerOrLogin == "login"){
                        navController.navigate("enterPassword/{$email}")
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    contentColor = if (isEmailValid) Color.White else Color(0xFF757575),
                    containerColor = if (isEmailValid) BlueOcean else Color(0xFFE0E0E0),
                    disabledContainerColor = if (isEmailValid) BlueOcean else Color(0xFFE0E0E0)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(start = 10.dp, end = 10.dp),
                enabled = isEmailValid,


            ) {
                Text(
                    text = "Tiếp tục",
                    color = if (isEmailValid) Color.Black else Color(0xFF4E4E50),
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                    contentDescription = null,
                    tint = if (isEmailValid) Color.Black else Color(0xFF4E4E50)
                    )
            }
        }

    }
}


