package me.maartenmarx.interactionservice.service

import dto.UserRequest
import dto.UserResponse
import me.maartenmarx.interactionservice.repository.CommentRepository
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import service.JwtService

@Service
class InteractionService(
    private val webClient: WebClient
) {
    protected fun getOrCreateUser(userData: JwtService.UserData): UserResponse {
        var user = webClient.get()
            .uri("http://localhost:8083/api/users/email/" + userData.getEmail())
            .retrieve()
            .onStatus({ s -> s.equals(HttpStatus.NOT_FOUND) }) { _ ->
                Mono.empty()
            }
            .bodyToMono(UserResponse::class.java)
            .block()

        if (user?.id != null) {
            return user
        } else {
            var req = UserRequest().apply {
                email = userData.email
                username = userData.name
            }

            return webClient.post()
                .uri("http://localhost:8083/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(req), UserRequest::class.java)
                .retrieve()
                .bodyToMono(UserResponse::class.java)
                .block() ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}