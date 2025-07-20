package com.mylocket.navigation

import android.annotation.SuppressLint
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mylocket.service.SupabaseAuthService
import com.mylocket.ui.screens.ChatScreen
import com.mylocket.ui.screens.ChooseNameScreen
import com.mylocket.ui.screens.ChoosePasswordScreen
import com.mylocket.ui.screens.EnterPasswordScreen
import com.mylocket.ui.screens.HomeScreen
import com.mylocket.ui.screens.RegisterAndLoginScreen
import com.mylocket.ui.screens.SendingScreen
import com.mylocket.ui.screens.WelcomeScreen
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@SuppressLint("RestrictedApi")
@Composable
fun MyLocketNavHost() {
    val navController = rememberNavController()

    val authService = SupabaseAuthService()

    val currentUser = authService.getCurrentUser()

    val startDestination = "welcome" // Always start with welcome for now

    NavHost(navController = navController, startDestination = startDestination) {
        composable("welcome") {
            WelcomeScreen(
                onNavigateToRegister = {
                    navController.navigate(
                        route = "registerAndLogin/register"
                    )
                },
                onNavigateToLogin = {
                    navController.navigate(
                        route = "registerAndLogin/login"
                    )
                },
            )
        }
        composable("registerAndLogin/{registerOrLogin}") { backStackEntry ->
            val registerOrLogin = backStackEntry.arguments?.getString("registerOrLogin")
            RegisterAndLoginScreen(
                navController = navController,
                registerOrLogin = registerOrLogin
            )
        }
        composable("chooseName") {
            ChooseNameScreen(
                navController = navController,
                authService = authService
            )
        }

        composable("choosePassword/{email}") { backStackEntry ->
            val email = backStackEntry.arguments?.getString("email")
            ChoosePasswordScreen(
                authService = authService,
                navController = navController,
                email = email
            )
        }
        composable("enterPassword/{email}") { navBackStackEntry ->
            val email = navBackStackEntry.arguments?.getString("email")
            EnterPasswordScreen(navController = navController, authService = authService, email = email)
        }
        composable("home") { HomeScreen(navController = navController, authService = authService) }
        composable("chat") { ChatScreen(navController = navController) }
        composable("sending/{imgPath}"){navBackStackEntry ->
            val encodedPath = navBackStackEntry.arguments?.getString("imgPath")
            val imgPath = encodedPath?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            SendingScreen(navController= navController, imagePath = imgPath, authService = authService)
        }
    }
}