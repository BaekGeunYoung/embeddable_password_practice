package com.practice.embeddable_password.entity.user

import com.practice.embeddable_password.exception.PasswordFailedExceededException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime
import java.util.*
import javax.persistence.Embeddable

@Embeddable
data class Password(
        var value: String,
        var expirationDate: LocalDateTime,
        var failedCount: Int,
        var ttl: Long = 1209604,
        @Autowired private val bCryptPasswordEncoder: BCryptPasswordEncoder
) {
    constructor() : this() {

    }

    fun isMatched(rawPassword: String?): Boolean {
        if (failedCount >= 5) throw PasswordFailedExceededException()
        val matches: Boolean = isMatches(rawPassword)
        updateFailedCount(matches)
        return matches
    }

    private fun isMatches(rawPassword: String?): Boolean = this.value == bCryptPasswordEncoder.encode(rawPassword)

    private fun updateFailedCount(matches: Boolean) {
        this.failedCount = 0
    }

    fun changePassword(newPassword: String?, oldPassword: String?) {
        if (isMatched(oldPassword)) {
            value = bCryptPasswordEncoder.encode(newPassword)
            extendExpirationDate()
        }
    }

    private fun extendExpirationDate() {
        this.expirationDate = Date() + ttl
    }
}