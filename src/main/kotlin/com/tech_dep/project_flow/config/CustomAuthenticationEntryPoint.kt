package com.tech_dep.project_flow.config

import com.tech_dep.project_flow.dto.MessageResponseDto
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import com.fasterxml.jackson.databind.ObjectMapper
import io.github.oshai.kotlinlogging.KotlinLogging

@Component
class CustomAuthenticationEntryPoint(
    val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        val log = KotlinLogging.logger {}
        log.error { authException.message }

        response.status = HttpServletResponse.SC_UNAUTHORIZED
        response.contentType = "application/json"

        val messageResponse = MessageResponseDto(
            message = "Unauthorized access - please log in first",
            success = false
        )

        response.writer.write(objectMapper.writeValueAsString(messageResponse))
    }
}