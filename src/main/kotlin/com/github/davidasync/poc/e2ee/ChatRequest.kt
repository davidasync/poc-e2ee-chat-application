package com.github.davidasync.poc.e2ee

data class ChatRequest(
    val message: String = "",
    val username: String = "",
)