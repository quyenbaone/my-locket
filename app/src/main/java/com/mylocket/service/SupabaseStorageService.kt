package com.mylocket.service

import android.util.Log
import com.mylocket.config.SupabaseConfig
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.withTimeout
import java.io.File

class SupabaseStorageService {
    private val storage = SupabaseConfig.client.storage

    suspend fun uploadImage(file: File, fileName: String? = null): Result<String> {
        return try {
            val actualFileName = fileName ?: "images/${System.currentTimeMillis()}.jpg"
            val bucket = storage.from("images")

            Log.d("SupabaseStorage", "Starting upload for file: ${file.name}, size: ${file.length()} bytes")

            // Upload with timeout (60 seconds)
            withTimeout(60_000) {
                bucket.upload(actualFileName, file.readBytes())
            }

            // Get the public URL
            val publicUrl = bucket.publicUrl(actualFileName)

            Log.d("SupabaseStorage", "Image uploaded successfully: $publicUrl")
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Error uploading image (${file.name})", e)
            Result.failure(e)
        }
    }

    suspend fun uploadImageFromByteArray(data: ByteArray, fileName: String? = null): Result<String> {
        return try {
            val actualFileName = fileName ?: "images/${System.currentTimeMillis()}.jpg"
            val bucket = storage.from("images")

            Log.d("SupabaseStorage", "Starting upload for byte array, size: ${data.size} bytes")

            // Upload with timeout (60 seconds)
            withTimeout(60_000) {
                bucket.upload(actualFileName, data)
            }

            // Get the public URL
            val publicUrl = bucket.publicUrl(actualFileName)

            Log.d("SupabaseStorage", "Image uploaded successfully: $publicUrl")
            Result.success(publicUrl)
        } catch (e: Exception) {
            Log.e("SupabaseStorage", "Error uploading image from byte array", e)
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
