package com.practice.embeddable_password.dto

import com.practice.embeddable_password.entity.user.Password
import com.practice.embeddable_password.entity.user.Role
import com.practice.embeddable_password.entity.user.User

class AccountDto {
    data class SignUpReq(
            val username: String,
            val rawPassword: String
    ) {
        fun toEntity(): User {
            return User(
                    username = username,
                    password = Password(value = rawPassword),
                    roles = mutableSetOf(Role.USER)
            )
        }
    }

    data class SignInReq(
            val username: String,
            val rawPassword: String
    )
}