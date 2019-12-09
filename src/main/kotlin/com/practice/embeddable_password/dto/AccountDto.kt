package com.practice.embeddable_password.dto

import com.practice.embeddable_password.entity.user.Password
import com.practice.embeddable_password.entity.user.Role
import com.practice.embeddable_password.entity.user.User
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder

class AccountDto {
    data class SignUpReq(
            val username: String,
            val rawPassword: String
    ) {
        fun toEntity(bCryptPasswordEncoder: BCryptPasswordEncoder): User {
            return User(
                    username = username,
                    password = Password(value = bCryptPasswordEncoder.encode(rawPassword)),
                    roles = mutableSetOf(Role.USER)
            )
        }
    }

    data class SignInReq(
            val username: String,
            val rawPassword: String
    )
}