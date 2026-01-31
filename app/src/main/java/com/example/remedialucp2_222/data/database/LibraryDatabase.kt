package com.example.remedialucp2_222.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.remedialucp2_222.data.database.dao.*
import com.example.remedialucp2_222.data.database.entities.*

@Database(
    entities = [
        BookEntity::class,
        AuthorEntity::class,
        BookAuthorCrossRef::class,
        CategoryEntity::class,
        BookCategoryCrossRef::class,
        AuditLogEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class LibraryDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun authorDao(): AuthorDao
    abstract fun categoryDao(): CategoryDao
    abstract fun auditLogDao(): AuditLogDao
    abstract fun crossRefDao(): CrossRefDao
}
