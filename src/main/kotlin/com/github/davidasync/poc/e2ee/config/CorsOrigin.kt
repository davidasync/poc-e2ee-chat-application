package com.github.davidasync.poc.e2ee.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties(prefix = "cors-origin")
data class CorsOrigin(
    var origin: Set<String> = emptySet(),
)
