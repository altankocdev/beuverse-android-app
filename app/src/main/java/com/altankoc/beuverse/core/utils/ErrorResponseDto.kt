package com.altankoc.beuverse.core.utils

data class ErrorResponseDto(
    val status: Int,
    val message: String,
    val timestamp: String,
    val errors: Map<String, String>?
)