package com.practice.embeddable_password.exception

enum class ErrorCode(
        val code: String,
        val message: String,
        val status: Int
) {
    ACCOUNT_NOT_FOUND("AC_001", "해당 회원을 찾을 수 없습니다.", 404),
    EMAIL_DUPLICATION("AC_002", "이메일이 중복되었습니다.", 400),
    INPUT_VALUE_INVALID("CM_001", "입력값이 올바르지 않습니다.", 400),
    PASSWORD_FAILED_COUNT_EXCEEDED("PW_001", "비밀번호 실패 횟수가 초과되었습니다.", 400),
    AUTHENTICATION_FAILED("AC_003", "회원정보가 일치하지 않습니다.", 400)
}