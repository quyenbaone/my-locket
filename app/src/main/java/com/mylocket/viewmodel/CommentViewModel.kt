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
            _isLoading.value = true
            val result = databaseService.getCommentsForPost(postId)
            if (result.isSuccess) {
                _comments.value = result.getOrNull() ?: emptyList()
                Log.d("CommentViewModel", "Loaded ${_comments.value.size} comments for post $postId")
            } else {
                Log.e("CommentViewModel", "Failed to load comments for post $postId", result.exceptionOrNull())
                _comments.value = emptyList()
            }
            _isLoading.value = false
        }
    }

    fun addComment(content: String, userId: String, userName: String) {
        if (content.isBlank()) return
        
        viewModelScope.launch {
            val comment = Comment(
                postId = postId,
                userId = userId,
                userName = userName,
                content = content.trim()
            )
            
            val result = databaseService.addComment(comment)
            if (result.isSuccess) {
                Log.d("CommentViewModel", "Comment added successfully")
                // Refresh comments
                loadComments()
            } else {
                Log.e("CommentViewModel", "Failed to add comment", result.exceptionOrNull())
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
