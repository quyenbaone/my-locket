package com.mylocket.ui.screens

import android.content.ContentValues.TAG
import android.util.Log
import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowForward
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mylocket.service.SupabaseAuthService
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.mylocket.R
import com.mylocket.ui.theme.BlueOcean
import kotlin.coroutines.coroutineContext


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChoosePasswordScreen(
    authService: SupabaseAuthService,
    navController: NavController,
    email: String?
){
    val scope = rememberCoroutineScope()

    var password by remember {
        mutableStateOf("")
    }

    val isTruePassword = remember (password) {
        password.length >= 8
    }

    var passwordVisible by remember { mutableStateOf(false) }

    val emailFix = email?.substring(1,email.length-1)

    var context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
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
                painter = painterResource(id = R.drawable.back),
                contentDescription = null
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "Chọn một mật khẩu",
                color = MaterialTheme.colorScheme.secondary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                fontFamily = FontFamily.SansSerif,

            )

            OutlinedTextField(
                value = password,
                onValueChange = {input ->
                    run {
                        password = input
                    }
                },
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = MaterialTheme.colorScheme.onTertiary,
                    focusedTextColor = MaterialTheme.colorScheme.secondary,
                    cursorColor = MaterialTheme.colorScheme.secondary,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    unfocusedTextColor = MaterialTheme.colorScheme.secondary
                ),
                placeholder = {
                    Text(text = "Mật khẩu")
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 30.dp, end = 30.dp, top = 15.dp, bottom = 15.dp)
                    .clip(shape = RoundedCornerShape(10.dp)),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {

                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(painter = if (passwordVisible) painterResource(id = R.drawable.ic_visibility_off) else painterResource(id = R.drawable.ic_visibility), contentDescription = "")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)

            )

            Row(

            ) {
                Text(
                    text = "Mật khẩu phải có ít nhất ",
                    color = MaterialTheme.colorScheme.secondary
                )
                Text(
                    text = "8 ký tự",
                    color = BlueOcean
                )
            }
        }

        Button(
            onClick = {

                if (emailFix != null) {
                    scope.launch {
                        val result = authService.signUp(emailFix, password)
                        if (result.isSuccess) {
                            navController.navigate("chooseName")
                        } else {
                            // Handle error - you might want to show a toast or error message
                            Log.e("ChoosePassword", "Sign up failed: ${result.exceptionOrNull()}")
                        }
                    }
                }




            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiary,
                contentColor = Color.Black,
                disabledContainerColor = MaterialTheme.colorScheme.onTertiary,
                disabledContentColor = Color(0xFF4E4E50)
            ),

            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(start = 20.dp, end = 20.dp, bottom = 30.dp),
            enabled = isTruePassword
        ) {
           Text(
               text = "Tiếp tục",
               fontWeight = FontWeight.Bold
           )
            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                imageVector = Icons.AutoMirrored.Rounded.ArrowForward,
                contentDescription = null,
                tint = if (isTruePassword) Color.Black else Color(0xFF4E4E50)
            )
        }
    }
}

//@Preview
//@Composable
//fun PreviewChoosePasswordScreen(){
//    ChoosePasswordScreen(email = "", navController = NavController)
//}
