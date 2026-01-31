package com.example.remedialucp2_222.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class BookWithAuthors(
    @Embedded val book: BookEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "authorId",
        associateBy = androidx.room.Junction(
            value = BookAuthorCrossRef::class,
            parentColumn = "bookId",
            entityColumn = "authorId"
        )
    )
    val authors: List<AuthorEntity>
)
