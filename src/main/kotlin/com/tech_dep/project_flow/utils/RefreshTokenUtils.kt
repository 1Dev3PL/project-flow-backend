package com.tech_dep.project_flow.utils

import com.tech_dep.project_flow.entity.RefreshToken
import com.tech_dep.project_flow.exception.InvalidTokenException
import com.tech_dep.project_flow.repository.RefreshTokenRepository
import com.tech_dep.project_flow.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.Instant
import java.util.*

@Service
class RefreshTokenUtils(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val userRepository: UserRepository,
    @Value("\${tech_dep.app.jwtRefreshExpirationMs}")
    private val refreshTokenDurationMs: Long
) {
    fun generateRefreshToken(email: String): RefreshToken {
        val refreshToken = RefreshToken(
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenDurationMs),
            user = userRepository.findByEmail(email)
        )
        return refreshTokenRepository.save(refreshToken)
    }

    fun findByToken(token: String): RefreshToken? {
        return refreshTokenRepository.findByToken(token)
    }

    fun existsByUserId(userId: Long): Boolean {
        return refreshTokenRepository.existsByUserId(userId)
    }

    @Transactional
    fun deleteByUserId(userId: Long): Long {
        return refreshTokenRepository.deleteByUserId(userId)
    }

    fun verifyExpiration(token: RefreshToken): RefreshToken {
        if (token.expiryDate!! < Instant.now()) {
            refreshTokenRepository.delete(token)
            throw InvalidTokenException()
        }
        return token
    }
}