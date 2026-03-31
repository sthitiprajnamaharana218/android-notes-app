package com.maharana.notesapp.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class ImageUtils(private val context: Context) {
    
    fun saveImageToInternalStorage(uri: Uri): String? {
        return try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "image_$timestamp.jpg"
            
            val imageDir = File(context.getExternalFilesDir(null), "images")
            if (!imageDir.exists()) {
                imageDir.mkdirs()
            }
            
            val imageFile = File(imageDir, fileName)
            val outputStream = FileOutputStream(imageFile)
            
            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            
            imageFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun createImageUri(): Uri? {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "camera_image_$timestamp.jpg"
        
        val imageDir = File(context.getExternalFilesDir(null), "images")
        if (!imageDir.exists()) {
            imageDir.mkdirs()
        }
        
        val imageFile = File(imageDir, fileName)
        return try {
            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                imageFile
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    fun compressImage(imagePath: String, maxWidth: Int = 1024, maxHeight: Int = 1024): String {
        return try {
            val originalBitmap = BitmapFactory.decodeFile(imagePath) ?: return imagePath
            
            val width = originalBitmap.width
            val height = originalBitmap.height
            
            val aspectRatio = width.toFloat() / height.toFloat()
            var newWidth = maxWidth
            var newHeight = (maxWidth / aspectRatio).toInt()
            
            if (newHeight > maxHeight) {
                newHeight = maxHeight
                newWidth = (maxHeight * aspectRatio).toInt()
            }
            
            val compressedBitmap = Bitmap.createScaledBitmap(
                originalBitmap,
                newWidth,
                newHeight,
                true
            )
            
            val file = File(imagePath)
            val outputStream = FileOutputStream(file)
            compressedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
            outputStream.close()
            
            imagePath
        } catch (e: Exception) {
            e.printStackTrace()
            imagePath
        }
    }
    
    fun deleteImage(imagePath: String): Boolean {
        return try {
            val file = File(imagePath)
            file.delete()
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
    
    fun getImageSize(imagePath: String): Long {
        return try {
            val file = File(imagePath)
            file.length()
        } catch (e: Exception) {
            e.printStackTrace()
            0L
        }
    }
}
