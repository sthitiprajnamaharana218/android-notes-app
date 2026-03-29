package com.maharana.notesapp.domain.model

data class Note(
    val id: Long = 0,
    val title: String,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val color: Int = 0xFFE3F2FD.toInt(),
    val audioPath: String? = null,
    val images: List<String> = emptyList(),
    val checklistItems: List<ChecklistItem> = emptyList()
)

data class ChecklistItem(
    val id: Long = 0,
    val noteId: Long,
    val text: String,
    val isChecked: Boolean = false,
    val order: Int = 0
)
