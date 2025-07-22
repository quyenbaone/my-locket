package com.mylocket.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mylocket.data.Comment
import com.mylocket.service.LocalStorageService
import com.mylocket.service.SupabaseDatabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CommentViewModelFactory(
    private val postId: String,
    private val context: Context
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommentViewModel(postId, context) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CommentViewModel(
    private val postId: String,
    private val context: Context
) : ViewModel() {
    private val databaseService = SupabaseDatabaseService()
    private val localStorageService = LocalStorageService(context)

    private val _comments = MutableStateFlow<List<Comment>>(emptyList())
    val comments: StateFlow<List<Comment>>
        get() {
            return _comments
        }

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    init {
        loadComments()
    }

    private fun loadComments() {
        viewModelScope.launch {
            Log.d("CommentViewModel", "Starting to load comments for post: $postId")
            _isLoading.value = true

            try {
                // Always load from local storage first - this is our source of truth
                val savedComments = localStorageService.loadComments(postId)
                _comments.value = savedComments
                Log.d("CommentViewModel", "Loaded ${savedComments.size} comments from local storage for post $postId")

                // Only try database if we have no local comments at all
                if (savedComments.isEmpty()) {
                    Log.d("CommentViewModel", "No local comments found, trying database...")
                    try {
                        val result = databaseService.getCommentsForPost(postId)
                        if (result.isSuccess) {
                            val databaseComments = result.getOrNull() ?: emptyList()
                            if (databaseComments.isNotEmpty()) {
                                _comments.value = databaseComments
                                localStorageService.saveComments(postId, databaseComments)
                                Log.d("CommentViewModel", "Loaded ${databaseComments.size} comments from database and saved locally")
                            }
                        }
                    } catch (dbException: Exception) {
                        Log.e("CommentViewModel", "Database exception, using empty list", dbException)
                    }
                } else {
                    Log.d("CommentViewModel", "Using ${savedComments.size} local comments, skipping database")
                }
            } catch (e: Exception) {
                Log.e("CommentViewModel", "Exception while loading comments", e)
                _comments.value = emptyList()
            } finally {
                _isLoading.value = false
                Log.d("CommentViewModel", "Loading completed. Final comment count: ${_comments.value.size}")
            }
        }
    }

    fun addComment(content: String, userId: String, userName: String) {
        if (content.isBlank()) return

        viewModelScope.launch {
            val comment = Comment(
                id = "local_${System.currentTimeMillis()}_${(0..999).random()}", // Unique local ID
                postId = postId,
                userId = userId,
                userName = userName,
                content = content.trim(),
                time = kotlinx.datetime.Clock.System.now()
            )

            Log.d("CommentViewModel", "Adding comment: $content by $userName")

            try {
                // Add comment to current list immediately for better UX
                val currentComments = _comments.value.toMutableList()
                currentComments.add(comment)
                _comments.value = currentComments

                // Save to local storage immediately
                localStorageService.addComment(postId, comment)
                Log.d("CommentViewModel", "Comment saved to local storage. Total comments now: ${currentComments.size}")

                // Verify save worked
                val verifyComments = localStorageService.loadComments(postId)
                Log.d("CommentViewModel", "Verification: ${verifyComments.size} comments in storage")

                // Try to save to database (optional, for sync)
                val result = databaseService.addComment(comment)
                if (result.isSuccess) {
                    Log.d("CommentViewModel", "Comment added successfully to database")
                } else {
                    Log.e("CommentViewModel", "Failed to add comment to database (but saved locally)", result.exceptionOrNull())
                }
            } catch (e: Exception) {
                Log.e("CommentViewModel", "Error adding comment", e)
                // Remove the comment from UI if local save failed
                _comments.value = _comments.value.filter { it.id != comment.id }
            }
        }
    }

    fun deleteComment(commentId: String) {
        viewModelScope.launch {
            val result = databaseService.deleteComment(commentId)
            if (result.isSuccess) {
                Log.d("CommentViewModel", "Comment deleted successfully")
                // Refresh comments
                loadComments()
            } else {
                Log.e("CommentViewModel", "Failed to delete comment", result.exceptionOrNull())
            }
        }
    }

    fun refreshComments() {
        loadComments()
    }
}
