package com.example.remedialucp2_222.data.repository

import com.example.remedialucp2_222.data.database.dao.*
import com.example.remedialucp2_222.data.database.entities.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookRepository @Inject constructor(
    private val bookDao: BookDao,
    private val authorDao: AuthorDao,
    private val crossRefDao: CrossRefDao,
    private val auditLogDao: AuditLogDao
) {
    
    fun getAllBooks(): Flow<List<BookEntity>> = bookDao.getAllBooks()
    
    suspend fun getBookById(id: String): BookEntity? = bookDao.getBookById(id)
    
    fun getBooksByStatus(status: String): Flow<List<BookEntity>> = bookDao.getBooksByStatus(status)
    
    suspend fun insertBookWithAuthors(
        book: BookEntity,
        authorIds: List<String>,
        authorRoles: List<String>
    ): Result<Unit> {
        return try {
            validateBook(book)
            validateAuthors(authorIds)
            
            bookDao.insertBook(book)
            
            // Insert book-author relationships
            authorIds.forEachIndexed { index, authorId ->
                val crossRef = BookAuthorCrossRef(
                    bookId = book.id,
                    authorId = authorId,
                    authorRole = authorRoles.getOrNull(index) ?: "author"
                )
                crossRefDao.insertBookAuthor(crossRef)
            }
            
            logAudit("books", book.id, "INSERT", null, book)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateBook(book: BookEntity): Result<Unit> {
        return try {
            val oldBook = bookDao.getBookById(book.id)
            validateBook(book)
            
            bookDao.updateBook(book)
            logAudit("books", book.id, "UPDATE", oldBook, book)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun softDeleteBook(bookId: String): Result<Unit> {
        return try {
            val book = bookDao.getBookById(bookId)
                ?: throw IllegalArgumentException("Book not found")
            
            if (book.status == "borrowed") {
                throw IllegalStateException("Cannot delete borrowed book")
            }
            
            bookDao.softDeleteBook(bookId)
            logAudit("books", bookId, "SOFT_DELETE", book, null)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateBookStatus(bookId: String, newStatus: String): Result<Unit> {
        return try {
            val book = bookDao.getBookById(bookId)
                ?: throw IllegalArgumentException("Book not found")
            
            validateStatusTransition(book.status, newStatus)
            
            val updatedBook = book.copy(status = newStatus)
            bookDao.updateBook(updatedBook)
            logAudit("books", bookId, "UPDATE", book, updatedBook)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    private fun validateBook(book: BookEntity) {
        if (book.title.isBlank()) {
            throw IllegalArgumentException("Book title cannot be empty")
        }
        if (book.isbn.isBlank()) {
            throw IllegalArgumentException("ISBN cannot be empty")
        }
        if (book.publishYear < 0 || book.publishYear > java.util.Calendar.getInstance().get(java.util.Calendar.YEAR)) {
            throw IllegalArgumentException("Invalid publish year")
        }
        if (book.pageCount <= 0) {
            throw IllegalArgumentException("Page count must be positive")
        }
        if (!listOf("available", "borrowed", "reserved", "maintenance").contains(book.status)) {
            throw IllegalArgumentException("Invalid book status")
        }
    }
    
    private suspend fun validateAuthors(authorIds: List<String>) {
        authorIds.forEach { authorId ->
            authorDao.getAuthorById(authorId)
                ?: throw IllegalArgumentException("Author with ID $authorId not found")
        }
    }
    
    private fun validateStatusTransition(currentStatus: String, newStatus: String) {
        val validTransitions = mapOf(
            "available" to listOf("borrowed", "reserved", "maintenance"),
            "borrowed" to listOf("available", "maintenance"),
            "reserved" to listOf("available", "borrowed", "maintenance"),
            "maintenance" to listOf("available")
        )
        
        val allowedStatuses = validTransitions[currentStatus] ?: emptyList()
        if (!allowedStatuses.contains(newStatus)) {
            throw IllegalArgumentException("Invalid status transition from $currentStatus to $newStatus")
        }
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
