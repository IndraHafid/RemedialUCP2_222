package com.example.remedialucp2_222.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import androidx.room.ForeignKey
import androidx.room.Index
import java.util.Date

@Entity(
    tableName = "categories",
    foreignKeys = [
        ForeignKey(
            entity = CategoryEntity::class,
            parentColumns = ["id"],
            childColumns = ["parentId"],
            onDelete = ForeignKey.RESTRICT,
            onUpdate = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["parentId"]),
        Index(value = ["name"], unique = true)
    ]
)
data class CategoryEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String? = null,
    val parentId: String? = null,
    val level: Int = 0,
    @ColumnInfo(defaultValue = "0")
    val isDeleted: Boolean = false,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: Date = Date()
)
