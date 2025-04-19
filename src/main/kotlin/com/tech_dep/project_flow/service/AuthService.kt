package com.tech_dep.project_flow.service

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.entity.RefreshToken
import com.tech_dep.project_flow.entity.User
import com.tech_dep.project_flow.event.RegistrationCompleteEvent
import com.tech_dep.project_flow.exception.InvalidTokenException
import com.tech_dep.project_flow.exception.TokenNotFoundException
import com.tech_dep.project_flow.exception.UserAlreadyExistsException
import com.tech_dep.project_flow.exception.UserNotFoundException
import com.tech_dep.project_flow.repository.UserRepository
import com.tech_dep.project_flow.utils.JwtUtils
import com.tech_dep.project_flow.utils.RefreshTokenUtils
import com.tech_dep.project_flow.utils.VerificationTokenUtils
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.ApplicationEventPublisher
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager,
    private val jwtUtils: JwtUtils,
    private val refreshTokenUtils: RefreshTokenUtils,
    private val verificationTokenUtils: VerificationTokenUtils,
    private val eventPublisher: ApplicationEventPublisher,
    @Value("\${tech_dep.app.jwtExpirationMs}")
    private val jwtExpirationMs: Long,
    @Value("\${tech_dep.app.jwtRefreshExpirationMs}")
    private val jwtRefreshExpirationMs: Long,
) {
    private val log = KotlinLogging.logger {}

    fun fetchAuthData(accessToken: String): UserDto {
        log.info { "Получение данных пользователя" }
        val userEmail: String = jwtUtils.extractUsername(accessToken)
        val userData: User = userRepository.findByEmail(userEmail)!!

        return userData.toDto()
    }

    fun register(request: HttpServletRequest, response: HttpServletResponse, userData: RegisterRequestDto): UserDto {
        log.info { "Регистрация пользователя ${userData.name}" }

        val email = userData.email.lowercase()
        if (userRepository.existsByEmail(email)) {
            log.error { "Пользователь с email ${userData.email} уже зарегистрирован" }
            throw UserAlreadyExistsException(email)
        }

        val newUser = User(
            name = userData.name,
            email = email,
            password = passwordEncoder.encode(userData.password)
        )
        val user = userRepository.save(newUser)
        eventPublisher.publishEvent(RegistrationCompleteEvent(user))

        log.info { "Пользователь ${userData.name} успешно зарегистрирован" }

        return user.toDto()
    }

    fun confirmAccount(token: String, request: HttpServletRequest) {
        log.info { "Подтверждение регистрации" }

        val verificationToken = verificationTokenUtils.findByToken(token)

        if (verificationToken == null) {
            log.error { "Верификационный токен не найден!" }
            throw TokenNotFoundException()
        }

        verificationTokenUtils.verifyExpiration(verificationToken)

        val user = verificationToken.user
        user.enabled = true
        userRepository.save(user)
        verificationTokenUtils.deleteToken(verificationToken)

        log.info { "Регистрация успешно подтверждена" }
    }

    fun login(loginData: LoginRequestDto, response: HttpServletResponse): UserDto {
        log.info { "Вход пользователя ${loginData.email}" }

        val email = loginData.email.lowercase()
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(email, loginData.password))
        val user: User = userRepository.findByEmail(email)!!
        val accessToken: String = jwtUtils.generateToken(user, mapOf("id" to user.uuid))

        if (refreshTokenUtils.existsByUserId(user.id!!)) {
            refreshTokenUtils.deleteByUserId(user.id!!)
        }
        val refreshToken: RefreshToken = refreshTokenUtils.generateRefreshToken(user)

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

        return user.toDto()
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
            throw TokenNotFoundException()
        }
        refreshTokenUtils.verifyExpiration(token)
        val accessToken: String = jwtUtils.generateToken(token.user, mapOf("id" to token.user.uuid))
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

        val userData = userRepository.findByEmail(logoutData.email.lowercase())

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