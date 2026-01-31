package com.example.remedialucp2_222.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "authors")
data class AuthorEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String? = null,
    val biography: String? = null,
    val nationality: String? = null,
    @ColumnInfo(defaultValue = "0")
    val isDeleted: Boolean = false,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: Date = Date()
)
