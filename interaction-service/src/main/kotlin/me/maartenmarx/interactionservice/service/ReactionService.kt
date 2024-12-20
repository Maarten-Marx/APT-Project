package me.maartenmarx.interactionservice.service

import me.maartenmarx.common.dto.ReactionDto
import me.maartenmarx.common.dto.ReactionRequest
import me.maartenmarx.common.dto.ReactionsResponse
import me.maartenmarx.common.dto.UserResponse
import me.maartenmarx.interactionservice.model.Reaction
import me.maartenmarx.interactionservice.repository.ReactionRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import me.maartenmarx.common.service.JwtService
import org.springframework.kafka.core.KafkaTemplate

@Service
class ReactionService(
    private val reactionRepository: ReactionRepository,
    private val kafkaTemplate: KafkaTemplate<String, String?>,
    webClient: WebClient
): InteractionService(webClient) {
    fun getByThread(id: Long): ReactionsResponse {
        return ReactionsResponse(
            reactionRepository.findByThreadId(id).map {
                var user = webClient.get()
                    .uri("http://$userServiceBaseUrl/api/users/" + it.userId)
                    .retrieve()
                    .bodyToMono(UserResponse::class.java)
                    .block()

                ReactionDto(it.emoji, user)
            }
        )
    }

    fun getByUser(id: String): ReactionsResponse {
        return ReactionsResponse(
            reactionRepository.findByUserId(id).map {
                ReactionDto(it.emoji, null)
            }
        )
    }

    fun createReaction(request: ReactionRequest, userData: JwtService.UserData): Long? {
        val user = getOrCreateUser(userData)

        val reaction = Reaction().apply {
            userId = user.id
            threadId = request.threadId
            emoji = request.emoji
        }

        return reactionRepository.save(reaction).id.also {
            kafkaTemplate.send("reaction", user.id)
        }
    }
}
