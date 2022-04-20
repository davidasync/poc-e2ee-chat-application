/*
 * Copyright (c) 2022 LINE Corporation. All rights reserved.
 * LINE Corporation PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.github.davidasync.poc.e2ee.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.cors.reactive.CorsUtils
import org.springframework.web.server.ResponseStatusException
import org.springframework.web.server.WebFilter
import reactor.core.publisher.Mono

@Configuration
class CorsConfig {
    @Bean
    fun corsFilter(
        corsOrigin: CorsOrigin,
    ): WebFilter {
        return WebFilter { ctx, chain ->
            val request = ctx.request
            if (CorsUtils.isCorsRequest(request)) {
                val allowOrigin = checkOrigin(corsOrigin.origin, request.headers.origin)
                val response = ctx.response
                val headers = response.headers
                headers.add("Access-Control-Allow-Origin", allowOrigin)
                headers.add("Access-Control-Allow-Methods", "*")
                headers.add("Access-Control-Max-Age", "7200")
                headers.add(
                    "Access-Control-Allow-Headers",
                    "x-requested-with, x-access-token, Content-Type, Authorization, credential, X-XSRF-TOKEN, X-ACCESS-TOKEN"
                )
                if (request.method === HttpMethod.OPTIONS) {
                    response.statusCode = HttpStatus.OK
                    return@WebFilter Mono.empty<Void>()
                }
            }
            chain.filter(ctx)
        }
    }

    private fun checkOrigin(allowedOrigin: Set<String>, originHeader: String?): String {
        if (originHeader.isNullOrEmpty()) {
            throw forbidden()
        }
        if (allowedOrigin.contains("*")) {
            return "*"
        }
        if (allowedOrigin.contains(originHeader)) {
            return originHeader
        }
        throw forbidden()
    }

    private fun forbidden() = ResponseStatusException(HttpStatus.FORBIDDEN, "ORIGIN_FORBIDDEN")
}
