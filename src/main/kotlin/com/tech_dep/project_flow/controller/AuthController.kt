package com.tech_dep.project_flow.controller

import com.tech_dep.project_flow.dto.*
import com.tech_dep.project_flow.exception.InvalidTokenException
import com.tech_dep.project_flow.exception.UserAlreadyExistsException
import com.tech_dep.project_flow.service.AuthService
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    val authService: AuthService,
) {
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExistsException(ex: UserAlreadyExistsException): ResponseEntity<MessageResponseDto> {
        val response = MessageResponseDto(ex.message!!, false)
        return ResponseEntity(response, HttpStatus.CONFLICT)
    }

    @ExceptionHandler(InvalidTokenException::class)
    fun handleInvalidTokenException(ex: InvalidTokenException): ResponseEntity<MessageResponseDto> {
        val response = MessageResponseDto(ex.message!!, false)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(BadCredentialsException::class)
    fun handleBadCredentialsException(ex: BadCredentialsException): ResponseEntity<MessageResponseDto> {
        val response = MessageResponseDto(ex.message!!, false)
        return ResponseEntity(response, HttpStatus.BAD_REQUEST)
    }

    @GetMapping("/data")
    fun fetchAuthData(@CookieValue(name = "accessToken") accessToken: String): ResponseEntity<UserDto> =
        ResponseEntity.ok(authService.fetchAuthData(accessToken))

    @PostMapping("/register")
    fun register(
        @RequestBody @Validated body: RegisterRequestDto,
        response: HttpServletResponse
    ): ResponseEntity<UserDto> =
        ResponseEntity(authService.register(body, response), HttpStatus.CREATED)

    @PostMapping("/login")
    fun login(@RequestBody @Validated body: LoginRequestDto, response: HttpServletResponse): ResponseEntity<UserDto> {
        return ResponseEntity.ok(authService.login(body, response))
    }

    @PostMapping("/refresh")
    fun refresh(
        @CookieValue(name = "refreshToken", required = false) refreshToken: String?,
        response: HttpServletResponse
    ): ResponseEntity<Void> {
        authService.refresh(refreshToken, response)
        return ResponseEntity.noContent().build()
    }

    @PostMapping("/logout")
    fun logout(@RequestBody @Validated body: LogoutRequestDto, response: HttpServletResponse): ResponseEntity<Void> {
        authService.logout(body, response)
        return ResponseEntity.noContent().build()
    }
}