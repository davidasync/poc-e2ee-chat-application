package com.github.davidasync.poc.e2ee

data class ChatRoom(
    val id: Long = 0L,
    val encryptedSymmetricKey: String = "",
    val member: List<String> = listOf()
)
