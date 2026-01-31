package com.example.remedialucp2_222.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remedialucp2_222.data.database.entities.CategoryEntity
import com.example.remedialucp2_222.data.database.entities.BookEntity
import com.example.remedialucp2_222.data.repository.CategoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CategoryViewModel @Inject constructor(
    private val categoryRepository: CategoryRepository
) : ViewModel() {
    
    private val _categories = MutableStateFlow<List<CategoryEntity>>(emptyList())
    val categories: StateFlow<List<CategoryEntity>> = _categories.asStateFlow()
    
    private val _categoryBooks = MutableStateFlow<List<BookEntity>>(emptyList())
    val categoryBooks: StateFlow<List<BookEntity>> = _categoryBooks.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()
    
    private val _selectedCategoryId = MutableStateFlow<String?>(null)
    val selectedCategoryId: StateFlow<String?> = _selectedCategoryId.asStateFlow()
    
    init {
        loadCategories()
        
        // Load books when selected category changes
        viewModelScope.launch {
            _selectedCategoryId.collect { categoryId ->
                if (categoryId != null) {
                    loadCategoryBooks(categoryId)
                } else {
                    _categoryBooks.value = emptyList()
                }
            }
        }
    }
    
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            categoryRepository.getAllCategories().collect { categoryList ->
                _categories.value = categoryList
                _isLoading.value = false
            }
        }
    }
    
    fun selectCategory(categoryId: String?) {
        _selectedCategoryId.value = categoryId
    }
    
    private fun loadCategoryBooks(categoryId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            
            categoryRepository.getBooksInCategoryRecursive(categoryId).collect { books ->
                _categoryBooks.value = books
                _isLoading.value = false
            }
        }
    }
    
    fun addCategory(category: CategoryEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = categoryRepository.insertCategory(category)
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to add category"
            }
            
            _isLoading.value = false
        }
    }
    
    fun updateCategory(category: CategoryEntity) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = categoryRepository.updateCategory(category)
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to update category"
            }
            
            _isLoading.value = false
        }
    }
    
    fun deleteCategory(categoryId: String, deleteBooks: Boolean = false, moveToUncategorized: Boolean = true) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            val result = categoryRepository.deleteCategoryWithBooks(categoryId, deleteBooks, moveToUncategorized)
            
            if (result.isFailure) {
                _errorMessage.value = result.exceptionOrNull()?.message ?: "Failed to delete category"
            } else {
                // Clear selection if deleted category was selected
                if (_selectedCategoryId.value == categoryId) {
                    _selectedCategoryId.value = null
                }
            }
            
            _isLoading.value = false
        }
    }
    
    fun clearError() {
        _errorMessage.value = null
    }
}
