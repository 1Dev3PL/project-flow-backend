package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.entity.RefreshToken
import com.tech_dep.project_flow.entity.User
import com.tech_dep.project_flow.entity.toDto
import com.tech_dep.project_flow.exception.InvalidTokenException
import com.tech_dep.project_flow.exception.UserAlreadyExistsException
import com.tech_dep.project_flow.exception.UserNotFoundException
import com.tech_dep.project_flow.repository.UserRepository
import com.tech_dep.project_flow.utils.JwtUtils
import com.tech_dep.project_flow.utils.RefreshTokenUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    val userRepository: UserRepository,
    val passwordEncoder: PasswordEncoder,
    val authenticationManager: AuthenticationManager,
    val jwtUtils: JwtUtils,
    val refreshTokenUtils: RefreshTokenUtils,
    @Value("\${tech_dep.app.jwtExpirationMs}")
    private val jwtExpirationMs: Long,
    @Value("\${tech_dep.app.jwtRefreshExpirationMs}")
    private val jwtRefreshExpirationMs: Long,
) {
    private val log = KotlinLogging.logger {}

    fun fetchAuthData(accessToken: String): UserDto {
        log.info { "Получение данных пользователя" }
        val userEmail: String = jwtUtils.extractUsername(accessToken)!!
        val userData: User = userRepository.findByEmail(userEmail)!!

        return userData.toDto()
    }

    fun register(userData: RegisterRequestDto, response: HttpServletResponse): UserDto {
        log.info { "Регистрация пользователя ${userData.name}" }
        if (userRepository.existsByEmail(userData.email)) {
            log.error { "Пользователь с email ${userData.email} уже зарегистрирован" }
            throw UserAlreadyExistsException(userData.email)
        }

        val newUser = User(
            name = userData.name,
            email = userData.email,
            password = passwordEncoder.encode(userData.password)
        )
        val user = userRepository.save(newUser)
        log.info { "Пользователь ${userData.name} успешно зарегистрирован" }

        val accessToken: String = jwtUtils.generateToken(user, mapOf("id" to user.uuid))
        val refreshToken: RefreshToken = refreshTokenUtils.generateRefreshToken(user.email)

        val accessTokenCookie: ResponseCookie = ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(jwtExpirationMs / 1000)
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())

        val refreshTokenCookie: ResponseCookie = ResponseCookie.from("refreshToken", refreshToken.token)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth/refresh")
            .maxAge(jwtRefreshExpirationMs / 1000)
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())

        return user.toDto()
    }

    fun login(loginData: LoginRequestDto, response: HttpServletResponse): UserDto {
        log.info { "Вход пользователя ${loginData.email}" }
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(loginData.email, loginData.password))
        val userData: User = userRepository.findByEmail(loginData.email)!!
        val accessToken: String = jwtUtils.generateToken(userData, mapOf("id" to userData.uuid))

        if (refreshTokenUtils.existsByUserId(userData.id!!)) {
            refreshTokenUtils.deleteByUserId(userData.id!!)
        }
        val refreshToken: RefreshToken = refreshTokenUtils.generateRefreshToken(loginData.email)

        val accessTokenCookie: ResponseCookie = ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(jwtExpirationMs / 1000)
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())

        val refreshTokenCookie: ResponseCookie = ResponseCookie.from("refreshToken", refreshToken.token)
            .httpOnly(true)
            .secure(true)
            .path("/api/auth/refresh")
            .maxAge(jwtRefreshExpirationMs / 1000)
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
        log.info { "Вход пользователя ${loginData.email} выполнен успешно" }

        return userData.toDto()
    }

    fun refresh(refreshToken: String?, response: HttpServletResponse) {
        log.info { "Обновление access токена пользователя" }
        if (refreshToken == null) {
            log.error { "Нет refresh токена!" }
            throw InvalidTokenException()
        }
        val token: RefreshToken? = refreshTokenUtils.findByToken(refreshToken)
        if (token == null) {
            log.error { "Refresh токен не найден!" }
            throw InvalidTokenException()
        }
        refreshTokenUtils.verifyExpiration(token)
        val accessToken: String = jwtUtils.generateToken(token.user!!, mapOf("id" to token.user!!.uuid))
        val accessTokenCookie: ResponseCookie = ResponseCookie.from("accessToken", accessToken)
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(jwtExpirationMs / 1000)
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
    }

    fun logout(logoutData: LogoutRequestDto, response: HttpServletResponse) {
        log.info { "Выход пользователя ${logoutData.email} из аккаунта" }
        val userData = userRepository.findByEmail(logoutData.email)

        if (userData == null) {
            log.error { "Пользователь с email ${logoutData.email} не найден!" }
            throw UserNotFoundException()
        }

        refreshTokenUtils.deleteByUserId(userData.id!!)

        val accessTokenCookie: ResponseCookie = ResponseCookie.from("accessToken", "none")
            .httpOnly(true)
            .secure(true)
            .path("/")
            .maxAge(1)
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())

        val refreshTokenCookie: ResponseCookie = ResponseCookie.from("refreshToken", "none")
            .httpOnly(true)
            .secure(true)
            .path("/api/auth/refresh")
            .maxAge(1)
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
    }
}