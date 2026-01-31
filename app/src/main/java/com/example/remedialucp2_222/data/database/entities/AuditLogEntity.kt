package com.example.remedialucp2_222.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ColumnInfo
import java.util.Date

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val tableName: String,
    val recordId: String,
    val operation: String, // "INSERT", "UPDATE", "DELETE", "SOFT_DELETE"
    val oldValues: String? = null, // JSON string
    val newValues: String? = null, // JSON string
    val userId: String? = null,
    val timestamp: Date = Date(),
    val ipAddress: String? = null,
    val userAgent: String? = null
)
