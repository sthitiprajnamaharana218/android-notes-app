package com.maharana.notesapp.utils

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import java.io.File

class AudioPlayer(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var isAudioPlaying = false

    fun startPlaying(audioPath: String, onCompletion: () -> Unit) {
        stopPlaying()
        try {
            mediaPlayer = MediaPlayer().apply {
                setDataSource(audioPath)
                prepare()
                start()
                setOnCompletionListener {
                    isAudioPlaying = false
                    onCompletion()
                    stopPlaying()
                }
            }
            isAudioPlaying = true
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun stopPlaying() {
        mediaPlayer?.apply {
            if (isPlaying) {
                stop()
            }
            release()
        }
        mediaPlayer = null
        isAudioPlaying = false
    }

    fun isPlaying(): Boolean = isAudioPlaying
}
