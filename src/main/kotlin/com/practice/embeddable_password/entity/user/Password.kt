package com.practice.embeddable_password.entity.user

import com.practice.embeddable_password.exception.PasswordFailedExceededException
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDateTime
import javax.persistence.Embeddable

@Embeddable
data class Password(
        var value: String,
        @Autowired private val bCryptPasswordEncoder: BCryptPasswordEncoder? = null
) {
    private var expirationDate = LocalDateTime.now().plusDays(14)
    private var failedCount = 0

    fun isMatched(rawPassword: String?): Boolean {
        val matches: Boolean = isMatches(rawPassword)
        updateFailedCount(matches)
        if (failedCount >= 5) throw PasswordFailedExceededException()

        return matches
    }

    private fun isMatches(rawPassword: String?): Boolean = this.value == bCryptPasswordEncoder!!.encode(rawPassword)

    private fun updateFailedCount(matches: Boolean) {
        this.failedCount = if(matches) 0 else this.failedCount + 1
    }

    fun changePassword(newPassword: String?, oldPassword: String?) {
        if (isMatched(oldPassword)) {
            value = bCryptPasswordEncoder!!.encode(newPassword)
            extendExpirationDate()
        }
    }

    private fun extendExpirationDate() {
        this.expirationDate = LocalDateTime.now().plusDays(14)
    }
}