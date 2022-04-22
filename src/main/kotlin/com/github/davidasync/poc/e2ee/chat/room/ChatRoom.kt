package com.github.davidasync.poc.e2ee.chat.room

data class ChatRoom(
    val id: Long = 0L,
    val roomMaster: ChatRoomMember,
)
