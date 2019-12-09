package com.practice.embeddable_password.controller.api

import com.practice.embeddable_password.config.security.jwt.JwtTokenProvider
import com.practice.embeddable_password.dto.AccountDto
import com.practice.embeddable_password.service.UserService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import javax.validation.Valid

@RestController
@RequestMapping("api/v1/user")
class UserController(
        @Autowired private val userService: UserService,
        @Autowired private val jwtTokenProvider: JwtTokenProvider
) {
    @PostMapping("/register")
    fun register(@RequestBody @Valid signUpReq: AccountDto.SignUpReq) = userService.signUp(signUpReq)

    @PostMapping("/login")
    fun login(@RequestBody @Valid signInReq: AccountDto.SignInReq) = userService.signIn(signInReq, jwtTokenProvider)
}