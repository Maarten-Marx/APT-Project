package me.maartenmarx.interactionservice.controller

import dto.ReactionRequest
import dto.ReactionsResponse
import me.maartenmarx.interactionservice.service.ReactionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import service.JwtService

@RestController
@RequestMapping("/api/reactions")
class ReactionController(
    private val reactionService: ReactionService
) {
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getByThread(@PathVariable id: Long): ReactionsResponse {
        return reactionService.getByThread(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createReaction(@RequestBody request: ReactionRequest, @RequestHeader("Authorization") bearer: String) {
        val userData = JwtService.getUserData(bearer)

        reactionService.createReaction(request, userData)
    }
}