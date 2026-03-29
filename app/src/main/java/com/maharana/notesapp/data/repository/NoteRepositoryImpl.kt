package com.maharana.notesapp.data.repository

import com.maharana.notesapp.data.local.AppDatabase
import com.maharana.notesapp.data.local.dao.NoteDao
import com.maharana.notesapp.data.local.dao.ChecklistItemDao
import com.maharana.notesapp.data.local.entity.Note
import com.maharana.notesapp.data.local.entity.ChecklistItem
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NoteRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao,
    private val checklistItemDao: ChecklistItemDao
) : NoteRepository {

    override fun getAllNotes(): Flow<List<Note>> = noteDao.getAllNotes()

    override suspend fun getNoteById(id: Long): Note? = noteDao.getNoteById(id)

    override suspend fun insertNote(note: Note): Long = noteDao.insertNote(note)

    override suspend fun updateNote(note: Note) = noteDao.updateNote(note)

    override suspend fun deleteNote(note: Note) = noteDao.deleteNote(note)

    override suspend fun deleteNoteById(id: Long) = noteDao.deleteNoteById(id)

    override fun getChecklistItemsForNote(noteId: Long): Flow<List<ChecklistItem>> =
        checklistItemDao.getChecklistItemsForNote(noteId)

    override suspend fun insertChecklistItem(item: ChecklistItem): Long =
        checklistItemDao.insertChecklistItem(item)

    override suspend fun updateChecklistItem(item: ChecklistItem) =
        checklistItemDao.updateChecklistItem(item)

    override suspend fun deleteChecklistItem(item: ChecklistItem) =
        checklistItemDao.deleteChecklistItem(item)
}
