package com.mylocket.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
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
import com.mylocket.viewmodel.UserViewModel
import com.mylocket.data.User
import com.mylocket.ui.bottomsheets.FriendBottomSheet
import com.mylocket.ui.bottomsheets.ProfileBottomSheet
import com.mylocket.ui.components.CameraComponent
import com.mylocket.ui.components.ImageComponent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    authService: SupabaseAuthService
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
                else -> ImageComponent()
            }
        }


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 75.dp)
                .background(Color.Transparent),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    showBottomSheet = true
                },
                modifier = Modifier
                    .size(50.dp)
                    .clip(shape = CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.user),
                    contentDescription = null,
                    modifier = Modifier.size(32.dp)
                )
            }

            Button(
                onClick = {
                    if (pagerState.currentPage == 0){
                        showBottomSheetFriend = true
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.friend),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )

                Spacer(modifier = Modifier.width(5.dp))

                Text(
                    text = if(pagerState.currentPage == 0) "1 Bạn bè" else "Tất cả bạn bè",
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = FontWeight.SemiBold
                )
            }

            IconButton(
                onClick = { navController.navigate("chat") },
                modifier = Modifier
                    .size(50.dp)
                    .clip(shape = CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.onSurface,
                    contentColor = Color.White
                )
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.chat),
                    contentDescription = null,
                    modifier = Modifier.size(30.dp)
                )
            }
        }
        if (showBottomSheet) {
            ModalBottomSheet(
                modifier = Modifier.fillMaxSize(),
                sheetState = sheetState,
                onDismissRequest = { showBottomSheet = false },
                containerColor = MaterialTheme.colorScheme.onBackground

            ) {
                ProfileBottomSheet(
                    authService,
                    navController,
                    logOut = {
//                        scope.launch { sheetState.hide() }.invokeOnCompletion {
//                            if (!sheetState.isVisible) {
//                                showBottomSheet = false
//
//                            }
//                        }

                        scope.launch {
                            // Điều hướng đến màn hình welcome
                            navController.navigate("welcome") {
                                popUpTo("home") { inclusive = true }
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
                containerColor = MaterialTheme.colorScheme.onBackground

            ) {
                FriendBottomSheet(authService)
            }
        }
    }
}

