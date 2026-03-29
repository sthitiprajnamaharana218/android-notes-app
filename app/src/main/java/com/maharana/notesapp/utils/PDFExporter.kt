package com.maharana.notesapp.utils

import android.content.Context
import android.os.Environment
import com.maharana.notesapp.data.local.entity.Note
import com.maharana.notesapp.data.local.entity.ChecklistItem
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class PDFExporter(private val context: Context) {
    
    fun exportNoteToPDF(note: Note): String? {
        return try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "note_${note.title}_$timestamp.pdf"
            
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val pdfFile = File(downloadsDir, fileName)
            
            val content = generatePDFContent(note)
            val outputStream = FileOutputStream(pdfFile)
            outputStream.write(content.toByteArray())
            outputStream.close()
            
            pdfFile.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    private fun generatePDFContent(note: Note): String {
        val content = StringBuilder()
        
        content.appendLine("Note: ${note.title}")
        content.appendLine("Created: ${SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(note.timestamp))}")
        content.appendLine("=" .repeat(50))
        content.appendLine()
        content.appendLine(note.content)
        
        if (note.checklistItems.isNotEmpty()) {
            content.appendLine()
            content.appendLine("Checklist:")
            note.checklistItems.forEach { item ->
                val status = if (item.isChecked) "✓" else "○"
                content.appendLine("$status ${item.text}")
            }
        }
        
        if (note.audioPath != null) {
            content.appendLine()
            content.appendLine("Voice note: ${note.audioPath}")
        }
        
        if (note.images.isNotEmpty()) {
            content.appendLine()
            content.appendLine("Images:")
            note.images.forEachIndexed { index, imagePath ->
                content.appendLine("${index + 1}. $imagePath")
            }
        }
        
        return content.toString()
    }
    
    private fun StringBuilder.appendLine(text: String = "") {
        append(text)
        append("\n")
    }
}
