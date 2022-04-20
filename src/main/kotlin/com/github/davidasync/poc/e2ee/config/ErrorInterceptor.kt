package com.github.davidasync.poc.e2ee.config

import org.slf4j.LoggerFactory
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.mapping.PropertyReferenceException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.ServerWebInputException
import javax.validation.ConstraintViolationException

@ControllerAdvice
class ErrorInterceptor {
    private val logger = LoggerFactory.getLogger(ErrorInterceptor::class.java)

    @ExceptionHandler(value = [Exception::class])
    fun handleException(
        request: ServerHttpRequest,
        error: Exception
    ): ResponseEntity<Void> {
        logger.error("Undefined error | ${error.stackTraceToString()}", error, request)

        return ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(value = [DataIntegrityViolationException::class])
    fun handleException(
        request: ServerHttpRequest,
        error: DataIntegrityViolationException
    ): ResponseEntity<String> {
        logger.error("Data integrity violation found | ${error.stackTraceToString()}", error, request)
        return ResponseEntity(error.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [IllegalArgumentException::class])
    fun handleException(
        request: ServerHttpRequest,
        error: IllegalArgumentException
    ): ResponseEntity<String> {
        logger.error("Illegal argument found | ${error.stackTraceToString()}", error, request)
        return ResponseEntity(error.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [ServerWebInputException::class])
    fun handleException(
        request: ServerHttpRequest,
        error: ServerWebInputException
    ): ResponseEntity<String> {
        logger.warn("Missing param in request | ${error.stackTraceToString()}", error, request)
        return ResponseEntity(error.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [ResponseStatusException::class])
    fun handleException(
        request: ServerHttpRequest,
        error: ResponseStatusException
    ): ResponseEntity<String> {
        logger.warn("Custom error handled | ${error.stackTraceToString()}", error, request)
        return ResponseEntity(error.reason ?: error.message, error.status)
    }

    @ExceptionHandler(value = [ConstraintViolationException::class])
    fun handleException(
        error: ConstraintViolationException
    ): ResponseEntity<String> {
        val message = "Constraint violation | ${error.stackTraceToString()}"
        logger.warn(message, error, null)
        return ResponseEntity(message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [MethodArgumentNotValidException::class])
    fun handleException(
        error: MethodArgumentNotValidException
    ): ResponseEntity<String> {
        logger.warn(error.stackTraceToString(), error, null)
        return ResponseEntity(error.message, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(value = [PropertyReferenceException::class])
    fun handleException(
        error: PropertyReferenceException
    ): ResponseEntity<String> {
        logger.warn(error.stackTraceToString(), error, null)
        return ResponseEntity(error.message, HttpStatus.BAD_REQUEST)
    }
}