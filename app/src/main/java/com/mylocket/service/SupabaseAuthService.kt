package com.mylocket.service

import android.util.Log
import com.mylocket.config.SupabaseConfig
import com.mylocket.data.User
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.gotrue.user.UserInfo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SupabaseAuthService {
    private val supabaseClient = SupabaseConfig.client
    private val auth = supabaseClient.auth

    suspend fun signUp(userEmail: String, userPassword: String): Result<UserInfo> {
        return try {
            Log.d("SupabaseAuth", "Sign up requested for: $userEmail")

            val user = auth.signUpWith(Email) {
                email = userEmail
                password = userPassword
            }

            Log.d("SupabaseAuth", "Sign up successful for: $userEmail")
            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Sign up returned null user"))
            }
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "Sign up failed for $userEmail", e)

            // Create more specific error messages for sign up
            val errorMessage = when {
                e.message?.contains("User already registered") == true ->
                    "Email này đã được đăng ký. Vui lòng đăng nhập hoặc sử dụng email khác."
                e.message?.contains("Password should be at least") == true ->
                    "Mật khẩu phải có ít nhất 6 ký tự."
                e.message?.contains("Invalid email") == true ->
                    "Định dạng email không hợp lệ."
                e.message?.contains("Signup is disabled") == true ->
                    "Đăng ký tài khoản hiện đang bị tắt."
                else ->
                    "Đăng ký thất bại: ${e.message ?: "Lỗi không xác định"}"
            }

            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun signIn(userEmail: String, userPassword: String): Result<UserInfo> {
        return try {
            Log.d("SupabaseAuth", "Sign in requested for: $userEmail")

            auth.signInWith(Email) {
                email = userEmail
                password = userPassword
            }

            // After successful sign in, get the current user
            val user = auth.currentUserOrNull()
            Log.d("SupabaseAuth", "Sign in successful for: $userEmail")

            if (user != null) {
                Result.success(user)
            } else {
                Result.failure(Exception("Sign in successful but user is null"))
            }
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "Sign in failed for $userEmail", e)

            // Create more specific error messages in Vietnamese
            val errorMessage = when {
                e.message?.contains("Invalid login credentials") == true ->
                    "Email hoặc mật khẩu không đúng. Vui lòng kiểm tra lại."
                e.message?.contains("Email not confirmed") == true ->
                    "Email chưa được xác thực. Vui lòng kiểm tra email và xác thực tài khoản."
                e.message?.contains("Too many requests") == true ->
                    "Quá nhiều lần thử. Vui lòng đợi một lúc rồi thử lại."
                e.message?.contains("User not found") == true ->
                    "Tài khoản không tồn tại. Vui lòng đăng ký tài khoản mới."
                e.message?.contains("Account is disabled") == true ->
                    "Tài khoản đã bị vô hiệu hóa. Vui lòng liên hệ hỗ trợ."
                else ->
                    "Đăng nhập thất bại: ${e.message ?: "Lỗi không xác định"}"
            }

            Result.failure(Exception(errorMessage))
        }
    }

    suspend fun signOut(): Result<Unit> {
        return try {
            Log.d("SupabaseAuth", "Sign out requested")
            auth.signOut()
            Log.d("SupabaseAuth", "Sign out successful")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "Sign out failed", e)
            Result.failure(e)
        }
    }

    suspend fun resetPassword(userEmail: String): Result<Unit> {
        return try {
            Log.d("SupabaseAuth", "Password reset requested for: $userEmail")
            auth.resetPasswordForEmail(userEmail)
            Log.d("SupabaseAuth", "Password reset email sent to: $userEmail")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "Password reset failed for $userEmail", e)
            Result.failure(e)
        }
    }

    suspend fun updateProfile(displayName: String): Result<Unit> {
        return try {
            Log.d("SupabaseAuth", "Profile update requested: $displayName")
            auth.modifyUser {
                data = buildJsonObject {
                    put("display_name", displayName)
                }
            }
            Log.d("SupabaseAuth", "Profile update successful: $displayName")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "Profile update failed for $displayName", e)
            Result.failure(e)
        }
    }

    suspend fun updateEmail(newEmail: String): Result<Unit> {
        return try {
            Log.d("SupabaseAuth", "Email update requested: $newEmail")
            auth.modifyUser {
                email = newEmail
            }
            Log.d("SupabaseAuth", "Email update successful: $newEmail")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "Email update failed for $newEmail", e)
            Result.failure(e)
        }
    }

    suspend fun deleteAccount(): Result<Unit> {
        return try {
            Log.d("SupabaseAuth", "Account deletion requested")
            // Note: Supabase doesn't have a direct delete user method in the client
            // This would typically be handled by a server-side function
            Log.w("SupabaseAuth", "Account deletion not implemented - requires server-side function")
            Result.failure(Exception("Account deletion requires server-side implementation"))
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "Account deletion failed", e)
            Result.failure(e)
        }
    }

    fun getCurrentUser(): UserInfo? {
        return try {
            auth.currentUserOrNull()
        } catch (e: Exception) {
            Log.e("SupabaseAuth", "Error getting current user", e)
            null
        }
    }

    fun isUserSignedIn(): Boolean {
        return getCurrentUser() != null
    }

    fun observeAuthState(): Flow<UserInfo?> {
        return auth.sessionStatus.map { sessionStatus ->
            when (sessionStatus) {
                is io.github.jan.supabase.gotrue.SessionStatus.Authenticated -> sessionStatus.session.user
                is io.github.jan.supabase.gotrue.SessionStatus.NotAuthenticated -> null
                is io.github.jan.supabase.gotrue.SessionStatus.LoadingFromStorage -> getCurrentUser()
                is io.github.jan.supabase.gotrue.SessionStatus.NetworkError -> getCurrentUser()
                else -> null
            }
        }
    }
}
