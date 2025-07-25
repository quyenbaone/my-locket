package com.mylocket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mylocket.service.SupabaseDatabaseService
import com.mylocket.data.Friend
import com.mylocket.data.FriendStatus
import com.mylocket.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


class FriendViewModelFactory(private val uid: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FriendViewModel::class.java)) {
            return FriendViewModel(uid) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class FriendViewModel(private val uid: String) : ViewModel() {
    private val databaseService = SupabaseDatabaseService()

    private val _friends = MutableStateFlow<List<Friend>>(emptyList())
    val friends: StateFlow<List<Friend>>
        get() {
            return _friends
        }

    init {
        loadFriends()
        listenForFriendUpdates()
        loadMockFriends() // Add mock friends for testing
    }

    private fun loadFriends() {
        viewModelScope.launch {
            val result = databaseService.getFriendsForUser(uid)
            if (result.isSuccess) {
                _friends.value = result.getOrNull() ?: emptyList()
            } else {
                Log.e("Load Friends", "Failed to load friends", result.exceptionOrNull())
                _friends.value = emptyList()
            }
        }
    }

    private fun listenForFriendUpdates() {
        viewModelScope.launch {
            databaseService.observeFriendsForUser(uid).collect { newFriends ->
                // Update the current list with new friends
                val currentFriends = _friends.value.toMutableList()
                newFriends.forEach { newFriend ->
                    val existingIndex = currentFriends.indexOfFirst { it.friendId == newFriend.friendId }
                    if (existingIndex >= 0) {
                        currentFriends[existingIndex] = newFriend
                    } else {
                        currentFriends.add(newFriend)
                    }
                }
                _friends.value = currentFriends
            }
        }
    }

    fun addFriend(friend: Friend, user: User) {
        viewModelScope.launch {
            // Add friend for current user
            val result1 = databaseService.addFriend(uid, friend)

            // Add current user as friend for the other user
            val reciprocalFriend = Friend(
                friendId = user.id,
                name = user.name,
                email = user.email,
                photo = user.photo,
                status = FriendStatus.RECEIVED.toString()
            )
            val result2 = databaseService.addFriend(friend.friendId, reciprocalFriend)

            if (result1.isSuccess && result2.isSuccess) {
                Log.d("Add Friend", "Friend added successfully")
                loadFriends()
            } else {
                Log.e("Add Friend", "Error adding friend")
            }
        }
    }

    fun acceptFriend(friend: Friend, user: User){
        viewModelScope.launch {
            // Update status for current user
            val result1 = databaseService.updateFriendStatus(uid, friend.id, FriendStatus.FRIENDS.toString())

            // Update status for the friend
            val result2 = databaseService.updateFriendStatus(friend.id, user.id, FriendStatus.FRIENDS.toString())

            if (result1.isSuccess && result2.isSuccess) {
                Log.d("Accept Friend", "Friend accepted successfully")
                loadFriends()
            } else {
                Log.e("Accept Friend", "Error accepting friend")
            }
        }
    }

    fun deleteFriend(userId: String, friendId: String){
        viewModelScope.launch {
            // Delete friend from current user's list
            val result1 = databaseService.deleteFriend(uid, friendId)

            // Delete current user from friend's list
            val result2 = databaseService.deleteFriend(friendId, userId)

            if (result1.isSuccess && result2.isSuccess) {
                Log.d("Delete Friend", "Friend deleted successfully")
                loadFriends()
            } else {
                Log.e("Delete Friend", "Error deleting friend")
            }
        }
    }

    // Load mock friends for testing
    private fun loadMockFriends() {
        val mockFriends = listOf(
            Friend(
                id = "friend_123",
                friendId = "friend_123",
                name = "Anna",
                email = "anna@example.com",
                photo = "",
                status = FriendStatus.FRIENDS.toString()
            ),
            Friend(
                id = "friend_456",
                friendId = "friend_456",
                name = "Minh",
                email = "minh@example.com",
                photo = "",
                status = FriendStatus.FRIENDS.toString()
            ),
            Friend(
                id = "friend_789",
                friendId = "friend_789",
                name = "Linh",
                email = "linh@example.com",
                photo = "",
                status = FriendStatus.FRIENDS.toString()
            )
        )

        // Add mock friends to current list
        val currentFriends = _friends.value.toMutableList()
        mockFriends.forEach { mockFriend ->
            if (currentFriends.none { it.friendId == mockFriend.friendId }) {
                currentFriends.add(mockFriend)
            }
        }
        _friends.value = currentFriends
    }

    override fun onCleared() {
        super.onCleared()
        // Cleanup will be handled automatically by coroutines
    }

}