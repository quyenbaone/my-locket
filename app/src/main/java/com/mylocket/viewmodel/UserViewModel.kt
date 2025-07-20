package com.mylocket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mylocket.service.SupabaseDatabaseService
import com.mylocket.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class UserViewModel : ViewModel(){
    private val databaseService = SupabaseDatabaseService()

    private val _users = MutableStateFlow<List<User>>(emptyList())
    val users:StateFlow<List<User>>
        get() {
            return _users
        }

    init {
        loadUsers()
        listenForUserUpdates()
    }

    private fun loadUsers() {
        viewModelScope.launch {
            val result = databaseService.getUsers()
            if (result.isSuccess) {
                _users.value = result.getOrNull() ?: emptyList()
            } else {
                Log.e("UserViewModel", "Failed to load users", result.exceptionOrNull())
                _users.value = emptyList()
            }
        }
    }

    private fun listenForUserUpdates() {
        viewModelScope.launch {
            databaseService.observeUsers().collect { newUsers ->
                // Update the current list with new users
                val currentUsers = _users.value.toMutableList()
                newUsers.forEach { newUser ->
                    val existingIndex = currentUsers.indexOfFirst { it.id == newUser.id }
                    if (existingIndex >= 0) {
                        currentUsers[existingIndex] = newUser
                    } else {
                        currentUsers.add(newUser)
                    }
                }
                _users.value = currentUsers
            }
        }
    }

    fun addUser(user: User) {
        viewModelScope.launch {
            val result = databaseService.addUser(user)
            if (result.isSuccess) {
                Log.d("Add User", "User added successfully")
                // Refresh the users list
                loadUsers()
            } else {
                Log.e("Add User", "Error adding user", result.exceptionOrNull())
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        // Cleanup will be handled automatically by coroutines
    }
}