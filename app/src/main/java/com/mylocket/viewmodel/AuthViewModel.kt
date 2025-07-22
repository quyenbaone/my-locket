package com.mylocket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mylocket.service.SupabaseAuthService
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val authService = SupabaseAuthService()
    
    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    private val _currentUser = MutableStateFlow<UserInfo?>(null)
    val currentUser: StateFlow<UserInfo?> = _currentUser.asStateFlow()
    
    init {
        checkAuthState()
        observeAuthChanges()
    }
    
    private fun checkAuthState() {
        viewModelScope.launch {
            try {
                Log.d("AuthViewModel", "Checking initial auth state...")
                val user = authService.getCurrentUser()
                if (user != null) {
                    Log.d("AuthViewModel", "User is authenticated: ${user.email}")
                    _currentUser.value = user
                    _authState.value = AuthState.Authenticated
                } else {
                    Log.d("AuthViewModel", "User is not authenticated")
                    _currentUser.value = null
                    _authState.value = AuthState.NotAuthenticated
                }
            } catch (e: Exception) {
                Log.e("AuthViewModel", "Error checking auth state", e)
                _currentUser.value = null
                _authState.value = AuthState.NotAuthenticated
            }
        }
    }
    
    private fun observeAuthChanges() {
        viewModelScope.launch {
            authService.observeAuthState().collect { user ->
                Log.d("AuthViewModel", "Auth state changed: ${user?.email ?: "null"}")
                _currentUser.value = user
                _authState.value = if (user != null) {
                    AuthState.Authenticated
                } else {
                    AuthState.NotAuthenticated
                }
            }
        }
    }
    
    suspend fun signIn(email: String, password: String): Result<UserInfo> {
        _authState.value = AuthState.Loading
        return try {
            val result = authService.signIn(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                _currentUser.value = user
                _authState.value = AuthState.Authenticated
                Log.d("AuthViewModel", "Sign in successful for: $email")
            } else {
                _authState.value = AuthState.NotAuthenticated
                Log.e("AuthViewModel", "Sign in failed for: $email")
            }
            result
        } catch (e: Exception) {
            _authState.value = AuthState.NotAuthenticated
            Log.e("AuthViewModel", "Sign in error for: $email", e)
            Result.failure(e)
        }
    }
    
    suspend fun signUp(email: String, password: String): Result<UserInfo> {
        _authState.value = AuthState.Loading
        return try {
            val result = authService.signUp(email, password)
            if (result.isSuccess) {
                val user = result.getOrNull()
                _currentUser.value = user
                _authState.value = AuthState.Authenticated
                Log.d("AuthViewModel", "Sign up successful for: $email")
            } else {
                _authState.value = AuthState.NotAuthenticated
                Log.e("AuthViewModel", "Sign up failed for: $email")
            }
            result
        } catch (e: Exception) {
            _authState.value = AuthState.NotAuthenticated
            Log.e("AuthViewModel", "Sign up error for: $email", e)
            Result.failure(e)
        }
    }
    
    suspend fun signOut(): Result<Unit> {
        return try {
            val result = authService.signOut()
            if (result.isSuccess) {
                _currentUser.value = null
                _authState.value = AuthState.NotAuthenticated
                Log.d("AuthViewModel", "Sign out successful")
            }
            result
        } catch (e: Exception) {
            Log.e("AuthViewModel", "Sign out error", e)
            Result.failure(e)
        }
    }
    
    fun isUserSignedIn(): Boolean {
        return _currentUser.value != null
    }
    
    fun getCurrentUser(): UserInfo? {
        return _currentUser.value
    }
}

sealed class AuthState {
    object Loading : AuthState()
    object Authenticated : AuthState()
    object NotAuthenticated : AuthState()
}
