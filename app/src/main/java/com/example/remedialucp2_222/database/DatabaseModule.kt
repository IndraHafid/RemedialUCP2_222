package com.example.remedialucp2_222.database

import android.content.Context
import androidx.room.Room
import com.example.remedialucp2_222.data.database.LibraryDatabase
import com.example.remedialucp2_222.data.repository.AuthorRepository
import com.example.remedialucp2_222.data.repository.BookRepository
import com.example.remedialucp2_222.data.repository.CategoryRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideLibraryDatabase(@ApplicationContext context: Context): LibraryDatabase {
        return Room.databaseBuilder(
            context,
            LibraryDatabase::class.java,
            "library_database"
        )
        .addCallback(LibraryDatabaseCallback())
        .build()
    }
    
    @Provides
    fun provideBookDao(database: LibraryDatabase) = database.bookDao()
    
    @Provides
    fun provideAuthorDao(database: LibraryDatabase) = database.authorDao()
    
    @Provides
    fun provideCategoryDao(database: LibraryDatabase) = database.categoryDao()
    
    @Provides
    fun provideAuditLogDao(database: LibraryDatabase) = database.auditLogDao()
    
    @Provides
    fun provideCrossRefDao(database: LibraryDatabase) = database.crossRefDao()
    
    @Provides
    @Singleton
    fun provideBookRepository(
        bookDao: com.example.remedialucp2_222.data.database.dao.BookDao,
        authorDao: com.example.remedialucp2_222.data.database.dao.AuthorDao,
        crossRefDao: com.example.remedialucp2_222.data.database.dao.CrossRefDao,
        auditLogDao: com.example.remedialucp2_222.data.database.dao.AuditLogDao
    ): BookRepository {
        return BookRepository(bookDao, authorDao, crossRefDao, auditLogDao)
    }
    
    @Provides
    @Singleton
    fun provideAuthorRepository(
        authorDao: com.example.remedialucp2_222.data.database.dao.AuthorDao,
        auditLogDao: com.example.remedialucp2_222.data.database.dao.AuditLogDao
    ): AuthorRepository {
        return AuthorRepository(authorDao, auditLogDao)
    }
    
    @Provides
    @Singleton
    fun provideCategoryRepository(
        categoryDao: com.example.remedialucp2_222.data.database.dao.CategoryDao,
        bookDao: com.example.remedialucp2_222.data.database.dao.BookDao,
        crossRefDao: com.example.remedialucp2_222.data.database.dao.CrossRefDao,
        auditLogDao: com.example.remedialucp2_222.data.database.dao.AuditLogDao
    ): CategoryRepository {
        return CategoryRepository(categoryDao, bookDao, crossRefDao, auditLogDao)
    }
}
