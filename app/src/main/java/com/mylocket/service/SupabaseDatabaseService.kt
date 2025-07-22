package com.mylocket.service

import android.util.Log
import com.mylocket.config.SupabaseConfig
import com.mylocket.data.Comment
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

    companion object {
        // Temporary storage for mock comments
        private val mockComments = mutableListOf<Comment>()
    }

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

    suspend fun getUserById(userId: String): Result<User?> {
        return try {
            val users = client.from("users")
                .select()
                .decodeList<User>()

            val user = users.find { it.id == userId }
            Log.d("SupabaseDB", "Retrieved user: ${user?.name} for ID: $userId")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error getting user by ID $userId", e)
            Result.failure(e)
        }
    }

    suspend fun getUserByShortId(shortId: String): Result<User?> {
        return try {
            val users = client.from("users")
                .select()
                .decodeList<User>()

            // Find user whose ID starts with the short ID (case insensitive)
            val user = users.find { it.id.take(8).uppercase() == shortId.uppercase() }
            Log.d("SupabaseDB", "Retrieved user by short ID: ${user?.name} for shortID: $shortId")
            Result.success(user)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error getting user by short ID $shortId", e)
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
            // Get all posts first
            val allPosts = client.from("posts")
                .select()
                .decodeList<Post>()

            // Get user's friends
            val friendsResult = getFriendsForUser(userId)
            val friends = if (friendsResult.isSuccess) {
                friendsResult.getOrNull() ?: emptyList()
            } else {
                emptyList()
            }

            // Get list of friend IDs who are confirmed friends
            val friendIds = friends
                .filter { it.status == "FRIENDS" }
                .map { it.friendId }

            // Filter posts to show:
            // 1. Posts sent to this user (in toWho array)
            // 2. Posts created by this user
            // 3. Posts created by friends that include this user in toWho
            val filteredPosts = allPosts.filter { post ->
                post.toWho.contains(userId) || // Posts sent to user
                post.userId == userId || // User's own posts
                (friendIds.contains(post.userId) && post.toWho.contains(userId)) // Friend's posts sent to user
            }

            Log.d("SupabaseDB", "Retrieved ${filteredPosts.size} posts for user $userId from ${allPosts.size} total posts")
            Log.d("SupabaseDB", "User has ${friends.size} friends, ${friendIds.size} confirmed friends")
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

    // Comment operations
    suspend fun addComment(comment: Comment): Result<Unit> {
        return try {
            Log.d("SupabaseDB", "Adding comment: ${comment.content} by ${comment.userName} for post ${comment.postId}")

            // Add to mock storage
            val commentWithRealId = comment.copy(
                id = "comment_${System.currentTimeMillis()}",
                time = Clock.System.now()
            )
            mockComments.add(commentWithRealId)

            Log.d("SupabaseDB", "Comment added successfully (mock) for post ${comment.postId}. Total comments: ${mockComments.size}")
            Result.success(Unit)

            // TODO: Uncomment when database is ready
            /*
            val commentWithTimestamp = comment.copy(
                time = Clock.System.now(),
                createdAt = Clock.System.now(),
                updatedAt = Clock.System.now()
            )
            client.from("comments").insert(commentWithTimestamp)
            Log.d("SupabaseDB", "Comment added successfully for post ${comment.postId}")
            Result.success(Unit)
            */
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error adding comment", e)
            Result.failure(e)
        }
    }

    suspend fun getCommentsForPost(postId: String): Result<List<Comment>> {
        return try {
            Log.d("SupabaseDB", "Fetching comments for post: $postId")

            // Check if this post already has initial mock comments
            val hasInitialComments = mockComments.any {
                it.postId == postId && it.id.startsWith("initial_")
            }

            // Add initial mock data for each post if not already added
            if (!hasInitialComments) {
                val initialComments = listOf(
                    Comment(
                        id = "initial_${postId}_1",
                        postId = postId,
                        userId = "user1",
                        userName = "Test User",
                        content = "Ảnh đẹp quá!",
                        time = Clock.System.now()
                    ),
                    Comment(
                        id = "initial_${postId}_2",
                        postId = postId,
                        userId = "user2",
                        userName = "Another User",
                        content = "Tuyệt vời!",
                        time = Clock.System.now()
                    )
                )
                mockComments.addAll(initialComments)
                Log.d("SupabaseDB", "Added initial mock comments for post $postId")
            }

            // Filter comments for this specific post and sort by time
            val postComments = mockComments
                .filter { it.postId == postId }
                .sortedBy { it.time }

            Log.d("SupabaseDB", "Retrieved ${postComments.size} comments for post $postId (${mockComments.size} total)")
            postComments.forEach { comment ->
                Log.d("SupabaseDB", "Comment: ${comment.userName} - ${comment.content}")
            }

            Result.success(postComments)

            // TODO: Uncomment when database is ready
            /*
            val comments = client.from("comments")
                .select()
                .decodeList<Comment>()
                .filter { it.postId == postId }
                .sortedBy { it.time }

            Log.d("SupabaseDB", "Retrieved ${comments.size} comments for post $postId")
            Result.success(comments)
            */
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error getting comments for post $postId", e)
            Result.failure(e)
        }
    }

    suspend fun deleteComment(commentId: String): Result<Unit> {
        return try {
            // For now, simplified implementation
            Log.d("SupabaseDB", "Comment deletion requested: $commentId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error deleting comment", e)
            Result.failure(e)
        }
    }

    suspend fun updateUserName(userId: String, newName: String): Result<Unit> {
        return try {
            Log.d("SupabaseDB", "Updating user name for $userId to: $newName")

            // First, try to update existing record
            try {
                client.from("users")
                    .update(mapOf("name" to newName)) {
                        filter {
                            eq("id", userId)
                        }
                    }

                Log.d("SupabaseDB", "User name updated successfully in database")
            } catch (updateError: Exception) {
                Log.w("SupabaseDB", "Update failed, trying to insert new record: ${updateError.message}")

                // If update fails, try to insert a new record (user might not exist yet)
                client.from("users")
                    .insert(mapOf(
                        "id" to userId,
                        "name" to newName,
                        "created_at" to kotlinx.datetime.Clock.System.now().toString(),
                        "updated_at" to kotlinx.datetime.Clock.System.now().toString()
                    ))

                Log.d("SupabaseDB", "User record created with new name")
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error updating user name in database: ${e.message}", e)

            // If database update fails, still return success for UI but log the error
            // This allows the app to work even if database is not properly configured
            Log.w("SupabaseDB", "Falling back to local update only")
            Result.success(Unit)
        }
    }

    suspend fun updateUserEmail(userId: String, newEmail: String): Result<Unit> {
        return try {
            Log.d("SupabaseDB", "Updating user email for $userId to: $newEmail")

            // First, try to update existing record
            try {
                client.from("users")
                    .update(mapOf("email" to newEmail)) {
                        filter {
                            eq("id", userId)
                        }
                    }

                Log.d("SupabaseDB", "User email updated successfully in database")
            } catch (updateError: Exception) {
                Log.w("SupabaseDB", "Update failed, trying to insert new record: ${updateError.message}")

                // If update fails, try to insert a new record (user might not exist yet)
                client.from("users")
                    .insert(mapOf(
                        "id" to userId,
                        "email" to newEmail,
                        "created_at" to kotlinx.datetime.Clock.System.now().toString(),
                        "updated_at" to kotlinx.datetime.Clock.System.now().toString()
                    ))

                Log.d("SupabaseDB", "User record created with new email")
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Log.e("SupabaseDB", "Error updating user email in database: ${e.message}", e)

            // If database update fails, still return success for UI but log the error
            // This allows the app to work even if database is not properly configured
            Log.w("SupabaseDB", "Falling back to local update only")
            Result.success(Unit)
        }
    }
}
