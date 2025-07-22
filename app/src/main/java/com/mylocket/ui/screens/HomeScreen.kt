package com.mylocket.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.mylocket.R
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mylocket.service.SupabaseAuthService
import com.mylocket.viewmodel.AuthViewModel
import com.mylocket.viewmodel.UserViewModel
import com.mylocket.data.User
import com.mylocket.ui.bottomsheets.FriendBottomSheet
import com.mylocket.ui.bottomsheets.ProfileBottomSheet
import com.mylocket.ui.components.CameraComponent
import com.mylocket.ui.components.ImageComponent
import com.mylocket.ui.components.PostsComponent
import com.mylocket.ui.theme.BlueOcean
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authService: SupabaseAuthService,
    authViewModel: AuthViewModel
) {
    //Pager
    val pagerState =  rememberPagerState (initialPage = 0, pageCount = {10})

    //bottom sheet
    var showBottomSheet by remember {
        mutableStateOf(false)
    }

    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    var showBottomSheetFriend by remember {
        mutableStateOf(false)
    }

    val sheetFriendState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )

    val scope = rememberCoroutineScope()

    val userAuthentication = authService.getCurrentUser()
    val userViewModel: UserViewModel = viewModel()

    // Handle null user case
    if (userAuthentication != null) {
        val displayName = userAuthentication.userMetadata?.get("display_name") as? String ?: ""
        val newUser = User(id = userAuthentication.id, name = displayName, email = userAuthentication.email ?: "", photo = userAuthentication.userMetadata?.get("avatar_url") as? String)
        userViewModel.addUser(newUser)
    }

    Box(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) {page ->
            when (page){
                0 -> CameraComponent(navController = navController)
                else -> {
                    // Show posts for the authenticated user
                    if (userAuthentication != null) {
                        PostsComponent(userId = userAuthentication.id)
                    } else {
                        ImageComponent() // Fallback to static component
                    }
                }
            }
        }


        // Enhanced top navigation - chỉ hiển thị khi không phải camera
        AnimatedVisibility(
            visible = pagerState.currentPage != 0,
            enter = fadeIn(animationSpec = tween(300))
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 60.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile button
                IconButton(
                    onClick = { showBottomSheet = true },
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.user),
                        contentDescription = "Profile",
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }
                // Friends button - improved styling
                Button(
                    onClick = { showBottomSheetFriend = true },
                    colors = ButtonDefaults.buttonColors(
                        Color.Black.copy(alpha = 0.6f),
                        contentColor = Color.White

                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .height(44.dp)
                        .widthIn(min = 80.dp),
                    elevation = ButtonDefaults.buttonElevation(
                        defaultElevation = 4.dp,
                        pressedElevation = 8.dp
                    )
                ) {
                    Text(
                        text = "Bạn bè",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                // Message button - NEW
                IconButton(
                    onClick = {
                        navController.navigate("chat")
                    },
                    modifier = Modifier
                        .size(56.dp)
                        .background(
                            Color.Black.copy(alpha = 0.6f),
                            CircleShape
                        )
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.chat),
                        contentDescription = "Messages",
                        modifier = Modifier.size(50.dp),
                        tint = Color.White
                    )
                }


            }
        }



        if (showBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxSize(),
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.White

            ) {
                ProfileBottomSheet(
                    authService,
                    navController,
                    authViewModel = authViewModel,
                    logOut = {
                        scope.launch {
                            // Thực hiện logout thông qua AuthViewModel
                            val result = authViewModel.signOut()
                            if (result.isSuccess) {
                                // Đóng bottom sheet
                                showBottomSheet = false
                                // Navigation sẽ được xử lý tự động bởi AuthViewModel
                            }
                        }
                    }
                )
            }
        }
        if (showBottomSheetFriend) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxSize(),
                sheetState = sheetFriendState,
                onDismissRequest = { showBottomSheetFriend = false },
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = Color.White

            ) {
                FriendBottomSheet(authService)
            }
        }
    }
}

// Preview cho HomeScreen
@Preview(showBackground = true, showSystemUi = true, name = "Home Screen - Light")
@Composable
fun HomeScreenPreview() {
    com.mylocket.ui.theme.MyLocketTheme {
        val navController = androidx.navigation.compose.rememberNavController()
        val authService = com.mylocket.service.SupabaseAuthService()
        val authViewModel = com.mylocket.viewmodel.AuthViewModel()
        HomeScreen(
            navController = navController,
            authService = authService,
            authViewModel = authViewModel
        )
    }
}

// Preview cho HomeScreen với Dark Theme
@Preview(showBackground = true, showSystemUi = true, name = "Home Screen - Dark")
@Composable
fun HomeScreenDarkPreview() {
    com.mylocket.ui.theme.MyLocketTheme(darkTheme = true) {
        val navController = androidx.navigation.compose.rememberNavController()
        val authService = com.mylocket.service.SupabaseAuthService()
        val authViewModel = com.mylocket.viewmodel.AuthViewModel()
        HomeScreen(
            navController = navController,
            authService = authService,
            authViewModel = authViewModel
        )
    }
}