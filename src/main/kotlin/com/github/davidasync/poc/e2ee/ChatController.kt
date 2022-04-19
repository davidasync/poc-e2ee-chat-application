package com.github.davidasync.poc.e2ee

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ChatController(
    private val chatRepository: ChatRepository
) {
    @GetMapping("/{roomId}")
    suspend fun getChatRoom(
        @PathVariable roomId: Long,
    ): ChatRoom {
        return chatRepository.getChatRoom(roomId)
    }

    @PostMapping
    suspend fun createChatRoom(
        @RequestBody chatRoomRequest: ChatRoomRequest
    ): ChatRoom {
        return chatRepository.createChatRoom(chatRoomRequest)
    }

    @GetMapping("/{roomId}/chat")
    suspend fun getChat(
        @PathVariable roomId: Long,
    ): List<Chat> {
        return chatRepository.getChats(roomId)
    }

    @PostMapping("/{roomId}/chat")
    suspend fun pushChat(
        @PathVariable roomId: Long,
        @RequestBody chatRequest: ChatRequest
    ): Boolean? {
        return chatRepository.pushChat(roomId, chatRequest)
    }
}
