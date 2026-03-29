package com.maharana.notesapp.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.maharana.notesapp.presentation.screens.NotesListScreen
import com.maharana.notesapp.presentation.screens.AddEditNoteScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "notes_list"
    ) {
        composable("notes_list") {
            NotesListScreen(
                navController = navController
            )
        }
        composable("add_edit_note/{noteId}") { backStackEntry ->
            val noteId = backStackEntry.arguments?.getString("noteId")?.toLongOrNull() ?: -1L
            AddEditNoteScreen(
                navController = navController,
                noteId = noteId
            )
        }
    }
}
