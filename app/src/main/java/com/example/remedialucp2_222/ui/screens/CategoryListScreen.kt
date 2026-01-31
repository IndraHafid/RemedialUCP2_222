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
import com.example.remedialucp2_222.data.database.entities.CategoryEntity
import com.example.remedialucp2_222.ui.viewmodel.CategoryViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    viewModel: CategoryViewModel = hiltViewModel(),
    onCategoryClick: (CategoryEntity) -> Unit = {},
    onAddCategory: () -> Unit = {}
) {
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val categoryBooks by viewModel.categoryBooks.collectAsStateWithLifecycle()
    val selectedCategoryId by viewModel.selectedCategoryId.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    
    var showDeleteDialog by remember { mutableStateOf<CategoryEntity?>(null) }
    
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            // Show snackbar or handle error
        }
    }
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        TopAppBar(
            title = { Text("Categories") },
            actions = {
                IconButton(onClick = onAddCategory) {
                    Icon(Icons.Default.Add, contentDescription = "Add Category")
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
            Row(modifier = Modifier.fillMaxSize()) {
                // Categories list
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { category ->
                        CategoryCard(
                            category = category,
                            isSelected = category.id == selectedCategoryId,
                            onCategoryClick = { 
                                viewModel.selectCategory(category.id)
                                onCategoryClick(category)
                            },
                            onDeleteClick = { showDeleteDialog = category }
                        )
                    }
                }
                
                // Books in selected category
                if (selectedCategoryId != null) {
                    Divider(
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(1.dp)
                    )
                    
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Text(
                                text = "Books in Category",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        
                        items(categoryBooks) { book ->
                            BookSummaryCard(book = book)
                        }
                    }
                }
            }
        }
    }
    
    // Delete confirmation dialog
    showDeleteDialog?.let { category ->
        DeleteCategoryDialog(
            category = category,
            onDismiss = { showDeleteDialog = null },
            onConfirm = { deleteBooks, moveToUncategorized ->
                viewModel.deleteCategory(category.id, deleteBooks, moveToUncategorized)
                showDeleteDialog = null
            }
        )
    }
}

@Composable
fun CategoryCard(
    category: CategoryEntity,
    isSelected: Boolean,
    onCategoryClick: () -> Unit,
    onDeleteClick: (CategoryEntity) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCategoryClick() },
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 4.dp
        ),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
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
                        text = category.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    category.description?.let { description ->
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = "Level: ${category.level}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(onClick = { onDeleteClick(category) }) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete")
                }
            }
        }
    }
}

@Composable
fun BookSummaryCard(book: com.example.remedialucp2_222.data.database.entities.BookEntity) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Text(
                text = book.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "ISBN: ${book.isbn}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Status: ${book.status.replaceFirstChar { it.uppercaseChar() }}",
                style = MaterialTheme.typography.bodySmall,
                color = when (book.status) {
                    "available" -> MaterialTheme.colorScheme.primary
                    "borrowed" -> MaterialTheme.colorScheme.error
                    "reserved" -> MaterialTheme.colorScheme.secondary
                    "maintenance" -> MaterialTheme.colorScheme.tertiary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                }
            )
        }
    }
}

@Composable
fun DeleteCategoryDialog(
    category: CategoryEntity,
    onDismiss: () -> Unit,
    onConfirm: (Boolean, Boolean) -> Unit
) {
    var deleteBooks by remember { mutableStateOf(false) }
    var moveToUncategorized by remember { mutableStateOf(true) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Delete Category") },
        text = {
            Column {
                Text("Are you sure you want to delete \"${category.name}\"?")
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("What should happen to books in this category?")
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = deleteBooks,
                        onCheckedChange = { 
                            deleteBooks = it
                            if (it) moveToUncategorized = false
                        }
                    )
                    Text("Delete books in this category")
                }
                
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = moveToUncategorized,
                        onCheckedChange = { 
                            moveToUncategorized = it
                            if (it) deleteBooks = false
                        }
                    )
                    Text("Move to Uncategorized")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(deleteBooks, moveToUncategorized)
                }
            ) {
                Text("Delete")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
