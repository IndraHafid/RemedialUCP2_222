package com.example.remedialucp2_222.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class BookWithCategories(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = androidx.room.Junction(
            value = BookCategoryCrossRef::class,
            parentColumn = "bookId",
            entityColumn = "categoryId"
        )
    )
    val categories: List<CategoryEntity>
)
