package com.mylocket.ui.bottomsheets

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mylocket.service.SupabaseAuthService
import com.mylocket.R
import com.mylocket.viewmodel.FriendViewModel
import com.mylocket.viewmodel.FriendViewModelFactory
import com.mylocket.viewmodel.UserViewModel
import com.mylocket.ui.theme.MyLocketTheme
import com.mylocket.data.Friend
import com.mylocket.data.FriendStatus
import com.mylocket.data.User
import com.mylocket.ui.theme.BlueOcean
import com.mylocket.ui.theme.Grey

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendBottomSheet(
    authService: SupabaseAuthService,
) {

    var searchInput by remember { mutableStateOf("") }

    val currentUser = authService.getCurrentUser()

    // Handle null user case
    if (currentUser == null) {
        Text("Please log in to view friends")
        return
    }

    val friendViewModel: FriendViewModel = viewModel(
        factory = FriendViewModelFactory(currentUser.id)
    )
    val friendList by friendViewModel.friends.collectAsState(emptyList())

    val userViewModel: UserViewModel = viewModel()
    val userList by userViewModel.users.collectAsState(emptyList())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Bạn bè của bạn",
            style = TextStyle(
                color = Color.White,
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.SemiBold
            ),
            modifier = Modifier
                .padding(top = 10.dp, bottom = 10.dp)
        )
        Text(
            text = "${friendList.size} người bạn đã được bổ sung",
            style = TextStyle(
                color = Color.Gray,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        )

        TextField(
            value = searchInput,
            onValueChange = {input->
                run {
                    searchInput = input
                }
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.onSecondary,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White,
                cursorColor = Color.White,
                unfocusedPlaceholderColor = Color.Gray,
                focusedPlaceholderColor = Color.Gray
            ),
            placeholder = {
                Row(
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(painter = painterResource(id = R.drawable.ic_search), contentDescription ="" )

                    Text(
                        text = "Thêm một người bạn mới",
                        style = TextStyle(
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        ),
                    )
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp)
                .clip(shape = RoundedCornerShape(15.dp))
                .border(
                    width = 2.dp,
                    color = Color.Transparent,
                    shape = RoundedCornerShape(10.dp)
                )
        )



        // Search to add Friend
        if (searchInput != ""){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_friend_add), contentDescription = "", tint = Color.Gray)

                Text(
                    text = "Tìm kiếm bạn bè",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }

            userList.map { user->
                if (user.id != currentUser.id && user.email.contains(searchInput) && friendList.none { friend -> friend.id == user.id }) {
                    CustomLine(
                        user = user,
                        onAction = {
                            val newFriend = Friend(
                                id = user.id,
                                name = user.name,
                                email = user.email,
                                photo = user.photo,
                                status = FriendStatus.SENT.toString()
                            )
                            val displayName = currentUser.userMetadata?.get("display_name") as? String ?: ""
                            friendViewModel.addFriend(
                                newFriend,
                                user = User(
                                    currentUser.id,
                                    displayName,
                                    currentUser.email ?: "",
                                    currentUser.userMetadata?.get("avatar_url") as? String
                                )
                            )
                        }
                    )
                }
//                else{
//                    Row(
//                        modifier = Modifier
//                            .fillMaxWidth()
//                            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.Center
//                    ) {
//                        Text(
//                            text = "Không tìm thấy người nào",
//                            style = TextStyle(
//                                color = Color.Gray,
//                                fontSize = 16.sp,
//                                fontWeight = FontWeight.SemiBold
//                            ),
//                            modifier = Modifier.padding(start = 10.dp)
//                        )
//                    }
//                }
            }
        }

        if (friendList.map { it.status== FriendStatus.RECEIVED.toString()}.isNotEmpty()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_friend_add), contentDescription = "", tint = Color.Gray)

                Text(
                    text = "Yêu cầu kết bạn",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }


        friendList.map {item ->
            if (item.status == FriendStatus.RECEIVED.toString()){
                CustomLine(friend = item, onAction = {
                    val displayName = currentUser.userMetadata?.get("display_name") as? String ?: ""
                    friendViewModel.acceptFriend(friend = item, user = User(
                        currentUser.id,
                        displayName,
                        currentUser.email ?: "",
                        currentUser.userMetadata?.get("avatar_url") as? String
                    ))
                })
            }
        }

        if (friendList.map { it.status== FriendStatus.SENT.toString()}.isNotEmpty()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_friend_add), contentDescription = "", tint = Color.Gray)

                Text(
                    text = "Đã gửi lời mời",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }


        friendList.map {item ->
            if (item.status == FriendStatus.SENT.toString()){
                CustomLine(friend = item, onAction = {
                    friendViewModel.deleteFriend(currentUser.id, item.id)
                })
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(painter = painterResource(id = R.drawable.friend), contentDescription = "", tint = Color.Gray)

            Text(
                text = "Bạn bè của bạn",
                style = TextStyle(
                    color = Color.Gray,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        if (friendList.map { it.status== FriendStatus.FRIENDS.toString()}.isEmpty()){
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Hãy thêm bạn bè để chia sẻ khoảnh khắc",
                    style = TextStyle(
                        color = Color.Gray,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    modifier = Modifier.padding(start = 10.dp)
                )
            }
        }

        friendList.map {item ->
            if (item.status == FriendStatus.FRIENDS.toString()){
                CustomLine(friend = item, onAction = {
                    friendViewModel.deleteFriend(currentUser.id, item.id)
                })
            }
        }
    }
}


@Composable
fun CustomLine(
    friend: Friend,
    onAction: () -> Unit
){
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (friend.photo == null) painterResource(id = R.drawable.user) else painterResource(id = R.drawable.img),
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = BlueOcean, shape = CircleShape)
            )

            Text(
                text = friend.name,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        if (friend.status == FriendStatus.FRIENDS.toString()){
            IconButton(
                onClick = onAction,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color.White
                )
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_close), contentDescription = "")
            }
        }else if (friend.status == FriendStatus.SENT.toString()){
            IconButton(
                onClick = onAction,
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape),
                colors = IconButtonDefaults.iconButtonColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = Color.White
                )
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_close), contentDescription = "")
            }
        }else{
            Button(
                onClick = onAction,
                modifier = Modifier.height(40.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BlueOcean,
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
            ) {
                Row (
                    verticalAlignment = Alignment.CenterVertically
                ){
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = "",
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Chấp nhận",
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }


}

@Composable
fun CustomLine(
    user: User,
    onAction: () -> Unit
) {
    Row (
        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .padding(start = 20.dp, end = 20.dp, top = 10.dp, bottom = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ){
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = if (user.photo == null) painterResource(id = R.drawable.user) else painterResource(id = R.drawable.img),
                contentDescription = "",
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .border(width = 2.dp, color = BlueOcean, shape = CircleShape)
            )

            Text(
                text = user.name,
                style = TextStyle(
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(start = 10.dp)
            )
        }

        Button(
            onClick = onAction,
            modifier = Modifier.height(40.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = BlueOcean,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Row (
                verticalAlignment = Alignment.CenterVertically
            ){
                Icon(
                    painter = painterResource(id = R.drawable.ic_add),
                    contentDescription = "",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Thêm",
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            }
        }
    }

}

// Preview cho FriendBottomSheet
@Preview(showBackground = true)
@Composable
fun FriendBottomSheetPreview() {
    MyLocketTheme {
        val authService = SupabaseAuthService()
        FriendBottomSheet(authService = authService)
    }
}
