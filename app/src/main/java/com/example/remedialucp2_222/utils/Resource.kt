package com.example.remedialucp2_222.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

sealed class Resource<out T> {
    data class Success<T>(val data: T) : Resource<T>()
    data class Error(val exception: Throwable) : Resource<Nothing>()
    object Loading : Resource<Nothing>()
}

fun <T> Flow<T>.asResource(): Flow<Resource<T>> {
    return this
        .map<T, Resource<T>> { Resource.Success(it) }
        .onStart { emit(Resource.Loading) }
        .catch { emit(Resource.Error(it)) }
}

fun <T> Result<T>.toResource(): Resource<T> {
    return if (isSuccess) {
        Resource.Success(getOrNull()!!)
    } else {
        Resource.Error(exceptionOrNull()!!)
    }
}

object Constants {
    const val DATABASE_NAME = "library_database"
    const val DATABASE_VERSION = 1
    
    // Book statuses
    const val STATUS_AVAILABLE = "available"
    const val STATUS_BORROWED = "borrowed"
    const val STATUS_RESERVED = "reserved"
    const val STATUS_MAINTENANCE = "maintenance"
    
    // Author roles
    const val ROLE_AUTHOR = "author"
    const val ROLE_CO_AUTHOR = "co-author"
    const val ROLE_EDITOR = "editor"
    const val ROLE_TRANSLATOR = "translator"
    
    // Default categories
    const val CATEGORY_UNCATEGORIZED = "Uncategorized"
    const val CATEGORY_FICTION = "Fiction"
    const val CATEGORY_NON_FICTION = "Non-Fiction"
    
    // Validation patterns
    val VALID_STATUSES = listOf(STATUS_AVAILABLE, STATUS_BORROWED, STATUS_RESERVED, STATUS_MAINTENANCE)
    val VALID_AUTHOR_ROLES = listOf(ROLE_AUTHOR, ROLE_CO_AUTHOR, ROLE_EDITOR, ROLE_TRANSLATOR)
}
