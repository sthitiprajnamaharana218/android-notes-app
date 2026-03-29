package com.maharana.notesapp.presentation.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.maharana.notesapp.data.local.entity.ChecklistItem
import com.maharana.notesapp.presentation.viewmodel.AddEditNoteViewModel
import com.maharana.notesapp.presentation.viewmodel.AddEditNoteEvent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditNoteScreen(
    navController: NavController,
    noteId: Long,
    viewModel: AddEditNoteViewModel = hiltViewModel()
) {
    val title by viewModel.title.collectAsState()
    val content by viewModel.content.collectAsState()
    val color by viewModel.color.collectAsState()
    val audioPath by viewModel.audioPath.collectAsState()
    val images by viewModel.images.collectAsState()
    val checklistItems by viewModel.checklistItems.collectAsState()
    val event by viewModel.event.collectAsState(initial = null)

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    LaunchedEffect(noteId) {
        if (noteId != -1L) {
            viewModel.loadNote(noteId)
        }
    }

    LaunchedEffect(event) {
        event?.let { event ->
            when (event) {
                is AddEditNoteEvent.NoteSaved -> {
                    navController.navigateUp()
                }
                is AddEditNoteEvent.NoteDeleted -> {
                    navController.navigateUp()
                }
                is AddEditNoteEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(
            initialOffsetY = { -it },
            animationSpec = tween(500, easing = EaseOutBack)
        ) + fadeIn(animationSpec = tween(500)),
        exit = slideOutVertically(
            targetOffsetY = { -it },
            animationSpec = tween(500, easing = EaseInBack)
        ) + fadeOut(animationSpec = tween(500))
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { viewModel.saveNote() },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = "Save note"
                    )
                }
            },
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(color))
                    .padding(paddingValues)
                    .padding(16.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { viewModel.onTitleChange(it) },
                    label = { Text("Title") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                
                OutlinedTextField(
                    value = content,
                    onValueChange = { viewModel.onContentChange(it) },
                    label = { Text("Content") },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                
                ChecklistSection(
                    checklistItems = checklistItems,
                    onAddItem = { viewModel.addChecklistItem(it) },
                    onUpdateItem = { viewModel.updateChecklistItem(it) },
                    onDeleteItem = { viewModel.deleteChecklistItem(it) }
                )
                
                AudioRecorderSection(
                    audioPath = audioPath,
                    onAudioPathChange = { viewModel.onAudioPathChange(it) }
                )
                
                ImageAttachmentSection(
                    images = images,
                    onImagesChange = { viewModel.onImagesChange(it) }
                )
                
                ColorPickerSection(
                    selectedColor = color,
                    onColorChange = { viewModel.onColorChange(it) }
                )
            }
        }
    }
}

@Composable
fun ChecklistSection(
    checklistItems: List<ChecklistItem>,
    onAddItem: (String) -> Unit,
    onUpdateItem: (ChecklistItem) -> Unit,
    onDeleteItem: (ChecklistItem) -> Unit
) {
    var newItemText by remember { mutableStateOf("") }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Checklist",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = {
                        if (newItemText.isNotBlank()) {
                            onAddItem(newItemText)
                            newItemText = ""
                        }
                    }
                ) {
                    Text("Add Item")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = newItemText,
                onValueChange = { newItemText = it },
                label = { Text("New checklist item") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent
                ),
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            checklistItems.forEach { item ->
                ChecklistItemRow(
                    item = item,
                    onUpdate = { onUpdateItem(item) },
                    onDelete = { onDeleteItem(item) }
                )
            }
        }
    }
}

@Composable
fun ChecklistItemRow(
    item: ChecklistItem,
    onUpdate: (ChecklistItem) -> Unit,
    onDelete: (ChecklistItem) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { 
                    onUpdate(item.copy(isChecked = it))
                }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = item.text,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f)
            )
        }
        IconButton(
            onClick = { onDelete(item) }
        ) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "Delete item",
                tint = MaterialTheme.colorScheme.error
            )
        }
    }
}

@Composable
fun AudioRecorderSection(
    audioPath: String?,
    onAudioPathChange: (String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Voice Recording",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Record audio",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(8.dp))
                if (audioPath != null) {
                    Text(
                        text = "Audio recorded",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    Text(
                        text = "Tap to record",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun ImageAttachmentSection(
    images: List<String>,
    onImagesChange: (List<String>) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Images",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TextButton(
                    onClick = { /* TODO: Implement image picker */ }
                ) {
                    Text("Add Images")
                }
            }
            
            if (images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(images) { imagePath ->
                        Card(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(MaterialTheme.shapes.small)
                        ) {
                            // TODO: Load and display image
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(MaterialTheme.colorScheme.surfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Image",
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ColorPickerSection(
    selectedColor: Int,
    onColorChange: (Int) -> Unit
) {
    val colors = listOf(
        0xFFE3F2FD.toInt(), // Blue
        0xFFF3E5F5.toInt(), // Pink
        0xFFE8F5E8.toInt(), // Green
        0xFFFFF8E1.toInt(), // Yellow
        0xFFE0F2F1.toInt()  // Purple
    )

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Note Color",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                colors.forEach { color ->
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(Color(color))
                            .clip(MaterialTheme.shapes.small)
                            .clickable { onColorChange(color) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedColor == color) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Selected",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
