package com.github.davidasync.poc.e2ee.chat.room

data class ChatRoomMember(
    val id: Long = 0L,
    val username: String = "",
    val rsaPublicKey: String = "",
    var encryptedSecretKey: String? = null,
)
