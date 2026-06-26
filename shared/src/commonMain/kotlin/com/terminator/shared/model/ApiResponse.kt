package com.terminator.shared.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null,
    val code: String? = null
)

@Serializable
data class PaginatedResponse<T>(
    val success: Boolean,
    val message: String,
    val data: PaginatedData<T>,
    val code: String? = null
)

@Serializable
data class PaginatedData<T>(
    val list: List<T>,
    val total: Int,
    val page: Int,
    @SerialName("page_size")
    val pageSize: Int
)
