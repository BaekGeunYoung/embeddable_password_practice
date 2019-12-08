package com.practice.embeddable_password.controller.response

class ErrorResponse(
        private val message: String,
        private val code: String,
        private val status: Int,
        private val errors: List<FieldError> = listOf()
) {
    class FieldError(
            private val field: String,
            private val value: String,
            private val reason: String
    )
}