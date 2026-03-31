package com.maharana.notesapp.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.maharana.notesapp.data.local.entity.Note
import com.maharana.notesapp.data.repository.NoteRepository
import com.maharana.notesapp.utils.ThemeSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    private val repository: NoteRepository,
    private val themeSettings: ThemeSettings
) : ViewModel() {

    private val _notes = MutableStateFlow<List<Note>>(emptyList())
    val notes = _notes.asStateFlow()

    private val _event = Channel<NotesEvent>()
    val event = _event.receiveAsFlow()

    val isDarkMode = themeSettings.isDarkMode

    init {
        viewModelScope.launch {
            repository.getAllNotes().collect { notesList ->
                _notes.value = notesList
            }
        }
    }

    fun onEvent(event: NotesEvent) {
        when (event) {
            is NotesEvent.DeleteNote -> {
                viewModelScope.launch {
                    repository.deleteNote(event.note)
                }
            }
            is NotesEvent.RestoreNote -> {
                viewModelScope.launch {
                    repository.insertNote(event.note)
                }
            }
            is NotesEvent.ToggleTheme -> {
                themeSettings.toggleTheme()
            }
        }
    }
}

sealed class NotesEvent {
    data class DeleteNote(val note: Note) : NotesEvent()
    data class RestoreNote(val note: Note) : NotesEvent()
    object ToggleTheme : NotesEvent()
}
