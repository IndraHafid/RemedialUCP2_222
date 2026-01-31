package com.example.remedialucp2_222.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.remedialucp2_222.data.database.entities.AuthorEntity
import com.example.remedialucp2_222.ui.viewmodel.AuthorViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthorListScreen(
    viewModel: AuthorViewModel = hiltViewModel(),
    onAuthorClick: (AuthorEntity) -> Unit = {},
    onAddAuthor: () -> Unit = {}
) {
    val authors by viewModel.authors.collectAsStateWithLifecycle()
    val searchResults by viewModel.searchResults.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    
    var searchQuery by remember { mutableStateOf("") }
    var isSearching by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf<AuthorEntity?>(null) }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            // Show snackbar or handle error
        }
    }
    
    LaunchedEffect(searchQuery) {
        if (searchQuery.isNotBlank()) {
            isSearching = true
            viewModel.searchAuthors(searchQuery)
        } else {
            isSearching = false
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Authors") },
            actions = {
                IconButton(onClick = onAddAuthor) {
                    Icon(Icons.Default.Add, contentDescription = "Add Author")
                }
            }
        )
        
        // Search bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("Search authors...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            singleLine = true
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
                val displayAuthors = if (isSearching) searchResults else authors
                
                items(displayAuthors) { author ->
                    AuthorCard(
                        author = author,
                        onAuthorClick = onAuthorClick,
                        onDeleteClick = { showDeleteDialog = author }
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { author ->
        AlertDialog(
            onDismissRequest = { showDeleteDialog = null },
            title = { Text("Delete Author") },
            text = { Text("Are you sure you want to delete \"${author.name}\"?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAuthor(author.id)
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
fun AuthorCard(
    author: AuthorEntity,
    onAuthorClick: (AuthorEntity) -> Unit,
    onDeleteClick: (AuthorEntity) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onAuthorClick(author) },
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
                        text = author.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    author.nationality?.let { nationality ->
                        Text(
                            text = nationality,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
            
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Column {
                    author.email?.let { email ->
                        Text("Email: $email")
                    }
                    author.biography?.let { biography ->
                        Text("Biography: $biography")
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
