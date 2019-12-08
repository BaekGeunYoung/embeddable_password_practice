package com.practice.embeddable_password.config.controller

import com.practice.embeddable_password.controller.response.ErrorResponse
import com.practice.embeddable_password.exception.ErrorCode
import com.practice.embeddable_password.exception.PasswordFailedExceededException
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ErrorExceptionController {
    @ExceptionHandler(PasswordFailedExceededException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handlePasswordFailedExceededException(e: PasswordFailedExceededException): ErrorResponse {
        val errorCode = ErrorCode.PASSWORD_FAILED_COUNT_EXCEEDED

        return ErrorResponse(
                message = errorCode.message,
                code = errorCode.code,
                status = errorCode.status
        )
    }
}