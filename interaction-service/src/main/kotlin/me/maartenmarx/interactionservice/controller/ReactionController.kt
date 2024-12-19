package me.maartenmarx.interactionservice.controller

import me.maartenmarx.common.dto.ReactionRequest
import me.maartenmarx.common.dto.ReactionsResponse
import me.maartenmarx.interactionservice.service.ReactionService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*
import me.maartenmarx.common.service.JwtService

@RestController
@RequestMapping("/api/reactions")
class ReactionController(
    private val reactionService: ReactionService
) {
    @GetMapping("/thread/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getByThread(@PathVariable id: Long): ReactionsResponse {
        return reactionService.getByThread(id)
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getByUser(@PathVariable id: String): ReactionsResponse {
        return reactionService.getByUser(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createReaction(@RequestBody request: ReactionRequest, @RequestHeader("Authorization") bearer: String) {
        val userData = JwtService.getUserData(bearer)

        reactionService.createReaction(request, userData)
    }
}