package com.tech_dep.project_flow.utils

import com.tech_dep.project_flow.entity.User
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.*
import javax.crypto.SecretKey
import kotlin.collections.HashMap

@Service
class JwtUtils(
    @Value("\${tech_dep.app.jwtExpirationMs}")
    private val jwtExpirationMs: Long,
    @Value("\${tech_dep.app.jwtSecret}")
    private val jwtSecret: String,
) {
    fun extractUsername(token: String): String = extractClaim(token, Claims::getSubject)

    fun extractId(token: String): UUID = extractClaim(token, fun(claims: Claims): UUID = UUID.fromString(claims["id"] as String))

    fun extractExpiration(token: String): Date = extractClaim(token, Claims::getExpiration)

    fun <T> extractClaim(token: String, claimsResolver: (claims: Claims) -> T): T =
        claimsResolver(extractAllClaims(token))

    fun generateToken(userDetails: User, extraClaims: Map<String, Any> = HashMap()): String =
        Jwts.builder()
            .claims()
            .add(extraClaims)
            .add(mapOf("id" to userDetails.uuid))
            .subject(userDetails.username)
            .issuedAt(Date(System.currentTimeMillis()))
            .expiration(Date(System.currentTimeMillis() + jwtExpirationMs))
            .and().signWith(getSignInKey()).compact()

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean =
        (extractUsername(token) == userDetails.username) && !isTokenExpired(token)

    fun isTokenExpired(token: String): Boolean =
        extractExpiration(token).before(Date())

    private fun extractAllClaims(token: String): Claims =
        Jwts.parser().verifyWith(getSignInKey()).build().parseSignedClaims(token).payload

    private fun getSignInKey(): SecretKey {
        val keyBytes: ByteArray = Decoders.BASE64.decode(jwtSecret)
        return Keys.hmacShaKeyFor(keyBytes)
    }
}