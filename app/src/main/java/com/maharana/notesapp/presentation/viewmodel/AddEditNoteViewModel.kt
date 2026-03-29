package com.maharana.notesapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maharana.notesapp.data.local.entity.Note
import com.maharana.notesapp.data.local.entity.ChecklistItem
import com.maharana.notesapp.data.repository.NoteRepository
import com.maharana.notesapp.presentation.viewmodel.AddEditNoteEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditNoteViewModel @Inject constructor(
    private val repository: NoteRepository
) : ViewModel() {

    private val _title = MutableStateFlow("")
    val title = _title.asStateFlow()

    private val _content = MutableStateFlow("")
    val content = _content.asStateFlow()

    private val _color = MutableStateFlow(0xFFE3F2FD.toInt())
    val color = _color.asStateFlow()

    private val _audioPath = MutableStateFlow<String?>(null)
    val audioPath = _audioPath.asStateFlow()

    private val _images = MutableStateFlow<List<String>>(emptyList())
    val images = _images.asStateFlow()

    private val _checklistItems = MutableStateFlow<List<ChecklistItem>>(emptyList())
    val checklistItems = _checklistItems.asStateFlow()

    private val _event = Channel<AddEditNoteEvent>()
    val event = _event.receiveAsFlow()

    private var currentNoteId: Long? = null

    fun onTitleChange(newTitle: String) {
        _title.value = newTitle
    }

    fun onContentChange(newContent: String) {
        _content.value = newContent
    }

    fun onColorChange(newColor: Int) {
        _color.value = newColor
    }

    fun onAudioPathChange(audioPath: String?) {
        _audioPath.value = audioPath
    }

    fun onImagesChange(images: List<String>) {
        _images.value = images
    }

    fun addChecklistItem(text: String) {
        val newItems = _checklistItems.value.toMutableList()
        newItems.add(
            ChecklistItem(
                id = 0,
                noteId = currentNoteId ?: 0,
                text = text,
                order = newItems.size
            )
        )
        _checklistItems.value = newItems
    }

    fun updateChecklistItem(item: ChecklistItem) {
        val newItems = _checklistItems.value.toMutableList()
        val index = newItems.indexOf(item)
        if (index != -1) {
            newItems[index] = item
            _checklistItems.value = newItems
        }
    }

    fun deleteChecklistItem(item: ChecklistItem) {
        val newItems = _checklistItems.value.toMutableList()
        newItems.remove(item)
        _checklistItems.value = newItems
    }

    fun saveNote() {
        viewModelScope.launch {
            try {
                val note = Note(
                    id = currentNoteId ?: 0,
                    title = _title.value,
                    content = _content.value,
                    color = _color.value,
                    audioPath = _audioPath.value,
                    images = _images.value,
                    checklistItems = _checklistItems.value
                )

                if (currentNoteId == null) {
                    repository.insertNote(note)
                } else {
                    repository.updateNote(note)
                }

                _event.send(AddEditNoteEvent.NoteSaved)
            } catch (e: Exception) {
                _event.send(AddEditNoteEvent.ShowError("Failed to save note"))
            }
        }
    }

    fun loadNote(noteId: Long) {
        viewModelScope.launch {
            try {
                val note = repository.getNoteById(noteId)
                if (note != null) {
                    currentNoteId = note.id
                    _title.value = note.title
                    _content.value = note.content
                    _color.value = note.color
                    _audioPath.value = note.audioPath
                    _images.value = note.images
                    _checklistItems.value = note.checklistItems
                }
            } catch (e: Exception) {
                _event.send(AddEditNoteEvent.ShowError("Failed to load note"))
            }
        }
    }

    fun deleteNote() {
        viewModelScope.launch {
            try {
                currentNoteId?.let { id ->
                    repository.deleteNoteById(id)
                    _event.send(AddEditNoteEvent.NoteDeleted)
                }
            } catch (e: Exception) {
                _event.send(AddEditNoteEvent.ShowError("Failed to delete note"))
            }
        }
    }
}

sealed class AddEditNoteEvent {
    object NoteSaved : AddEditNoteEvent()
    object NoteDeleted : AddEditNoteEvent()
    data class ShowError(val message: String) : AddEditNoteEvent()
}
