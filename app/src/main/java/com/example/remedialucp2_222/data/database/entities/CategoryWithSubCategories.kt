package com.example.remedialucp2_222.data.database.entities

import androidx.room.Embedded
import androidx.room.Relation

data class CategoryWithSubCategories(
    @Embedded val category: CategoryEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    val subCategories: List<CategoryEntity>
)
