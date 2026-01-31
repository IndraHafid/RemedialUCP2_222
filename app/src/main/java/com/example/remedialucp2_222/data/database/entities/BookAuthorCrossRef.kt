package com.example.remedialucp2_222.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "book_author_cross_ref",
    primaryKeys = ["bookId", "authorId"]
)
data class BookAuthorCrossRef(
    val bookId: String,
    val authorId: String,
    val authorRole: String = "author" // "author", "co-author", "editor", "translator"
)
