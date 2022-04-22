package com.github.davidasync.poc.e2ee.chat.room

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping
class ChatRoomController(
    private val chatRoomRepository: ChatRoomRepository
) {
    @GetMapping("/{roomId}")
    suspend fun getChatRoom(
        @PathVariable roomId: Long,
    ): ChatRoomResponse {
        return chatRoomRepository.getChatRoom(roomId)
    }

    @PostMapping
    suspend fun createChatRoom(
        @RequestBody createChatRoomRequest: CreateChatRoomRequest
    ): ChatRoomResponse {
        return chatRoomRepository.createChatRoom(createChatRoomRequest)
    }

    @PatchMapping
    suspend fun updateChatMember(
        @RequestBody updateChatRoomMemberRequest: UpdateChatRoomMemberRequest
    ): ChatRoomResponse {
        return chatRoomRepository.updateChatRoomMember(updateChatRoomMemberRequest)
    }

    @PostMapping("/join")
    suspend fun joinChatRoom(
        @RequestBody joinChatRoomRequest: JoinChatRoomRequest,
    ): ChatRoomResponse {
        return chatRoomRepository.joinChatRoom(joinChatRoomRequest)
    }
}
