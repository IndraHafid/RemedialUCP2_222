package com.example.remedialucp2_222.utils

import android.content.Context
import androidx.work.*
import com.example.remedialucp2_222.data.migration.DataMigrationWorker
import java.util.concurrent.TimeUnit

class MigrationManager(private val context: Context) {
    
    fun scheduleDataMigration() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
            .setRequiresBatteryNotLow(true)
            .build()
        
        val migrationRequest = OneTimeWorkRequestBuilder<DataMigrationWorker>()
            .setConstraints(constraints)
            .addTag("data_migration")
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                OneTimeWorkRequest.MIN_BACKOFF_MILLIS,
                TimeUnit.MILLISECONDS
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            "library_data_migration",
            ExistingWorkPolicy.KEEP,
            migrationRequest
        )
    }
    
    fun getMigrationStatus(): LiveData<WorkInfo> {
        return WorkManager.getInstance(context)
            .getWorkInfoByIdLiveData("library_data_migration")
    }
    
    fun cancelMigration() {
        WorkManager.getInstance(context).cancelUniqueWork("library_data_migration")
    }
}
