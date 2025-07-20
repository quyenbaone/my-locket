package com.mylocket

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.mylocket.navigation.MyLocketNavHost
import com.mylocket.ui.screens.ChatScreen
import com.mylocket.ui.screens.HomeScreen
import com.mylocket.ui.theme.MyLocketTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()
        setContent {
            MyLocketTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { paddingValues ->
                    MyLocketNavHost()
                }

            }
        }
    }
}
