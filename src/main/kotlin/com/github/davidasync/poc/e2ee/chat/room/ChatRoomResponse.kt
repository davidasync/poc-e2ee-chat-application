package com.github.davidasync.poc.e2ee.chat.room

data class ChatRoomResponse(
    val id: Long = 0L,
    val roomMaster: ChatRoomMember = ChatRoomMember(),
    val chatRoomMembers: MutableList<ChatRoomMember> = mutableListOf()
)
