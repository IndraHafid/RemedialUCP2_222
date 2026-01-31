package com.example.remedialucp2_222.ui.screens

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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.remedialucp2_222.data.database.entities.BookEntity
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBookScreen(
    book: BookEntity? = null,
    onSave: (BookEntity) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var title by remember { mutableStateOf(book?.title ?: "") }
    var isbn by remember { mutableStateOf(book?.isbn ?: "") }
    var publisher by remember { mutableStateOf(book?.publisher ?: "") }
    var publishYear by remember { mutableStateOf(book?.publishYear?.toString() ?: "") }
    var pageCount by remember { mutableStateOf(book?.pageCount?.toString() ?: "") }
    var language by remember { mutableStateOf(book?.language ?: "English") }
    var status by remember { mutableStateOf(book?.status ?: "available") }
    var location by remember { mutableStateOf(book?.location ?: "") }
    
    var titleError by remember { mutableStateOf<String?>(null) }
    var isbnError by remember { mutableStateOf<String?>(null) }
    var publishYearError by remember { mutableStateOf<String?>(null) }
    var pageCountError by remember { mutableStateOf<String?>(null) }
    
    val isEditing = book != null
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text(if (isEditing) "Edit Book" else "Add Book") },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        if (validateForm()) {
                            val newBook = BookEntity(
                                id = book?.id ?: UUID.randomUUID().toString(),
                                title = title,
                                isbn = isbn,
                                publisher = publisher,
                                publishYear = publishYear.toInt(),
                                pageCount = pageCount.toInt(),
                                language = language,
                                status = status,
                                location = location
                            )
                            onSave(newBook)
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        )
        
        // Title
        OutlinedTextField(
            value = title,
            onValueChange = { 
                title = it
                titleError = if (it.isBlank()) "Title cannot be empty" else null
            },
            label = { Text("Title *") },
            isError = titleError != null,
            supportingText = titleError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        
        // ISBN
        OutlinedTextField(
            value = isbn,
            onValueChange = { 
                isbn = it
                isbnError = if (it.isBlank()) "ISBN cannot be empty" else null
            },
            label = { Text("ISBN *") },
            isError = isbnError != null,
            supportingText = isbnError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Publisher
        OutlinedTextField(
            value = publisher,
            onValueChange = { publisher = it },
            label = { Text("Publisher") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Publish Year
            OutlinedTextField(
                value = publishYear,
                onValueChange = { 
                    publishYear = it
                    publishYearError = when {
                        it.isBlank() -> "Year cannot be empty"
                        it.toIntOrNull() == null -> "Invalid year"
                        it.toInt() < 0 || it.toInt() > Calendar.getInstance().get(Calendar.YEAR) -> "Invalid year"
                        else -> null
                    }
                },
                label = { Text("Publish Year *") },
                isError = publishYearError != null,
                supportingText = publishYearError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
            
            // Page Count
            OutlinedTextField(
                value = pageCount,
                onValueChange = { 
                    pageCount = it
                    pageCountError = when {
                        it.isBlank() -> "Page count cannot be empty"
                        it.toIntOrNull() == null -> "Invalid number"
                        it.toInt() <= 0 -> "Must be positive"
                        else -> null
                    }
                },
                label = { Text("Page Count *") },
                isError = pageCountError != null,
                supportingText = pageCountError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
        
        // Language
        OutlinedTextField(
            value = language,
            onValueChange = { language = it },
            label = { Text("Language") },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Status
        var statusExpanded by remember { mutableStateOf(false) }
        ExposedDropdownMenuBox(
            expanded = statusExpanded,
            onExpandedChange = { statusExpanded = !statusExpanded }
        ) {
            OutlinedTextField(
                value = status.replaceFirstChar { it.uppercaseChar() },
                onValueChange = { },
                readOnly = true,
                label = { Text("Status") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = statusExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = statusExpanded,
                onDismissRequest = { statusExpanded = false }
            ) {
                listOf("available", "borrowed", "reserved", "maintenance").forEach { statusOption ->
                    DropdownMenuItem(
                        text = { Text(statusOption.replaceFirstChar { it.uppercaseChar() }) },
                        onClick = {
                            status = statusOption
                            statusExpanded = false
                        }
                    )
                }
            }
        }
        
        // Location
        OutlinedTextField(
            value = location,
            onValueChange = { location = it },
            label = { Text("Location") },
            modifier = Modifier.fillMaxWidth()
        )
    }
    
    private fun validateForm(): Boolean {
        var isValid = true
        
        if (title.isBlank()) {
            titleError = "Title cannot be empty"
            isValid = false
        } else {
            titleError = null
        }
        
        if (isbn.isBlank()) {
            isbnError = "ISBN cannot be empty"
            isValid = false
        } else {
            isbnError = null
        }
        
        val year = publishYear.toIntOrNull()
        if (publishYear.isBlank()) {
            publishYearError = "Year cannot be empty"
            isValid = false
        } else if (year == null) {
            publishYearError = "Invalid year"
            isValid = false
        } else if (year < 0 || year > Calendar.getInstance().get(Calendar.YEAR)) {
            publishYearError = "Invalid year"
            isValid = false
        } else {
            publishYearError = null
        }
        
        val pages = pageCount.toIntOrNull()
        if (pageCount.isBlank()) {
            pageCountError = "Page count cannot be empty"
            isValid = false
        } else if (pages == null) {
            pageCountError = "Invalid number"
            isValid = false
        } else if (pages <= 0) {
            pageCountError = "Must be positive"
            isValid = false
        } else {
            pageCountError = null
        }
        
        return isValid
    }
}
