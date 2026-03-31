package com.maharana.notesapp.utils

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
    import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat

object PermissionsUtil {
    
    fun checkPermission(context: Context, permission: String): Boolean {
        return ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    fun checkAudioPermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.RECORD_AUDIO)
    }
    
    fun checkCameraPermission(context: Context): Boolean {
        return checkPermission(context, Manifest.permission.CAMERA)
    }
    
    fun checkStoragePermission(context: Context): Boolean {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            checkPermission(context, Manifest.permission.READ_MEDIA_IMAGES)
        } else {
            checkPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }
    
    fun getAudioPermissionContract(): ActivityResultContract<String, Boolean> {
        return ActivityResultContracts.RequestPermission()
    }
    
    fun getCameraPermissionContract(): ActivityResultContract<String, Boolean> {
        return ActivityResultContracts.RequestPermission()
    }
    
    fun getStoragePermissionContract(): ActivityResultContract<Array<String>, Map<String, Boolean>> {
        return ActivityResultContracts.RequestMultiplePermissions()
    }
    
    fun getRequiredPermissions(): Array<String> {
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        } else {
            arrayOf(
                Manifest.permission.RECORD_AUDIO,
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        }
    }
}
