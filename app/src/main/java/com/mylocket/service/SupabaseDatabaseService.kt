package com.mylocket.service

import android.util.Log
import com.mylocket.config.SupabaseConfig
import com.mylocket.data.Friend
import com.mylocket.data.Post
import com.mylocket.data.User
import io.github.jan.supabase.postgrest.from
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.datetime.Clock

class SupabaseDatabaseService {
    private val client = SupabaseConfig.client

    // User operations
    suspend fun addUser(user: User): Result<Unit> {
        return try {
            client.from("users").insert(user)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error adding user", e)
            Result.failure(e)
        }
    }

    suspend fun getUsers(): Result<List<User>> {
        return try {
            val users = client.from("users").select().decodeList<User>()
            Result.success(users)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error getting users", e)
            Result.failure(e)
        }
    }

    // Simplified real-time - return empty flow for now
    fun observeUsers(): Flow<List<User>> {
        return flowOf(emptyList())
    }

    // Post operations
    suspend fun addPost(post: Post): Result<Unit> {
        return try {
            val postWithTimestamp = post.copy(time = Clock.System.now())
            client.from("posts").insert(postWithTimestamp)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error adding post", e)
            Result.failure(e)
        }
    }

    suspend fun getPostsForUser(userId: String): Result<List<Post>> {
        return try {
            // For now, get all posts and filter client-side
            val posts = client.from("posts").select().decodeList<Post>()
            val filteredPosts = posts.filter { post ->
                post.toWho.contains(userId) || post.userId == userId
            }
            Result.success(filteredPosts)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error getting posts", e)
            Result.failure(e)
        }
    }

    // Simplified real-time - return empty flow for now
    fun observePostsForUser(userId: String): Flow<List<Post>> {
        return flowOf(emptyList())
    }

    // Friend operations
    suspend fun addFriend(userId: String, friend: Friend): Result<Unit> {
        return try {
            val friendData = mapOf(
                "user_id" to userId,
                "friend_id" to friend.id,
                "friend_name" to friend.name,
                "friend_email" to friend.email,
                "friend_photo" to friend.photo,
                "status" to friend.status
            )
            client.from("friends").insert(friendData)
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error adding friend", e)
            Result.failure(e)
        }
    }

    suspend fun getFriendsForUser(userId: String): Result<List<Friend>> {
        return try {
            // Get all friends data and filter client-side for now
            val friendsData = client.from("friends").select().decodeList<Map<String, Any>>()
            val userFriends = friendsData.filter { data ->
                data["user_id"] == userId
            }
            
            val friends = userFriends.map { data ->
                Friend(
                    status = data["status"] as? String ?: "",
                    id = data["friend_id"] as? String ?: "",
                    name = data["friend_name"] as? String ?: "",
                    email = data["friend_email"] as? String ?: "",
                    photo = data["friend_photo"] as? String
                )
            }
            Result.success(friends)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error getting friends", e)
            Result.failure(e)
        }
    }

    suspend fun updateFriendStatus(userId: String, friendId: String, status: String): Result<Unit> {
        return try {
            // For now, we'll need to implement this with a more complex query
            // This is a simplified version
            Log.d("SupabaseDB", "Friend status update requested: $userId -> $friendId = $status")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error updating friend status", e)
            Result.failure(e)
        }
    }

    suspend fun deleteFriend(userId: String, friendId: String): Result<Unit> {
        return try {
            // For now, we'll need to implement this with a more complex query
            // This is a simplified version
            Log.d("SupabaseDB", "Friend deletion requested: $userId -> $friendId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error deleting friend", e)
            Result.failure(e)
        }
    }

    // Simplified real-time - return empty flow for now
    fun observeFriendsForUser(userId: String): Flow<List<Friend>> {
        return flowOf(emptyList())
    }
}
