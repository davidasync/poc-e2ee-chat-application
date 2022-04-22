package com.github.davidasync.poc.e2ee.chat.room

data class CreateChatRoomRequest(
    val username: String = "",
    val rsaPublicKey: String = "",
)
