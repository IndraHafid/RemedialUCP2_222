package com.example.remedialucp2_222.data.validation

import com.example.remedialucp2_222.data.database.entities.BookEntity
import com.example.remedialucp2_222.data.database.entities.AuthorEntity
import com.example.remedialucp2_222.data.database.entities.CategoryEntity

object DataValidator {
    
    fun validateBook(book: BookEntity): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (book.title.isBlank()) {
            errors.add("Book title cannot be empty")
        }
        
        if (book.isbn.isBlank()) {
            errors.add("ISBN cannot be empty")
        } else if (!isValidISBN(book.isbn)) {
            errors.add("Invalid ISBN format")
        }
        
        if (book.publishYear < 0 || book.publishYear > java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)) {
            errors.add("Invalid publish year")
        }
        
        if (book.pageCount <= 0) {
            errors.add("Page count must be positive")
        }
        
        if (!listOf("available", "borrowed", "reserved", "maintenance").contains(book.status)) {
            errors.add("Invalid book status")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
    
    fun validateAuthor(author: AuthorEntity): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (author.name.isBlank()) {
            errors.add("Author name cannot be empty")
        }
        
        if (author.email != null && !isValidEmail(author.email)) {
            errors.add("Invalid email format")
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
    
    fun validateCategory(category: CategoryEntity, existingCategories: List<CategoryEntity>): ValidationResult {
        val errors = mutableListOf<String>()
        
        if (category.name.isBlank()) {
            errors.add("Category name cannot be empty")
        }
        
        // Check for duplicate names
        if (existingCategories.any { it.name.equals(category.name, ignoreCase = true) && it.id != category.id }) {
            errors.add("Category name already exists")
        }
        
        // Check for cyclic reference
        if (category.parentId != null) {
            if (wouldCreateCyclicReference(category.id, category.parentId, existingCategories)) {
                errors.add("Creating cyclic reference in category hierarchy")
            }
        }
        
        return if (errors.isEmpty()) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(errors)
        }
    }
    
    fun validateStatusTransition(currentStatus: String, newStatus: String): ValidationResult {
        val validTransitions = mapOf(
            "available" to listOf("borrowed", "reserved", "maintenance"),
            "borrowed" to listOf("available", "maintenance"),
            "reserved" to listOf("available", "borrowed", "maintenance"),
            "maintenance" to listOf("available")
        )
        
        val allowedStatuses = validTransitions[currentStatus] ?: emptyList()
        
        return if (allowedStatuses.contains(newStatus)) {
            ValidationResult.Success
        } else {
            ValidationResult.Error(listOf("Invalid status transition from $currentStatus to $newStatus"))
        }
    }
    
    private fun isValidISBN(isbn: String): Boolean {
        // Remove hyphens and spaces
        val cleanIsbn = isbn.replace("-", "").replace(" ", "")
        
        // Check if it's ISBN-10 or ISBN-13
        return when {
            cleanIsbn.length == 10 -> isValidISBN10(cleanIsbn)
            cleanIsbn.length == 13 -> isValidISBN13(cleanIsbn)
            else -> false
        }
    }
    
    private fun isValidISBN10(isbn: String): Boolean {
        try {
            var sum = 0
            for (i in 0..8) {
                val digit = isbn[i].toString().toInt()
                sum += digit * (10 - i)
            }
            
            val checkDigit = isbn[9].toString().toInt()
            val calculatedCheckDigit = (11 - (sum % 11)) % 11
            
            return checkDigit == calculatedCheckDigit
        } catch (e: NumberFormatException) {
            return false
        }
    }
    
    private fun isValidISBN13(isbn: String): Boolean {
        try {
            var sum = 0
            for (i in 0..11) {
                val digit = isbn[i].toString().toInt()
                sum += digit * if (i % 2 == 0) 1 else 3
            }
            
            val checkDigit = isbn[12].toString().toInt()
            val calculatedCheckDigit = (10 - (sum % 10)) % 10
            
            return checkDigit == calculatedCheckDigit
        } catch (e: NumberFormatException) {
            return false
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    private fun wouldCreateCyclicReference(
        categoryId: String,
        newParentId: String,
        categories: List<CategoryEntity>
    ): Boolean {
        val categoryMap = categories.associateBy { it.id }
        val visited = mutableSetOf<String>()
        
        fun hasCycle(currentId: String): Boolean {
            if (currentId == categoryId) return true
            if (currentId in visited) return false
            
            visited.add(currentId)
            val parent = categoryMap[currentId]?.parentId
            return parent?.let { hasCycle(it) } ?: false
        }
        
        return hasCycle(newParentId)
    }
}

sealed class ValidationResult {
    object Success : ValidationResult()
    data class Error(val messages: List<String>) : ValidationResult()
}
