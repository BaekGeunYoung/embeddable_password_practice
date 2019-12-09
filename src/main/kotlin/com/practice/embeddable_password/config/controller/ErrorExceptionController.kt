package com.practice.embeddable_password.config.controller

import com.practice.embeddable_password.controller.response.ErrorResponse
import com.practice.embeddable_password.exception.ErrorCode
import com.practice.embeddable_password.exception.PasswordFailedExceededException
import org.springframework.http.HttpStatus
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ErrorExceptionController : ResponseEntityExceptionHandler() {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordFailedExceededException::class)
    fun handlePasswordFailedExceededException(e: PasswordFailedExceededException): ErrorResponse {
        val errorCode = ErrorCode.PASSWORD_FAILED_COUNT_EXCEEDED

        return ErrorResponse(
                message = errorCode.message,
                code = errorCode.code,
                status = errorCode.status
        )
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(value = [(BadCredentialsException::class)])
    fun handleBadeCredentialsException(e: BadCredentialsException): ErrorResponse {
        val errorCode = ErrorCode.AUTHENTICATION_FAILED

        return ErrorResponse(
                message = errorCode.message,
                code = errorCode.code,
                status = errorCode.status
        )
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(UsernameNotFoundException::class)
    fun handleUsernameNotFoundException(e: UsernameNotFoundException): ErrorResponse {
        val errorCode = ErrorCode.ACCOUNT_NOT_FOUND

        return ErrorResponse(
                message = errorCode.message,
                code = errorCode.code,
                status = errorCode.status
        )
    }
}