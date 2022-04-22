package com.github.davidasync.poc.e2ee.chat.room

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.server.ResponseStatusException
import java.time.Duration

@Repository
class ChatRoomRepository(
    objectMapper: ObjectMapper,
    private val redisTemplate: ReactiveStringRedisTemplate,
) {
    private val stringSerializer = StringRedisSerializer()
    private val idGenerator: ReactiveValueOperations<String, String> = redisTemplate.opsForValue()

    companion object {
        fun getChatRoomIdKey() = "ROOM:ID"
        fun getChatRoomKey(chatRoomId: Long) = "ROOM:$chatRoomId"
        fun getChatRoomMemberKey(chatRoomId: Long) = "ROOM:$chatRoomId:MEMBER"
        fun getChatRoomMemberIdKey(chatRoomId: Long) = "ROOM:$chatRoomId:MEMBER:ID"
    }

    private val charRoomMemberList: ReactiveListOperations<String, ChatRoomMember> = redisTemplate.opsForList(
        RedisSerializationContext.newSerializationContext<String, ChatRoomMember>(stringSerializer)
            .value(Jackson2JsonRedisSerializer(ChatRoomMember::class.java).apply { setObjectMapper(objectMapper) })
            .build()
    )

    private val chatRoomValue: ReactiveValueOperations<String, ChatRoom> = redisTemplate.opsForValue(
        RedisSerializationContext.newSerializationContext<String, ChatRoom>(stringSerializer)
            .value(Jackson2JsonRedisSerializer(ChatRoom::class.java).apply { setObjectMapper(objectMapper) })
            .build()
    )

    suspend fun generateChatRoomId(): Long {
        val chatRoomIdKey = getChatRoomIdKey()

        return idGenerator.increment(chatRoomIdKey).awaitSingle()
            .also { redisTemplate.expire(chatRoomIdKey, Duration.ofHours(1)).awaitSingle() }
    }

    suspend fun generateChatRoomMemberId(chatRoomId: Long): Long {
        val chatRoomMemberId = getChatRoomMemberIdKey(chatRoomId)

        return idGenerator.increment(chatRoomMemberId).awaitSingle()
            .also { redisTemplate.expire(chatRoomMemberId, Duration.ofHours(1)).awaitSingle() }
    }

    suspend fun getChatRoom(roomId: Long): ChatRoomResponse {
        val chatRoom = chatRoomValue.get(getChatRoomKey(roomId)).awaitSingleOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND")
        val chatRoomMemberKey = getChatRoomMemberKey(roomId)
        val roomMembers = charRoomMemberList.range(chatRoomMemberKey, 0, -1).collectList().awaitSingle()

        return ChatRoomResponse(
            id = roomId,
            chatRoomMembers = roomMembers,
            roomMaster = chatRoom.roomMaster,
        )
    }

    suspend fun updateChatRoomMember(updateChatRoomMemberRequest: UpdateChatRoomMemberRequest): ChatRoomResponse {
        val chatRoomMembers = charRoomMemberList
            .range(getChatRoomMemberKey(updateChatRoomMemberRequest.chatRoomId), 0, -1)
            .collectList()
            .awaitSingle()

        val chatRoomMember = ChatRoomMember(
            id = updateChatRoomMemberRequest.chatRoomMemberId,
            username = updateChatRoomMemberRequest.username,
            rsaPublicKey = updateChatRoomMemberRequest.rsaPublicKey,
            encryptedSecretKey = null,
        )

        val chatRoomMemberIndex = chatRoomMembers.indexOf(chatRoomMember)
        chatRoomMember.encryptedSecretKey = updateChatRoomMemberRequest.encryptedSecretKey

        charRoomMemberList.set(
            getChatRoomMemberKey(updateChatRoomMemberRequest.chatRoomId),
            chatRoomMemberIndex.toLong(),
            chatRoomMember,
        ).awaitSingleOrNull() ?:
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "CANNOT_UPDATE")

        return getChatRoom(updateChatRoomMemberRequest.chatRoomId)
    }

    suspend fun createChatRoom(createChatRoomRequest: CreateChatRoomRequest): ChatRoomResponse = coroutineScope {
        val chatRoomId = generateChatRoomId()
        val chatRoomKey = getChatRoomKey(chatRoomId)
        val chatRoomMemberId = generateChatRoomMemberId(chatRoomId)
        val chatRoomMemberKey = "ROOM:$chatRoomId:MEMBER"

        val chatRoomMember = ChatRoomMember(
            id = chatRoomMemberId,
            username = createChatRoomRequest.username,
            rsaPublicKey = createChatRoomRequest.rsaPublicKey,
        )

        val chatRoom = ChatRoom(
            id = chatRoomId,
            roomMaster = chatRoomMember,
        )

        launch {
            charRoomMemberList.leftPush(chatRoomMemberKey, chatRoomMember).awaitSingleOrNull()
            chatRoomValue.set(chatRoomKey, chatRoom).awaitSingleOrNull()
        }

        val chatRoomMembers = charRoomMemberList
            .range(chatRoomMemberKey, 0, -1)
            .collectList()
            .awaitSingle()

        return@coroutineScope ChatRoomResponse(
            id = chatRoomId,
            chatRoomMembers = chatRoomMembers,
            roomMaster = chatRoomMember,
        )
    }

    suspend fun joinChatRoom(joinChatRoomRequest: JoinChatRoomRequest): ChatRoomResponse {
        val chatRoomKey = getChatRoomKey(joinChatRoomRequest.roomId)
        val chatRoomMemberKey = getChatRoomMemberKey(joinChatRoomRequest.roomId)
        val chatRoomMemberId = generateChatRoomMemberId(joinChatRoomRequest.roomId)

        val chatRoom = chatRoomValue.get(chatRoomKey).awaitSingleOrNull() ?:
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "ROOM_${joinChatRoomRequest.roomId}_NOT_FOUND")

        val chatRoomMember = ChatRoomMember(
            id = chatRoomMemberId,
            username = joinChatRoomRequest.username,
            rsaPublicKey = joinChatRoomRequest.rsaPublicKey,
        )

        charRoomMemberList.leftPush(chatRoomMemberKey, chatRoomMember).awaitSingleOrNull() ?:
            throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "ROOM_${joinChatRoomRequest.roomId}_CANNOT_JOIN")

        val chatRoomMembers = charRoomMemberList
            .range(chatRoomMemberKey, 0, -1)
            .collectList()
            .awaitSingle()

        return ChatRoomResponse(
            id = joinChatRoomRequest.roomId,
            chatRoomMembers = chatRoomMembers,
            roomMaster = chatRoom.roomMaster,
        )
    }
}
