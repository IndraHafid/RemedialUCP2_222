package com.example.remedialucp2_222.data.repository

import com.example.remedialucp2_222.data.database.dao.AuthorDao
import com.example.remedialucp2_222.data.database.dao.AuditLogDao
import com.example.remedialucp2_222.data.database.entities.AuthorEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthorRepository @Inject constructor(
    private val authorDao: AuthorDao,
    private val auditLogDao: AuditLogDao
) {
    
    fun getAllAuthors(): Flow<List<AuthorEntity>> = authorDao.getAllAuthors()
    
    suspend fun getAuthorById(id: String): AuthorEntity? = authorDao.getAuthorById(id)
    
    fun searchAuthorsByName(name: String): Flow<List<AuthorEntity>> = authorDao.searchAuthorsByName(name)
    
    suspend fun insertAuthor(author: AuthorEntity): Result<Unit> {
        return try {
            validateAuthor(author)
            authorDao.insertAuthor(author)
            logAudit("authors", author.id, "INSERT", null, author)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateAuthor(author: AuthorEntity): Result<Unit> {
        return try {
            val oldAuthor = authorDao.getAuthorById(author.id)
            validateAuthor(author)
            
            authorDao.updateAuthor(author)
            logAudit("authors", author.id, "UPDATE", oldAuthor, author)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun softDeleteAuthor(authorId: String): Result<Unit> {
        return try {
            val author = authorDao.getAuthorById(authorId)
                ?: throw IllegalArgumentException("Author not found")
            
            authorDao.softDeleteAuthor(authorId)
            logAudit("authors", authorId, "SOFT_DELETE", author, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun validateAuthor(author: AuthorEntity) {
        if (author.name.isBlank()) {
            throw IllegalArgumentException("Author name cannot be empty")
        }
        if (author.email != null && !isValidEmail(author.email)) {
            throw IllegalArgumentException("Invalid email format")
        }
    }
    
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    
    private suspend fun logAudit(
        tableName: String,
        recordId: String,
        operation: String,
        oldValues: Any?,
        newValues: Any?
    ) {
        val auditLog = com.example.remedialucp2_222.data.database.entities.AuditLogEntity(
            tableName = tableName,
            recordId = recordId,
            operation = operation,
            oldValues = oldValues?.toString(),
            newValues = newValues?.toString()
        )
        auditLogDao.insertAuditLog(auditLog)
    }
}
