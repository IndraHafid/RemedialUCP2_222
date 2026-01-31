package com.example.remedialucp2_222.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "book_category_cross_ref",
    primaryKeys = ["bookId", "categoryId"]
)
data class BookCategoryCrossRef(
    val bookId: String,
    val categoryId: String
)
