package com.example.remedialucp2_222.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remedialucp2_222.data.database.entities.BookEntity
import com.example.remedialucp2_222.data.database.entities.CategoryEntity
import com.example.remedialucp2_222.data.database.entities.AuthorEntity
import com.example.remedialucp2_222.ui.screens.*

@Composable
fun LibraryNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = "dashboard"
    ) {
        composable("dashboard") {
            DashboardScreen(
                onNavigateToBooks = {
                    navController.navigate("books")
                },
                onNavigateToCategories = {
                    navController.navigate("categories")
                },
                onNavigateToAuthors = {
                    navController.navigate("authors")
                }
            )
        }
        
        composable("books") {
            BookListScreen(
                onBookClick = { book ->
                    navController.navigate("edit_book/${book.id}")
                },
                onAddBook = {
                    navController.navigate("add_book")
                }
            )
        }
        
        composable("add_book") {
            AddEditBookScreen(
                onSave = { book ->
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("edit_book/{bookId}") { backStackEntry ->
            val bookId = backStackEntry.arguments?.getString("bookId")
            // In a real app, you would load the book from ViewModel
            AddEditBookScreen(
                book = null, // Load book by ID from ViewModel
                onSave = { book ->
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("categories") {
            CategoryListScreen(
                onCategoryClick = { category ->
                    // Could navigate to category details if needed
                },
                onAddCategory = {
                    navController.navigate("add_category")
                }
            )
        }
        
        composable("add_category") {
            AddEditCategoryScreen(
                onSave = { category ->
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
        
        composable("authors") {
            AuthorListScreen(
                onAuthorClick = { author ->
                    // Could navigate to author details if needed
                },
                onAddAuthor = {
                    navController.navigate("add_author")
                }
            )
        }
        
        composable("add_author") {
            AddEditAuthorScreen(
                onSave = { author ->
                    navController.popBackStack()
                },
                onCancel = {
                    navController.popBackStack()
                }
            )
        }
    }
}
