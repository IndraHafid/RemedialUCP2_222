package com.example.remedialucp2_222.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remedialucp2_222.data.database.entities.BookEntity
import com.example.remedialucp2_222.data.repository.BookRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookViewModel @Inject constructor(
    private val bookRepository: BookRepository
) : ViewModel() {
    
    private val _books = MutableStateFlow<List<BookEntity>>(emptyList())
    val books: StateFlow<List<BookEntity>> = _books.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadBooks()
    }
    
    fun loadBooks() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            bookRepository.getAllBooks().collect { bookList ->
                _books.value = bookList
                _isLoading.value = false
            }
        }
    }
    
    fun addBook(book: BookEntity, authorIds: List<String> = emptyList()) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = bookRepository.insertBookWithAuthors(book, authorIds, List(authorIds.size) { "author" })
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to add book"
            }
            
            _isLoading.value = false
        }
    }
    
    fun updateBook(book: BookEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = bookRepository.updateBook(book)
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to update book"
            }
            
            _isLoading.value = false
        }
    }
    
    fun deleteBook(bookId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = bookRepository.softDeleteBook(bookId)
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to delete book"
            }
            
            _isLoading.value = false
        }
    }
    
    fun updateBookStatus(bookId: String, newStatus: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = bookRepository.updateBookStatus(bookId, newStatus)
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to update book status"
            }
            
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
