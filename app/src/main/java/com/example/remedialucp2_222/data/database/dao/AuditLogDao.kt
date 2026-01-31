package com.example.remedialucp2_222.data.database.dao

import androidx.room.*
import com.example.remedialucp2_222.data.database.entities.AuditLogEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuditLogDao {
    @Query("SELECT * FROM audit_logs ORDER BY timestamp DESC")
    fun getAllAuditLogs(): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs WHERE tableName = :tableName ORDER BY timestamp DESC")
    fun getAuditLogsByTable(tableName: String): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs WHERE recordId = :recordId ORDER BY timestamp DESC")
    fun getAuditLogsByRecord(recordId: String): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getAuditLogsByUser(userId: String): Flow<List<AuditLogEntity>>

    @Insert
    suspend fun insertAuditLog(auditLog: AuditLogEntity)

    @Query("DELETE FROM audit_logs WHERE timestamp < :beforeDate")
    suspend fun deleteOldAuditLogs(beforeDate: java.util.Date)
}
