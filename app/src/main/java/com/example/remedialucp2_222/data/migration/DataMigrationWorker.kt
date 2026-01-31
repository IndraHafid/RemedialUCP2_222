package com.example.remedialucp2_222.data.migration

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.remedialucp2_222.data.database.entities.*
import com.example.remedialucp2_222.data.repository.BookRepository
import com.example.remedialucp2_222.data.repository.AuthorRepository
import com.example.remedialucp2_222.data.repository.CategoryRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first
import java.util.*

class DataMigrationWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted workerParams: WorkerParameters,
    private val bookRepository: BookRepository,
    private val authorRepository: AuthorRepository,
    private val categoryRepository: CategoryRepository
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            performMigration()
            Result.success()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private suspend fun performMigration() {
        // Simulate data from old system
        val legacyBooks = getLegacyBooks()
        val legacyAuthors = getLegacyAuthors()
        val legacyCategories = getLegacyCategories()
        
        // Migrate categories first (since books depend on them)
        migrateCategories(legacyCategories)
        
        // Migrate authors
        migrateAuthors(legacyAuthors)
        
        // Migrate books
        migrateBooks(legacyBooks)
    }

    private suspend fun migrateCategories(legacyCategories: List<LegacyCategory>) {
        legacyCategories.forEach { legacy ->
            val category = CategoryEntity(
                id = UUID.randomUUID().toString(),
                name = legacy.name,
                description = legacy.description,
                parentId = null, // Simplified - would need to map parent relationships
                level = 0
            )
            categoryRepository.insertCategory(category)
        }
    }

    private suspend fun migrateAuthors(legacyAuthors: List<LegacyAuthor>) {
        legacyAuthors.forEach { legacy ->
            val author = AuthorEntity(
                id = UUID.randomUUID().toString(),
                name = legacy.name,
                email = legacy.email,
                biography = legacy.biography,
                nationality = legacy.nationality
            )
            authorRepository.insertAuthor(author)
        }
    }

    private suspend fun migrateBooks(legacyBooks: List<LegacyBook>) {
        val authors = authorRepository.getAllAuthors().first()
        
        legacyBooks.forEach { legacy ->
            val book = BookEntity(
                id = UUID.randomUUID().toString(),
                title = legacy.title,
                isbn = legacy.isbn,
                publisher = legacy.publisher,
                publishYear = legacy.publishYear,
                pageCount = legacy.pageCount,
                language = legacy.language,
                status = when (legacy.status.lowercase()) {
                    "available" -> "available"
                    "checked out" -> "borrowed"
                    "reserved" -> "reserved"
                    else -> "available"
                },
                location = legacy.location
            )
            
            // Find matching authors (simplified - would need better matching logic)
            val matchingAuthors = authors.filter { author ->
                legacy.authorNames.any { authorName ->
                    author.name.contains(authorName, ignoreCase = true)
                }
            }
            
            val authorIds = matchingAuthors.map { it.id }
            val authorRoles = List(authorIds.size) { "author" }
            
            bookRepository.insertBookWithAuthors(book, authorIds, authorRoles)
        }
    }

    // Simulate legacy data - in real implementation this would come from files, API, or old database
    private fun getLegacyBooks(): List<LegacyBook> {
        return listOf(
            LegacyBook(
                title = "To Kill a Mockingbird",
                isbn = "978-0-06-112008-4",
                authorNames = listOf("Harper Lee"),
                publisher = "J.B. Lippincott & Co.",
                publishYear = 1960,
                pageCount = 281,
                language = "English",
                status = "Available",
                location = "Fiction Section A"
            ),
            LegacyBook(
                title = "1984",
                isbn = "978-0-452-28423-4",
                authorNames = listOf("George Orwell"),
                publisher = "Secker & Warburg",
                publishYear = 1949,
                pageCount = 328,
                language = "English",
                status = "Checked Out",
                location = "Fiction Section B"
            ),
            LegacyBook(
                title = "A Brief History of Time",
                isbn = "978-0-553-38016-3",
                authorNames = listOf("Stephen Hawking"),
                publisher = "Bantam Books",
                publishYear = 1988,
                pageCount = 256,
                language = "English",
                status = "Available",
                location = "Science Section A"
            )
        )
    }

    private fun getLegacyAuthors(): List<LegacyAuthor> {
        return listOf(
            LegacyAuthor(
                name = "Harper Lee",
                email = "harper.lee@example.com",
                biography = "American novelist best known for To Kill a Mockingbird",
                nationality = "American"
            ),
            LegacyAuthor(
                name = "George Orwell",
                email = "george.orwell@example.com",
                biography = "English novelist and critic",
                nationality = "British"
            ),
            LegacyAuthor(
                name = "Stephen Hawking",
                email = "stephen.hawking@example.com",
                biography = "English theoretical physicist and cosmologist",
                nationality = "British"
            )
        )
    }

    private fun getLegacyCategories(): List<LegacyCategory> {
        return listOf(
            LegacyCategory(
                name = "Fiction",
                description = "Fictional literature including novels and short stories"
            ),
            LegacyCategory(
                name = "Science",
                description = "Scientific literature and educational materials"
            ),
            LegacyCategory(
                name = "Biography",
                description = "Life stories and autobiographies"
            )
        )
    }
}

// Data classes representing legacy system data
data class LegacyBook(
    val title: String,
    val isbn: String,
    val authorNames: List<String>,
    val publisher: String,
    val publishYear: Int,
    val pageCount: Int,
    val language: String,
    val status: String,
    val location: String
)

data class LegacyAuthor(
    val name: String,
    val email: String?,
    val biography: String?,
    val nationality: String?
)

data class LegacyCategory(
    val name: String,
    val description: String?
)
