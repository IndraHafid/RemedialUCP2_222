package com.example.remedialucp2_222.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithBooks(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = androidx.room.Junction(
            value = BookCategoryCrossRef::class,
            parentColumn = "categoryId",
            entityColumn = "bookId"
        )
    )
    val books: List<BookEntity>
)
