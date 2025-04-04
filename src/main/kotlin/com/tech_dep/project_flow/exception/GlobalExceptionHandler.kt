package com.tech_dep.project_flow.exception

import com.tech_dep.project_flow.dto.MessageResponseDto
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.MissingServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

@RestControllerAdvice
class GlobalExceptionHandler {
    private val log = KotlinLogging.logger {}

    @ExceptionHandler(ResourceNotFoundException::class)
    fun handleResourceNotFoundException(ex: ResourceNotFoundException): ResponseEntity<MessageResponseDto> {
        val response = MessageResponseDto(ex.message!!, false)
        return ResponseEntity<MessageResponseDto>(response, HttpStatus.NOT_FOUND)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleParsingException(ex: Exception): ResponseEntity<MessageResponseDto> {
        log.error { ex.message }
        val response = MessageResponseDto("Error occurred while parsing JSON", false)
        return ResponseEntity<MessageResponseDto>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleValidationException(ex: MethodArgumentTypeMismatchException): ResponseEntity<MessageResponseDto> {
        log.error { ex.message }
        val response = MessageResponseDto("Wrong parameter types", false)
        return ResponseEntity<MessageResponseDto>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<MessageResponseDto> {
        val response =
            MessageResponseDto(ex.bindingResult.fieldErrors.map {
                it.defaultMessage ?: "Validation error on field: ${it.field}!"
            }.joinToString(" "), false)
        return ResponseEntity<MessageResponseDto>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): ResponseEntity<MessageResponseDto> {
        val response =
            MessageResponseDto(ex.constraintViolations.map { "Field ${it.propertyPath}, error: ${it.message}!" }
                .joinToString(" "), false)
        return ResponseEntity<MessageResponseDto>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(MissingServletRequestParameterException::class)
    fun handleMissingRequestParameterException(ex: MissingServletRequestParameterException): ResponseEntity<MessageResponseDto> {
        val response = MessageResponseDto("Parameter ${ex.parameterName} is missing", false)
        return ResponseEntity<MessageResponseDto>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDeniedException(ex: AccessDeniedException): ResponseEntity<MessageResponseDto> {
        val response = MessageResponseDto(ex.message!!, false)
        return ResponseEntity<MessageResponseDto>(response, HttpStatus.FORBIDDEN)
    }
}