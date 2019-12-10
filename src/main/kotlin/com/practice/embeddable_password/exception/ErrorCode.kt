package com.practice.embeddable_password.exception

enum class ErrorCode(
        val code: String,
        val message: String,
        val status: Int
) {
    ACCOUNT_NOT_FOUND("AC_001", "Cannot find such user.", 404),
    EMAIL_DUPLICATION("AC_002", "Duplicated Email.", 400),
    INPUT_VALUE_INVALID("CM_001", "Input value is invalid.", 400),
    PASSWORD_FAILED_COUNT_EXCEEDED("PW_001", "password failed count was exceeded.", 400),
    AUTHENTICATION_FAILED("AC_003", "authentication failed.", 400)
}