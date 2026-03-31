package com.maharana.notesapp.utils

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class AudioRecorder(private val context: Context) {
    private var mediaRecorder: MediaRecorder? = null
    private var audioFile: File? = null
    private var isRecording = false

    fun startRecording(): String? {
        return try {
            if (isRecording) return audioFile?.absolutePath
            
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "audio_$timestamp.mp4" // Changed to mp4 as it's more standard for MediaRecorder
            val storageDir = File(context.getExternalFilesDir(null), "audio")
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }
            val newAudioFile = File(storageDir, fileName)
            audioFile = newAudioFile
            
            mediaRecorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }
            
            mediaRecorder?.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(newAudioFile.absolutePath)
                prepare()
                start()
            }
            
            isRecording = true
            newAudioFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun stopRecording(): String? {
        return try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            audioFile?.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun cancelRecording() {
        try {
            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            audioFile?.delete()
            audioFile = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun isRecording(): Boolean = isRecording
}
