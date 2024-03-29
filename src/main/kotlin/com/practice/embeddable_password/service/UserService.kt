package com.practice.embeddable_password.service

import com.practice.embeddable_password.config.security.jwt.JwtTokenProvider
import com.practice.embeddable_password.dto.AccountDto
import com.practice.embeddable_password.entity.user.User
import org.springframework.security.authentication.AuthenticationManager

interface UserService {
    fun signUp(signUpDto: AccountDto.SignUpReq): User
    fun signIn(signInDto: AccountDto.SignInReq,
               jwtTokenProvider: JwtTokenProvider,
               authenticationManager: AuthenticationManager): MutableMap<String, Any>
}