package com.github.davidasync.poc.e2ee.chat.room

data class JoinChatRoomRequest(
    val roomId: Long = 0L,
    val username: String = "",
    val rsaPublicKey: String = "",
)