package com.maharana.notesapp.data.repository

import com.maharana.notesapp.data.local.entity.Note
import com.maharana.notesapp.data.local.entity.ChecklistItem
import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun getAllNotes(): Flow<List<Note>>
    suspend fun getNoteById(id: Long): Note?
    suspend fun insertNote(note: Note): Long
    suspend fun updateNote(note: Note)
    suspend fun deleteNote(note: Note)
    suspend fun deleteNoteById(id: Long)
    
    fun getChecklistItemsForNote(noteId: Long): Flow<List<ChecklistItem>>
    suspend fun insertChecklistItem(item: ChecklistItem): Long
    suspend fun updateChecklistItem(item: ChecklistItem)
    suspend fun deleteChecklistItem(item: ChecklistItem)
}
