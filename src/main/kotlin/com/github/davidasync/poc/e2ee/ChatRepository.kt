package com.github.davidasync.poc.e2ee

import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactor.awaitSingle
import kotlinx.coroutines.reactor.awaitSingleOrNull
import org.springframework.data.domain.Range
import org.springframework.data.redis.core.ReactiveSetOperations
import org.springframework.data.redis.core.ReactiveStringRedisTemplate
import org.springframework.data.redis.core.ReactiveValueOperations
import org.springframework.data.redis.core.ReactiveZSetOperations
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Repository
import org.springframework.web.server.ResponseStatusException
import java.time.Duration

@Repository
class ChatRepository(
    objectMapper: ObjectMapper,
    private val redisTemplate: ReactiveStringRedisTemplate
) {
    private val stringSerializer = StringRedisSerializer()

    private val idGenerator: ReactiveValueOperations<String, String> = redisTemplate.opsForValue()
    private val charRoomMemberSet: ReactiveSetOperations<String, String> = redisTemplate.opsForSet()

    private val chatSet: ReactiveZSetOperations<String, Chat> = redisTemplate.opsForZSet(
        RedisSerializationContext.newSerializationContext<String, Chat>(stringSerializer)
            .value(Jackson2JsonRedisSerializer(Chat::class.java).apply { setObjectMapper(objectMapper) })
            .build()
    )

    private val chatRoomValue: ReactiveValueOperations<String, ChatRoom> = redisTemplate.opsForValue(
        RedisSerializationContext.newSerializationContext<String, ChatRoom>(stringSerializer)
            .value(Jackson2JsonRedisSerializer(ChatRoom::class.java).apply { setObjectMapper(objectMapper) })
            .build()
    )

    suspend fun generateChatRoomId(): Long {
        val chatRoomIdKey = "ROOM:ID"

        return idGenerator.increment(chatRoomIdKey).awaitSingle()
            .also { redisTemplate.expire(chatRoomIdKey, Duration.ofHours(1)).awaitSingle() }
    }

    suspend fun generateChatId(roomId: Long): Long {
        val chatIdKey = "ROOM:$roomId:CHAT:ID"

        return idGenerator.increment(chatIdKey).awaitSingle()
            .also { redisTemplate.expire(chatIdKey, Duration.ofHours(1)).awaitSingle() }
    }

    suspend fun getChatRoom(roomId: Long): ChatRoom {
        return chatRoomValue.get("ROOM:$roomId").awaitSingleOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND")
    }

    suspend fun getChats(roomId: Long): List<Chat> {
        return chatSet.rangeByScore("ROOM:$roomId:CHAT", Range.open(0.0, Double.MAX_VALUE)).collectList().awaitSingleOrNull()
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "ROOM_NOT_FOUND")
    }

    suspend fun createChatRoom(chatRoomRequest: ChatRoomRequest): ChatRoom = coroutineScope {
        val chatRoomId = generateChatRoomId()
        val chatRoomKey = "ROOM:$chatRoomId"
        val chatRoomMemberKey = "ROOM:$chatRoomId:MEMBER"

        val chatRoom = ChatRoom(
            id = chatRoomId,
            member = listOf(chatRoomRequest.username)
        )

        launch {
            charRoomMemberSet.add(chatRoomMemberKey, chatRoomRequest.username).awaitSingleOrNull()
            chatRoomValue.set(chatRoomKey, chatRoom).awaitSingleOrNull()
        }

        return@coroutineScope chatRoom
    }

    suspend fun pushChat(roomId: Long, chatRequest: ChatRequest): Boolean? = coroutineScope {
        val isRoomIdExists = redisTemplate.hasKey("ROOM:$roomId").awaitSingleOrNull()
        val roomMember = charRoomMemberSet.isMember("ROOM:$roomId:MEMBER", chatRequest.username).awaitSingleOrNull()

        if (isRoomIdExists == null || isRoomIdExists == false || roomMember == null || roomMember == false) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "ROOM_OR_USERNAME_NOT_FOUND")
        }

        val chatKey = "ROOM:$roomId:CHAT"
        val chatId = generateChatId(roomId)

        val chat = Chat(
            id = chatId,
            message = chatRequest.message,
            roomId = roomId,
            username = chatRequest.username,
            timestamp = System.currentTimeMillis(),
        )

        val result = chatSet.add(
            chatKey,
            chat,
            chat.id.toDouble(),
        ).awaitSingleOrNull()

        launch {
            redisTemplate.expire(chatKey, Duration.ofHours(1)).awaitSingleOrNull()
            redisTemplate.convertAndSend("CHAT", roomId.toString()).awaitSingleOrNull()
        }

        return@coroutineScope result
    }
}
