package com.example.remedialucp2_222.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "books")
data class BookEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val isbn: String,
    val publisher: String,
    val publishYear: Int,
    val pageCount: Int,
    val language: String,
    val status: String, // "available", "borrowed", "reserved", "maintenance"
    val location: String,
    @ColumnInfo(defaultValue = "0")
    val isDeleted: Boolean = false,
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val createdAt: Date = Date(),
    @ColumnInfo(defaultValue = "CURRENT_TIMESTAMP")
    val updatedAt: Date = Date()
)
