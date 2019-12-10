package com.practice.embeddable_password.config.controller

import com.practice.embeddable_password.controller.response.ErrorResponse
import com.practice.embeddable_password.exception.ErrorCode
import com.practice.embeddable_password.exception.PasswordFailedExceededException
import com.practice.embeddable_password.exception.PasswordNotMatchedException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice
class ErrorExceptionController {
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(PasswordFailedExceededException::class)
    fun handle(e: PasswordFailedExceededException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.PASSWORD_FAILED_COUNT_EXCEEDED
        val errorResponse = ErrorResponse.of(errorCode)

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(BadCredentialsException::class)])
    fun handle(e: BadCredentialsException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.AUTHENTICATION_FAILED
        val errorResponse = ErrorResponse.of(errorCode)

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [(PasswordNotMatchedException::class)])
    fun handle(e: PasswordNotMatchedException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.AUTHENTICATION_FAILED
        val details = listOf(ErrorResponse.ErrorDetail("current fail count : ${e.failedCount}"))

        val errorResponse = ErrorResponse.of(errorCode, details)

        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(UsernameNotFoundException::class)
    fun handle(e: UsernameNotFoundException): ResponseEntity<ErrorResponse> {
        val errorCode = ErrorCode.ACCOUNT_NOT_FOUND
        val errorResponse = ErrorResponse.of(errorCode)

        return ResponseEntity(errorResponse, HttpStatus.NOT_FOUND)
    }
}