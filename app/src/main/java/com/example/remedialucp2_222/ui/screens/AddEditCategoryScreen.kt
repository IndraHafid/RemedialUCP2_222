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
import com.example.remedialucp2_222.data.database.entities.CategoryEntity
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditCategoryScreen(
    category: CategoryEntity? = null,
    onSave: (CategoryEntity) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var name by remember { mutableStateOf(category?.name ?: "") }
    var description by remember { mutableStateOf(category?.description ?: "") }
    var parentId by remember { mutableStateOf(category?.parentId) }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    
    val isEditing = category != null
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text(if (isEditing) "Edit Category" else "Add Category") },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        if (validateForm()) {
                            val newCategory = CategoryEntity(
                                id = category?.id ?: UUID.randomUUID().toString(),
                                name = name,
                                description = description.ifBlank { null },
                                parentId = parentId,
                                level = 0 // This should be calculated based on parent
                            )
                            onSave(newCategory)
                        }
                    }
                ) {
                    Text("Save")
                }
            }
        )
        
        // Name
        OutlinedTextField(
            value = name,
            onValueChange = { 
                name = it
                nameError = if (it.isBlank()) "Category name cannot be empty" else null
            },
            label = { Text("Category Name *") },
            isError = nameError != null,
            supportingText = nameError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Description
        OutlinedTextField(
            value = description,
            onValueChange = { description = it },
            label = { Text("Description") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
        )
        
        // Parent Category (simplified - in real app would show dropdown)
        OutlinedTextField(
            value = parentId ?: "",
            onValueChange = { parentId = if (it.isBlank()) null else it },
            label = { Text("Parent Category ID") },
            modifier = Modifier.fillMaxWidth()
        )
    }
    
    private fun validateForm(): Boolean {
        var isValid = true
        
        if (name.isBlank()) {
            nameError = "Category name cannot be empty"
            isValid = false
        } else {
            nameError = null
        }
        
        return isValid
    }
}
