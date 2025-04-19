package com.tech_dep.project_flow.config

import com.tech_dep.project_flow.dto.MessageResponseDto
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.authentication.DisabledException

@Component
class CustomAuthenticationEntryPoint(
    val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    val log = KotlinLogging.logger {}

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.error { authException.message }
        val message: String

        if (authException is DisabledException) {
            response.status = HttpServletResponse.SC_FORBIDDEN
            message = "User is disabled"
        } else {
            response.status = HttpServletResponse.SC_UNAUTHORIZED
            message = "Unauthorized access - please log in first"
        }

        response.contentType = "application/json"

        val messageResponse = MessageResponseDto(
            message = message,
            success = false
        )

        response.writer.write(objectMapper.writeValueAsString(messageResponse))
    }
}