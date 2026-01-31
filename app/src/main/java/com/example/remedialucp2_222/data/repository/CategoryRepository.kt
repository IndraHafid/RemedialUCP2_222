package com.example.remedialucp2_222.data.repository

import com.example.remedialucp2_222.data.database.dao.*
import com.example.remedialucp2_222.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CategoryRepository @Inject constructor(
    private val categoryDao: CategoryDao,
    private val bookDao: BookDao,
    private val crossRefDao: CrossRefDao,
    private val auditLogDao: AuditLogDao
) {
    
    fun getAllCategories(): Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    
    suspend fun getCategoryById(id: String): CategoryEntity? = categoryDao.getCategoryById(id)
    
    fun getSubCategories(parentId: String?): Flow<List<CategoryEntity>> = 
        categoryDao.getSubCategories(parentId)
    
    suspend fun getAllChildCategoryIds(categoryId: String): List<String> = 
        categoryDao.getAllChildCategoryIds(categoryId)
    
    suspend fun getBooksInCategoryRecursive(categoryId: String): Flow<List<BookEntity>> {
        val childIds = getAllChildCategoryIds(categoryId)
        val allCategoryIds = listOf(categoryId) + childIds
        return bookDao.getBooksByCategories(allCategoryIds)
    }
    
    suspend fun insertCategory(category: CategoryEntity): Result<Unit> {
        return try {
            validateCategory(category)
            categoryDao.insertCategory(category)
            logAudit("categories", category.id, "INSERT", null, category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateCategory(category: CategoryEntity): Result<Unit> {
        return try {
            val oldCategory = categoryDao.getCategoryById(category.id)
            validateCategory(category)
            validateNoCyclicReference(category)
            
            categoryDao.updateCategory(category)
            logAudit("categories", category.id, "UPDATE", oldCategory, category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun deleteCategoryWithBooks(
        categoryId: String,
        deleteBooks: Boolean,
        moveToUncategorized: Boolean
    ): Result<Unit> {
        return try {
            val bookIds = crossRefDao.getBookIdsByCategory(categoryId)
            val borrowedBooksCount = bookDao.countBorrowedBooks(bookIds)
            
            if (borrowedBooksCount > 0) {
                return Result.failure(
                    IllegalStateException("Cannot delete category with $borrowedBooksCount borrowed books")
                )
            }
            
            // Perform transaction
            if (deleteBooks) {
                // Soft delete all books in this category
                bookIds.forEach { bookId ->
                    bookDao.softDeleteBook(bookId)
                }
            } else if (moveToUncategorized) {
                // Move books to "Uncategorized" category or remove category association
                crossRefDao.deleteCategoryBooks(categoryId)
            }
            
            // Soft delete the category
            categoryDao.softDeleteCategory(categoryId)
            
            logAudit("categories", categoryId, "DELETE", null, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private suspend fun validateCategory(category: CategoryEntity) {
        if (category.name.isBlank()) {
            throw IllegalArgumentException("Category name cannot be empty")
        }
        
        if (category.parentId != null) {
            val parent = categoryDao.getCategoryById(category.parentId!!)
                ?: throw IllegalArgumentException("Parent category not found")
            
            if (wouldCreateCycle(category.id, category.parentId!!)) {
                throw IllegalArgumentException("Creating cyclic reference in category hierarchy")
            }
        }
    }
    
    private suspend fun validateNoCyclicReference(category: CategoryEntity) {
        if (category.parentId != null) {
            val parentIds = categoryDao.getAllParentCategoryIds(category.parentId!!)
            if (parentIds.contains(category.id)) {
                throw IllegalArgumentException("Cyclic reference detected in category hierarchy")
            }
        }
    }
    
    private suspend fun wouldCreateCycle(categoryId: String, newParentId: String): Boolean {
        val parentIds = categoryDao.getAllParentCategoryIds(newParentId)
        return parentIds.contains(categoryId)
    }
    
    private suspend fun logAudit(
        tableName: String,
        recordId: String,
        operation: String,
        oldValues: Any?,
        newValues: Any?
    ) {
        val auditLog = AuditLogEntity(
            tableName = tableName,
            recordId = recordId,
            operation = operation,
            oldValues = oldValues?.toString(),
            newValues = newValues?.toString()
        )
        auditLogDao.insertAuditLog(auditLog)
    }
}
