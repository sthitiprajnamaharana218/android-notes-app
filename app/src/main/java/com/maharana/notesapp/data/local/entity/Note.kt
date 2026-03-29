package com.maharana.notesapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.maharana.notesapp.data.local.entity.ChecklistItem

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val color: Int = 0xFFE3F2FD.toInt(),
    val audioPath: String? = null,
    val images: List<String> = emptyList(),
    val checklistItems: List<ChecklistItem> = emptyList()
)
