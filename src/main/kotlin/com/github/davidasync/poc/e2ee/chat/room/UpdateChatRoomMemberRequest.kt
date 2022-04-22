package com.github.davidasync.poc.e2ee.chat.room

data class UpdateChatRoomMemberRequest(
    val username: String = "",
    val chatRoomId: Long = 0L,
    val chatRoomMemberId: Long = 0L,
    val rsaPublicKey: String = "",
    val encryptedSecretKey: String = "",
)
