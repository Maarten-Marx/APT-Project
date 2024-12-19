package me.maartenmarx.interactionservice.service

import me.maartenmarx.common.dto.CommentDto
import me.maartenmarx.common.dto.CommentRequest
import me.maartenmarx.common.dto.CommentsResponse
import me.maartenmarx.common.dto.UserResponse
import me.maartenmarx.interactionservice.model.Comment
import me.maartenmarx.interactionservice.repository.CommentRepository
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import me.maartenmarx.common.service.JwtService

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    webClient: WebClient
): InteractionService(webClient) {
    fun getByThread(id: Long): CommentsResponse {
        return CommentsResponse(
            commentRepository.findByThreadId(id).map {
                var user = webClient.get()
                    .uri("$userServiceBaseUrl/api/users/" + it.userId)
                    .retrieve()
                    .bodyToMono(UserResponse::class.java)
                    .block()

                CommentDto(it.content, user)
            }
        )
    }

    fun createComment(request: CommentRequest, userData: JwtService.UserData): Long? {
        val user = getOrCreateUser(userData)

        val comment = Comment().apply {
            userId = user.id
            threadId = request.threadId
            content = request.content
        }

        return commentRepository.save(comment).id
    }
}
