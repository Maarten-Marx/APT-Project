package me.maartenmarx.interactionservice.service

import me.maartenmarx.common.dto.UserRequest
import me.maartenmarx.common.dto.UserResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono
import me.maartenmarx.common.service.JwtService
import org.springframework.beans.factory.annotation.Value

@Service
@Suppress("RedundantModalityModifier")
open class InteractionService(
    protected val webClient: WebClient
) {
    @Value("\${services.users.baseurl}")
    protected var userServiceBaseUrl: String = ""

    protected fun getOrCreateUser(userData: JwtService.UserData): UserResponse {
        var user = webClient.get()
            .uri("http://$userServiceBaseUrl/api/users/email/" + userData.getEmail())
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
                .uri("http://$userServiceBaseUrl/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(req), UserRequest::class.java)
                .retrieve()
                .bodyToMono(UserResponse::class.java)
                .block() ?: throw ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}