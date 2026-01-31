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
import com.example.remedialucp2_222.data.database.entities.AuthorEntity
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditAuthorScreen(
    author: AuthorEntity? = null,
    onSave: (AuthorEntity) -> Unit = {},
    onCancel: () -> Unit = {}
) {
    var name by remember { mutableStateOf(author?.name ?: "") }
    var email by remember { mutableStateOf(author?.email ?: "") }
    var biography by remember { mutableStateOf(author?.biography ?: "") }
    var nationality by remember { mutableStateOf(author?.nationality ?: "") }
    
    var nameError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    
    val isEditing = author != null
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        TopAppBar(
            title = { Text(if (isEditing) "Edit Author" else "Add Author") },
            navigationIcon = {
                IconButton(onClick = onCancel) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                }
            },
            actions = {
                TextButton(
                    onClick = {
                        if (validateForm()) {
                            val newAuthor = AuthorEntity(
                                id = author?.id ?: UUID.randomUUID().toString(),
                                name = name,
                                email = email.ifBlank { null },
                                biography = biography.ifBlank { null },
                                nationality = nationality.ifBlank { null }
                            )
                            onSave(newAuthor)
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
                nameError = if (it.isBlank()) "Author name cannot be empty" else null
            },
            label = { Text("Author Name *") },
            isError = nameError != null,
            supportingText = nameError?.let { { Text(it) } },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Email
        OutlinedTextField(
            value = email,
            onValueChange = { 
                email = it
                emailError = if (it.isNotBlank() && !isValidEmail(it)) {
                    "Invalid email format"
                } else null
            },
            label = { Text("Email") },
            isError = emailError != null,
            supportingText = emailError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )
        
        // Nationality
        OutlinedTextField(
            value = nationality,
            onValueChange = { nationality = it },
            label = { Text("Nationality") },
            modifier = Modifier.fillMaxWidth()
        )
        
        // Biography
        OutlinedTextField(
            value = biography,
            onValueChange = { biography = it },
            label = { Text("Biography") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 4
        )
    }
    
    private fun validateForm(): Boolean {
        var isValid = true
        
        if (name.isBlank()) {
            nameError = "Author name cannot be empty"
            isValid = false
        } else {
            nameError = null
        }
        
        if (email.isNotBlank() && !isValidEmail(email)) {
            emailError = "Invalid email format"
            isValid = false
        } else {
            emailError = null
        }
        
        return isValid
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
