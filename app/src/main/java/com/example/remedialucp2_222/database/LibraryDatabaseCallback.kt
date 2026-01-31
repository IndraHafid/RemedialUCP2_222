package com.example.remedialucp2_222.database

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

class LibraryDatabaseCallback @Inject constructor() : RoomDatabase.Callback() {
    
    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        
        // Insert default data in a background coroutine
        CoroutineScope(Dispatchers.IO).launch {
            populateDefaultData(db)
        }
    }
    
    private suspend fun populateDefaultData(db: SupportSQLiteDatabase) {
        // Insert default categories
        val uncategorizedId = "cat_uncategorized"
        db.execSQL("""
            INSERT INTO categories (id, name, description, parentId, level, isDeleted, createdAt, updatedAt) 
            VALUES ('$uncategorizedId', 'Uncategorized', 'Books without specific category', NULL, 0, 0, '${Date().time}', '${Date().time}')
        """)
        
        val fictionId = "cat_fiction"
        db.execSQL("""
            INSERT INTO categories (id, name, description, parentId, level, isDeleted, createdAt, updatedAt) 
            VALUES ('$fictionId', 'Fiction', 'Fictional literature', NULL, 0, 0, '${Date().time}', '${Date().time}')
        """)
        
        val nonFictionId = "cat_non_fiction"
        db.execSQL("""
            INSERT INTO categories (id, name, description, parentId, level, isDeleted, createdAt, updatedAt) 
            VALUES ('$nonFictionId', 'Non-Fiction', 'Non-fictional literature', NULL, 0, 0, '${Date().time}', '${Date().time}')
        """)
        
        // Insert sub-categories
        val novelId = "cat_novel"
        db.execSQL("""
            INSERT INTO categories (id, name, description, parentId, level, isDeleted, createdAt, updatedAt) 
            VALUES ('$novelId', 'Novel', 'Novel books', '$fictionId', 1, 0, '${Date().time}', '${Date().time}')
        """)
        
        val scienceId = "cat_science"
        db.execSQL("""
            INSERT INTO categories (id, name, description, parentId, level, isDeleted, createdAt, updatedAt) 
            VALUES ('$scienceId', 'Science', 'Science books', '$nonFictionId', 1, 0, '${Date().time}', '${Date().time}')
        """)
        
        // Insert sample authors
        val author1Id = "author_1"
        db.execSQL("""
            INSERT INTO authors (id, name, email, biography, nationality, isDeleted, createdAt, updatedAt) 
            VALUES ('$author1Id', 'John Doe', 'john.doe@example.com', 'Fiction writer', 'American', 0, '${Date().time}', '${Date().time}')
        """)
        
        val author2Id = "author_2"
        db.execSQL("""
            INSERT INTO authors (id, name, email, biography, nationality, isDeleted, createdAt, updatedAt) 
            VALUES ('$author2Id', 'Jane Smith', 'jane.smith@example.com', 'Science author', 'British', 0, '${Date().time}', '${Date().time}')
        """)
        
        // Insert sample books
        val book1Id = "book_1"
        db.execSQL("""
            INSERT INTO books (id, title, isbn, publisher, publishYear, pageCount, language, status, location, isDeleted, createdAt, updatedAt) 
            VALUES ('$book1Id', 'The Great Adventure', '1234567890', 'ABC Publishing', 2020, 300, 'English', 'available', 'Shelf A1', 0, '${Date().time}', '${Date().time}')
        """)
        
        val book2Id = "book_2"
        db.execSQL("""
            INSERT INTO books (id, title, isbn, publisher, publishYear, pageCount, language, status, location, isDeleted, createdAt, updatedAt) 
            VALUES ('$book2Id', 'Science Today', '0987654321', 'XYZ Press', 2021, 250, 'English', 'available', 'Shelf B2', 0, '${Date().time}', '${Date().time}')
        """)
        
        // Insert book-author relationships
        db.execSQL("""
            INSERT INTO book_author_cross_ref (bookId, authorId, authorRole) 
            VALUES ('$book1Id', '$author1Id', 'author')
        """)
        
        db.execSQL("""
            INSERT INTO book_author_cross_ref (bookId, authorId, authorRole) 
            VALUES ('$book2Id', '$author2Id', 'author')
        """)
        
        // Insert book-category relationships
        db.execSQL("""
            INSERT INTO book_category_cross_ref (bookId, categoryId) 
            VALUES ('$book1Id', '$novelId')
        """)
        
        db.execSQL("""
            INSERT INTO book_category_cross_ref (bookId, categoryId) 
            VALUES ('$book2Id', '$scienceId')
        """)
    }
}
