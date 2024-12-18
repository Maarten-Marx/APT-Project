package me.maartenmarx.interactionservice.service

import me.maartenmarx.common.dto.ReactionDto
import me.maartenmarx.common.dto.ReactionRequest
import me.maartenmarx.common.dto.ReactionsResponse
import me.maartenmarx.interactionservice.model.Reaction
import me.maartenmarx.interactionservice.repository.ReactionRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import me.maartenmarx.common.service.JwtService

@Service
class ReactionService(
    private val reactionRepository: ReactionRepository,
    webClient: WebClient
): InteractionService(webClient) {
    fun getByThread(id: Long): ReactionsResponse {
        return ReactionsResponse(
            reactionRepository.findByThreadId(id).map {
                ReactionDto(it.emoji, it.userId)
            }
        )
    }

    fun createReaction(request: ReactionRequest, userData: JwtService.UserData) {
        val user = getOrCreateUser(userData)

        val reaction = Reaction().apply {
            userId = user.id
            threadId = request.threadId
            emoji = request.emoji
        }

        reactionRepository.save(reaction)
    }
}
