package com.example.remedialucp2_222.ui.screens

import androidx.compose.foundation.layout.*
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
import com.example.remedialucp2_222.ui.viewmodel.BookViewModel
import com.example.remedialucp2_222.utils.Resource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    bookViewModel: BookViewModel = hiltViewModel(),
    onNavigateToBooks: () -> Unit = {},
    onNavigateToCategories: () -> Unit = {},
    onNavigateToAuthors: () -> Unit = {}
) {
    val books by bookViewModel.books.collectAsStateWithLifecycle()
    val isLoading by bookViewModel.isLoading.collectAsStateWithLifecycle()
    
    LaunchedEffect(Unit) {
        bookViewModel.loadBooks()
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "Library Dashboard",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            // Statistics Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Total Books",
                    value = books.size.toString(),
                    icon = Icons.Default.Book,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBooks
                )
                
                StatCard(
                    title = "Available",
                    value = books.count { it.status == "available" }.toString(),
                    icon = Icons.Default.CheckCircle,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBooks
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StatCard(
                    title = "Borrowed",
                    value = books.count { it.status == "borrowed" }.toString(),
                    icon = Icons.Default.Person,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBooks
                )
                
                StatCard(
                    title = "Reserved",
                    value = books.count { it.status == "reserved" }.toString(),
                    icon = Icons.Default.Bookmark,
                    modifier = Modifier.weight(1f),
                    onClick = onNavigateToBooks
                )
            }
            
            // Quick Actions
            Text(
                text = "Quick Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ActionCard(
                    title = "Manage Books",
                    description = "Add, edit, or remove books from the library",
                    icon = Icons.Default.Book,
                    onClick = onNavigateToBooks
                )
                
                ActionCard(
                    title = "Manage Categories",
                    description = "Organize books with hierarchical categories",
                    icon = Icons.Default.Category,
                    onClick = onNavigateToCategories
                )
                
                ActionCard(
                    title = "Manage Authors",
                    description = "Add and manage author information",
                    icon = Icons.Default.Person,
                    onClick = onNavigateToAuthors
                )
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ActionCard(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
