package com.mylocket.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.mylocket.data.Comment
import com.mylocket.service.SupabaseDatabaseService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class CommentViewModelFactory(private val postId: String) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CommentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CommentViewModel(postId) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class CommentViewModel(private val postId: String) : ViewModel() {
    private val databaseService = SupabaseDatabaseService()

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
                val result = databaseService.getCommentsForPost(postId)
                Log.d("CommentViewModel", "Database call completed. Success: ${result.isSuccess}")

                if (result.isSuccess) {
                    val comments = result.getOrNull() ?: emptyList()
                    _comments.value = comments
                    Log.d("CommentViewModel", "Successfully loaded ${comments.size} comments for post $postId")
                    comments.forEach { comment ->
                        Log.d("CommentViewModel", "Comment: ${comment.userName} - ${comment.content}")
                    }
                } else {
                    Log.e("CommentViewModel", "Failed to load comments for post $postId", result.exceptionOrNull())
                    _comments.value = emptyList()
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
                id = "temp_${System.currentTimeMillis()}", // Temporary ID
                postId = postId,
                userId = userId,
                userName = userName,
                content = content.trim(),
                time = kotlinx.datetime.Clock.System.now()
            )

            Log.d("CommentViewModel", "Adding comment: $content by $userName")

            // Add comment to current list immediately for better UX
            val currentComments = _comments.value.toMutableList()
            currentComments.add(comment)
            _comments.value = currentComments

            val result = databaseService.addComment(comment)
            if (result.isSuccess) {
                Log.d("CommentViewModel", "Comment added successfully to database")
                // Refresh comments to get real IDs and sync with database
                loadComments()
            } else {
                Log.e("CommentViewModel", "Failed to add comment to database", result.exceptionOrNull())
                // Remove the comment from UI if database save failed
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
