package com.example.remedialucp2_222.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.remedialucp2_222.data.database.entities.BookEntity
import com.example.remedialucp2_222.ui.viewmodel.BookViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookListScreen(
    viewModel: BookViewModel = hiltViewModel(),
    onBookClick: (BookEntity) -> Unit = {},
    onAddBook: () -> Unit = {}
) {
    val books by viewModel.books.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    
    var showDeleteDialog by remember { mutableStateOf<BookEntity?>(null) }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            // Show snackbar or handle error
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Library Books") },
            actions = {
                IconButton(onClick = onAddBook) {
                    Icon(Icons.Default.Add, contentDescription = "Add Book")
                }
            }
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(books) { book ->
                    BookCard(
                        book = book,
                        onBookClick = onBookClick,
                        onDeleteClick = { showDeleteDialog = book },
                        onStatusChange = { newStatus ->
                            viewModel.updateBookStatus(book.id, newStatus)
                        }
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { book ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Book") },
            text = { Text("Are you sure you want to delete \"${book.title}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteBook(book.id)
                        showDeleteDialog = null
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun BookCard(
    book: BookEntity,
    onBookClick: (BookEntity) -> Unit,
    onDeleteClick: (BookEntity) -> Unit,
    onStatusChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onBookClick(book) },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "ISBN: ${book.isbn}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                StatusChip(
                    status = book.status,
                    onStatusChange = onStatusChange
                )
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    Text("Publisher: ${book.publisher}")
                    Text("Year: ${book.publishYear}")
                    Text("Pages: ${book.pageCount}")
                    Text("Language: ${book.language}")
                    Text("Location: ${book.location}")
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row {
                        IconButton(onClick = onDeleteClick) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete")
                        }
                    }
                }
            }
            
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier.align(Alignment.End)
            ) {
                Icon(
                    if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = if (expanded) "Show less" else "Show more"
                )
            }
        }
    }
}

@Composable
fun StatusChip(
    status: String,
    onStatusChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        FilterChip(
            onClick = { expanded = true },
            label = { Text(status.replaceFirstChar { it.uppercaseChar() }) },
            selected = true,
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = when (status) {
                    "available" -> MaterialTheme.colorScheme.primaryContainer
                    "borrowed" -> MaterialTheme.colorScheme.errorContainer
                    "reserved" -> MaterialTheme.colorScheme.secondaryContainer
                    "maintenance" -> MaterialTheme.colorScheme.tertiaryContainer
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
            ),
            modifier = Modifier.menuAnchor()
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            listOf("available", "borrowed", "reserved", "maintenance").forEach { statusOption ->
                DropdownMenuItem(
                    text = { Text(statusOption.replaceFirstChar { it.uppercaseChar() }) },
                    onClick = {
                        onStatusChange(statusOption)
                        expanded = false
                    }
                )
            }
        }
    }
}
