package com.mylocket.service

import android.util.Log
import com.mylocket.config.SupabaseConfig
import com.mylocket.data.Friend
import com.mylocket.data.Post
import com.mylocket.data.User
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Columns
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
            // Get posts where user is in to_who array OR user is the author
            val posts = client.from("posts")
                .select()
                .decodeList<Post>()

            val filteredPosts = posts.filter { post ->
                post.toWho.contains(userId) || post.userId == userId
            }

            Log.d("SupabaseDB", "Retrieved ${filteredPosts.size} posts for user $userId")
            Result.success(filteredPosts)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error getting posts for user $userId", e)
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
                "friend_id" to friend.friendId,
                "friend_name" to friend.name,
                "friend_email" to friend.email,
                "friend_photo" to friend.photo,
                "status" to friend.status
            )

            Log.d("SupabaseDB", "Adding friend: $friendData")
            client.from("friends").insert(friendData)
            Log.d("SupabaseDB", "Friend added successfully")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error adding friend for user $userId", e)
            Result.failure(e)
        }
    }

    suspend fun getFriendsForUser(userId: String): Result<List<Friend>> {
        return try {
            // Get all friends and filter client-side for now
            val allFriends = client.from("friends")
                .select()
                .decodeList<Friend>()

            // Filter friends for this user
            val userFriends = allFriends.filter { it.userId == userId }

            Log.d("SupabaseDB", "Retrieved ${userFriends.size} friends for user $userId")
            Result.success(userFriends)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error getting friends for user $userId", e)
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
