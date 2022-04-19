package com.github.davidasync.poc.e2ee

data class Chat(
    var id: Long = 0,
    val message: String = "",
    val roomId: Long = 0L,
    val username: String = "",
    val timestamp: Long = System.currentTimeMillis(),
)