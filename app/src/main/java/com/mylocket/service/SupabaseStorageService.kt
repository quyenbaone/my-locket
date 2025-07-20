package com.mylocket.service

import android.util.Log
import com.mylocket.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import java.io.File

class SupabaseStorageService {
    private val storage = SupabaseConfig.client.storage

    suspend fun uploadImage(file: File, fileName: String? = null): Result<String> {
        return try {
            val actualFileName = fileName ?: "images/${System.currentTimeMillis()}.jpg"
            val bucket = storage.from("images")
            
            // Upload the file
            bucket.upload(actualFileName, file.readBytes())
            
            // Get the public URL
            val publicUrl = bucket.publicUrl(actualFileName)
            
            Log.d("SupabaseStorage", "Image uploaded successfully: $publicUrl")
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Error uploading image", e)
            Result.failure(e)
        }
    }

    suspend fun uploadImageFromByteArray(data: ByteArray, fileName: String? = null): Result<String> {
        return try {
            val actualFileName = fileName ?: "images/${System.currentTimeMillis()}.jpg"
            val bucket = storage.from("images")
            
            // Upload the byte array
            bucket.upload(actualFileName, data)
            
            // Get the public URL
            val publicUrl = bucket.publicUrl(actualFileName)
            
            Log.d("SupabaseStorage", "Image uploaded successfully: $publicUrl")
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Error uploading image", e)
            Result.failure(e)
        }
    }

    suspend fun deleteImage(fileName: String): Result<Unit> {
        return try {
            val bucket = storage.from("images")
            bucket.delete(fileName)
            
            Log.d("SupabaseStorage", "Image deleted successfully: $fileName")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Error deleting image", e)
            Result.failure(e)
        }
    }

    suspend fun downloadImage(fileName: String): Result<ByteArray> {
        return try {
            val bucket = storage.from("images")
            val data = bucket.downloadAuthenticated(fileName)
            
            Log.d("SupabaseStorage", "Image downloaded successfully: $fileName")
            Result.success(data)
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Error downloading image", e)
            Result.failure(e)
        }
    }

    fun getPublicUrl(fileName: String): String {
        return try {
            val bucket = storage.from("images")
            bucket.publicUrl(fileName)
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Error getting public URL", e)
            ""
        }
    }
}
