package com.tech_dep.project_flow.utils

import com.tech_dep.project_flow.entity.RefreshToken
import com.tech_dep.project_flow.entity.User
import com.tech_dep.project_flow.exception.InvalidTokenException
import com.tech_dep.project_flow.repository.RefreshTokenRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.Instant
import java.util.*

@Service
class RefreshTokenUtils(
    private val refreshTokenRepository: RefreshTokenRepository,
    @Value("\${tech_dep.app.jwtRefreshExpirationMs}")
    private val refreshTokenDurationMs: Long
) {
    fun generateRefreshToken(user: User): RefreshToken {
        val refreshToken = RefreshToken(
            token = UUID.randomUUID().toString(),
            expiryDate = Instant.now().plusMillis(refreshTokenDurationMs),
            user = user
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
        if (token.expiryDate < Instant.now()) {
            refreshTokenRepository.delete(token)
            throw InvalidTokenException()
        }

        return token
    }
}