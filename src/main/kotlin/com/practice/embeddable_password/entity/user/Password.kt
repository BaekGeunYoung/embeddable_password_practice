package com.practice.embeddable_password.entity.user

import com.practice.embeddable_password.exception.PasswordFailedExceededException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime
import javax.persistence.Embeddable

@Embeddable
data class Password(
        var value: String
) {
    private var expirationDate = LocalDateTime.now().plusDays(14)
    private var failedCount = 0

    fun updateFailedCount(matches: Boolean) {
        this.failedCount = if(matches) 0 else this.failedCount + 1
    }

    fun changePassword(newPassword: String, oldPassword: String, bCryptPasswordEncoder: BCryptPasswordEncoder) {
        value = bCryptPasswordEncoder.encode(newPassword)
        extendExpirationDate()
    }

    private fun extendExpirationDate() {
        this.expirationDate = LocalDateTime.now().plusDays(14)
    }
}