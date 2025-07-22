package com.mylocket.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mylocket.R
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.MyLocketTheme

@Composable
fun WelcomeScreen(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Image(
            painter = painterResource(id = R.drawable.welcome),
            contentDescription = null,
            modifier = Modifier.size(320.dp) // 👈 nhỏ lại
        )

        Spacer(modifier = Modifier.height(24.dp))

        TitleComponent()

        Spacer(modifier = Modifier.height(36.dp))

        ActionComponent(
            onNavigateToRegister = onNavigateToRegister,
            onNavigateToLogin = onNavigateToLogin
        )

        Spacer(modifier = Modifier.height(36.dp))
    }
}

@Composable
fun TitleComponent() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Locket",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.SansSerif,
            color = MaterialTheme.colorScheme.secondary
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Ảnh trực tiếp từ bạn bè,",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB7B7B7),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = "ngay trên màn hình chính",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFB7B7B7),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ActionComponent(
    onNavigateToRegister: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Button(
            onClick = { onNavigateToRegister() },
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueOcean,
                contentColor = Color.White
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(54.dp)
        ) {
            Text(
                text = "Tạo một tài khoản",
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Đăng nhập",
            modifier = Modifier.clickable { onNavigateToLogin() },
            color = BlueOcean,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp
        )
    }
}

// ========== PREVIEW ==========

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    MyLocketTheme {
        WelcomeScreen(
            onNavigateToRegister = {},
            onNavigateToLogin = {}
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
            onNavigateToRegister = {},
            onNavigateToLogin = {}
        )
    }
}
