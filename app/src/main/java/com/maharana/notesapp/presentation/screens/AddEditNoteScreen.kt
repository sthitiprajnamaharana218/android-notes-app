package com.maharana.notesapp.presentation.screens

import android.Manifest
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.maharana.notesapp.data.local.entity.ChecklistItem
import com.maharana.notesapp.presentation.viewmodel.AddEditNoteViewModel
import com.maharana.notesapp.presentation.viewmodel.AddEditNoteEvent
import com.maharana.notesapp.utils.AudioPlayer
import com.maharana.notesapp.utils.AudioRecorder
import com.maharana.notesapp.utils.ImageUtils
import com.maharana.notesapp.utils.PermissionsUtil
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

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()
    
    val audioRecorder = remember { AudioRecorder(context) }
    val audioPlayer = remember { AudioPlayer(context) }
    val imageUtils = remember { ImageUtils(context) }
    
    var isRecording by remember { mutableStateOf(false) }
    var isPlaying by remember { mutableStateOf(false) }
    var showImageSourceDialog by remember { mutableStateOf(false) }
    var cameraImageUri by remember { mutableStateOf<Uri?>(null) }

    // Image Picker Launcher
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(),
        onResult = { uris ->
            val currentImages = images.toMutableList()
            uris.forEach { uri ->
                imageUtils.saveImageToInternalStorage(uri)?.let { path ->
                    currentImages.add(path)
                }
            }
            viewModel.onImagesChange(currentImages)
        }
    )

    // Camera Launcher
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture(),
        onResult = { success ->
            if (success) {
                cameraImageUri?.let { uri ->
                    val currentImages = images.toMutableList()
                    // Since it's already in our app's directory (via createImageUri), 
                    // we can just use the path or copy it if needed. 
                    // For simplicity, let's just use the URI string for now or map it to path.
                    // Actually, let's find the file path from URI or use the one we created.
                    val path = imageUtils.saveImageToInternalStorage(uri)
                    if (path != null) {
                        currentImages.add(path)
                        viewModel.onImagesChange(currentImages)
                    }
                }
            }
        }
    )

    // Permission Launchers
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val path = audioRecorder.startRecording()
                if (path != null) {
                    isRecording = true
                }
            } else {
                scope.launch { snackbarHostState.showSnackbar("Permission denied for audio") }
            }
        }
    )

    val cameraPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { isGranted ->
            if (isGranted) {
                val uri = imageUtils.createImageUri()
                cameraImageUri = uri
                if (uri != null) {
                    cameraLauncher.launch(uri)
                }
            } else {
                scope.launch { snackbarHostState.showSnackbar("Permission denied for camera") }
            }
        }
    )

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
    
    DisposableEffect(Unit) {
        onDispose {
            audioPlayer.stopPlaying()
            audioRecorder.cancelRecording()
        }
    }

    if (showImageSourceDialog) {
        AlertDialog(
            onDismissRequest = { showImageSourceDialog = false },
            title = { Text("Add Image") },
            text = { Text("Choose image source") },
            confirmButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    if (PermissionsUtil.checkCameraPermission(context)) {
                        val uri = imageUtils.createImageUri()
                        cameraImageUri = uri
                        if (uri != null) {
                            cameraLauncher.launch(uri)
                        }
                    } else {
                        cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                }) {
                    Text("Camera")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showImageSourceDialog = false
                    imagePickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                }) {
                    Text("Gallery")
                }
            }
        )
    }

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
                isRecording = isRecording,
                isPlaying = isPlaying,
                onToggleRecording = {
                    if (isRecording) {
                        val path = audioRecorder.stopRecording()
                        viewModel.onAudioPathChange(path)
                        isRecording = false
                    } else {
                        if (PermissionsUtil.checkAudioPermission(context)) {
                            val path = audioRecorder.startRecording()
                            if (path != null) isRecording = true
                        } else {
                            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                        }
                    }
                },
                onTogglePlay = {
                    if (isPlaying) {
                        audioPlayer.stopPlaying()
                        isPlaying = false
                    } else {
                        audioPath?.let { path ->
                            isPlaying = true
                            audioPlayer.startPlaying(path) {
                                isPlaying = false
                            }
                        }
                    }
                },
                onDeleteAudio = {
                    audioPlayer.stopPlaying()
                    isPlaying = false
                    viewModel.onAudioPathChange(null)
                }
            )
            
            ImageAttachmentSection(
                images = images,
                onAddImages = {
                    showImageSourceDialog = true
                },
                onRemoveImage = { path ->
                    val newList = images.toMutableList()
                    newList.remove(path)
                    viewModel.onImagesChange(newList)
                }
            )
            
            ColorPickerSection(
                selectedColor = color,
                onColorChange = { viewModel.onColorChange(it) }
            )
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
    isRecording: Boolean,
    isPlaying: Boolean,
    onToggleRecording: () -> Unit,
    onTogglePlay: () -> Unit,
    onDeleteAudio: () -> Unit
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
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Voice Recording",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = if (isRecording) "Recording..." else if (isPlaying) "Playing..." else if (audioPath != null) "Audio recorded" else "Tap mic to record",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isRecording) MaterialTheme.colorScheme.error else if (audioPath != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (audioPath != null && !isRecording) {
                    IconButton(onClick = onTogglePlay) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                    IconButton(onClick = onDeleteAudio) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete audio",
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
                
                if (!isPlaying) {
                    IconButton(onClick = onToggleRecording) {
                        Icon(
                            imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                            contentDescription = if (isRecording) "Stop" else "Record",
                            tint = if (isRecording) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ImageAttachmentSection(
    images: List<String>,
    onAddImages: () -> Unit,
    onRemoveImage: (String) -> Unit
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
                TextButton(onClick = onAddImages) {
                    Text("Add Images")
                }
            }
            
            if (images.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(images) { imagePath ->
                        Box(
                            modifier = Modifier.size(100.dp)
                        ) {
                            AsyncImage(
                                model = imagePath,
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .clip(MaterialTheme.shapes.small),
                                contentScale = ContentScale.Crop
                            )
                            IconButton(
                                onClick = { onRemoveImage(imagePath) },
                                modifier = Modifier
                                    .align(Alignment.TopEnd)
                                    .size(24.dp)
                                    .background(Color.Black.copy(alpha = 0.5f), CircleShape)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Remove",
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
