package com.mylocket.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.DrawStyle
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
// Firebase imports removed - using Supabase now
import com.mylocket.R
import com.mylocket.ui.theme.*
import com.mylocket.ui.theme.BlueOcean

@Composable
fun WelcomeScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {

        Spacer(modifier = Modifier.size(50.dp))

        Image(
            painter = painterResource(id = R.drawable.welcome),
            contentDescription = null,
            modifier = Modifier.size(450.dp)
        )

        TitleComponent()
        Spacer(modifier = Modifier.size(4.dp))
        ActionComponent(onNavigateToRegister, onNavigateToLogin)
        Spacer(modifier = Modifier.size(20.dp))

    }
}

@Composable
fun TitleComponent() {
    Column (
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .clip(shape = RoundedCornerShape(12.dp)),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Locket",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,
                color = MaterialTheme.colorScheme.secondary,
                )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Column (
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            Text(
                text = "Ảnh trực tiếp từ bạn bè,",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB7B7B7),
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "ngay trên màn hình chính",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFB7B7B7),
            )
        }
    }
}

@Composable
fun ActionComponent(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ){
        Button(
            onClick = { onNavigateToRegister() },
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueOcean,
                contentColor = Color.White
            ),
            modifier = Modifier.height(60.dp)
        ) {
            Text(
                text = "Tạo một tài khoản",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.SemiBold,
                fontSize = 20.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Đăng nhập",
            modifier = Modifier.clickable { onNavigateToLogin() },
            color = BlueOcean,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

// Preview cho WelcomeScreen
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    MyLocketTheme {
        WelcomeScreen(
            onNavigateToRegister = { /* Preview only */ },
            onNavigateToLogin = { /* Preview only */ }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TitleComponentPreview() {
    MyLocketTheme {
        TitleComponent()
    }
}

@Preview(showBackground = true)
@Composable
fun ActionComponentPreview() {
    MyLocketTheme {
        ActionComponent(
            onNavigateToRegister = { /* Preview only */ },
            onNavigateToLogin = { /* Preview only */ }
        )
    }
}
