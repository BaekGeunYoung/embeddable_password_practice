package com.practice.embeddable_password.repository

import com.practice.embeddable_password.entity.user.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository: JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
}