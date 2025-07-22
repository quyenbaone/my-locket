package com.mylocket.navigation

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mylocket.service.SupabaseAuthService
import com.mylocket.service.SupabaseDatabaseService
import com.mylocket.service.ChatService
import com.mylocket.ui.screens.ChatScreen
import com.mylocket.ui.screens.ChatDetailScreen
import com.mylocket.ui.screens.ChooseNameScreen
import com.mylocket.ui.screens.ChoosePasswordScreen
import com.mylocket.ui.screens.CommentsScreen
import com.mylocket.ui.screens.EnterPasswordScreen
import com.mylocket.ui.screens.HomeScreen
import com.mylocket.ui.screens.RegisterAndLoginScreen
import com.mylocket.ui.screens.SendingScreen
import com.mylocket.ui.screens.WelcomeScreen
import com.mylocket.viewmodel.AuthState
import com.mylocket.viewmodel.AuthViewModel
import java.net.URLDecoder
import java.nio.charset.StandardCharsets

@SuppressLint("RestrictedApi")
@Composable
fun MyLocketNavHost() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authService = SupabaseAuthService()

    val authState by authViewModel.authState.collectAsState()
    val currentUser by authViewModel.currentUser.collectAsState()

    // Show loading screen while checking auth state
    when (authState) {
        is AuthState.Loading -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
            return
        }
        is AuthState.Authenticated -> {
            // User is logged in, start with home
            val startDestination = "home"
            MyLocketNavigation(navController, authService, authViewModel, startDestination)
        }
        is AuthState.NotAuthenticated -> {
            // User is not logged in, start with welcome
            val startDestination = "welcome"
            MyLocketNavigation(navController, authService, authViewModel, startDestination)
        }
    }
}

@Composable
private fun MyLocketNavigation(
    navController: NavHostController,
    authService: SupabaseAuthService,
    authViewModel: AuthViewModel,
    startDestination: String
) {
    val context = LocalContext.current

    // Initialize services
    val databaseService = SupabaseDatabaseService()
    val chatService = ChatService(databaseService, context)
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
            val email = navBackStackEntry.arguments?.getString("email") ?: ""
            EnterPasswordScreen(navController = navController, authService = authService, email = email)
        }
        composable("home") {
            HomeScreen(
                navController = navController,
                authService = authService,
                authViewModel = authViewModel
            )
        }
        composable("chat") {
            ChatScreen(
                navController = navController,
                chatService = chatService
            )
        }
        composable("chat_detail/{friendId}/{friendName}") { backStackEntry ->
            val friendId = backStackEntry.arguments?.getString("friendId") ?: ""
            val friendName = backStackEntry.arguments?.getString("friendName") ?: ""
            ChatDetailScreen(
                navController = navController,
                friendId = friendId,
                friendName = friendName,
                chatService = chatService
            )
        }
        composable("sending/{imgPath}"){navBackStackEntry ->
            val encodedPath = navBackStackEntry.arguments?.getString("imgPath")
            val imgPath = encodedPath?.let { URLDecoder.decode(it, StandardCharsets.UTF_8.toString()) }
            SendingScreen(navController= navController, imagePath = imgPath, authService = authService)
        }
        composable("comments/{postId}/{currentUserId}") { backStackEntry ->
            val postId = backStackEntry.arguments?.getString("postId") ?: ""
            val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
            CommentsScreen(
                navController = navController,
                postId = postId,
                currentUserId = currentUserId
            )
        }
    }
}