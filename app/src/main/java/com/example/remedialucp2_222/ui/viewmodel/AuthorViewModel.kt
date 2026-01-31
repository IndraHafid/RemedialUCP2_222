package com.example.remedialucp2_222.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remedialucp2_222.data.database.entities.AuthorEntity
import com.example.remedialucp2_222.data.repository.AuthorRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthorViewModel @Inject constructor(
    private val authorRepository: AuthorRepository
) : ViewModel() {
    
    private val _authors = MutableStateFlow<List<AuthorEntity>>(emptyList())
    val authors: StateFlow<List<AuthorEntity>> = _authors.asStateFlow()
    
    private val _searchResults = MutableStateFlow<List<AuthorEntity>>(emptyList())
    val searchResults: StateFlow<List<AuthorEntity>> = _searchResults.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    init {
        loadAuthors()
    }
    
    fun loadAuthors() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            authorRepository.getAllAuthors().collect { authorList ->
                _authors.value = authorList
                _isLoading.value = false
            }
        }
    }
    
    fun searchAuthors(query: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            authorRepository.searchAuthorsByName(query).collect { results ->
                _searchResults.value = results
                _isLoading.value = false
            }
        }
    }
    
    fun addAuthor(author: AuthorEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = authorRepository.insertAuthor(author)
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to add author"
            }
            
            _isLoading.value = false
        }
    }
    
    fun updateAuthor(author: AuthorEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = authorRepository.updateAuthor(author)
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to update author"
            }
            
            _isLoading.value = false
        }
    }
    
    fun deleteAuthor(authorId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = authorRepository.softDeleteAuthor(authorId)
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to delete author"
            }
            
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
