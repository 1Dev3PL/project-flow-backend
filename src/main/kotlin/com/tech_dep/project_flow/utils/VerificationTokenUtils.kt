package com.tech_dep.project_flow.utils

import com.tech_dep.project_flow.entity.User
import com.tech_dep.project_flow.entity.VerificationToken
import com.tech_dep.project_flow.exception.InvalidTokenException
import com.tech_dep.project_flow.repository.VerificationTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class VerificationTokenUtils(
    private val verificationTokenRepository: VerificationTokenRepository,
    @Value("\${tech_dep.app.verificationExpirationMs}")
    private val verificationTokenDurationMs: Long
) {
    fun generateVerificationToken(user: User): VerificationToken {
        val verificationToken = VerificationToken(
            user = user,
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(verificationTokenDurationMs)
        )

        return verificationTokenRepository.save(verificationToken)
    }

    fun findByToken(token: String): VerificationToken? {
        return verificationTokenRepository.findByToken(token)
    }

    fun deleteToken(token: VerificationToken) {
        verificationTokenRepository.delete(token)
    }

    fun verifyExpiration(token: VerificationToken): VerificationToken {
        if (token.expiryDate < Instant.now()) {
            verificationTokenRepository.delete(token)
            throw InvalidTokenException()
        }

        return token
    }
}