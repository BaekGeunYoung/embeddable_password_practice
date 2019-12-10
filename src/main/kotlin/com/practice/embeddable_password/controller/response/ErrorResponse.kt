package com.practice.embeddable_password.controller.response

import com.practice.embeddable_password.exception.ErrorCode

data class ErrorResponse(
        val message: String,
        val code: String,
        val status: Int,
        var details: List<ErrorDetail> = listOf()
) {
    class ErrorDetail(
            val message: String
    )

    companion object {
        fun of(errorCode: ErrorCode, details: List<ErrorDetail>? = null): ErrorResponse {
            val errorResponse = ErrorResponse(
                    message = errorCode.message,
                    code = errorCode.code,
                    status = errorCode.status
            )

            details?.let{ errorResponse.details = it }

            return errorResponse
        }
    }
}