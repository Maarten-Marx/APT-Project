package me.maartenmarx.interactionservice.controller

import me.maartenmarx.common.dto.CommentRequest
import me.maartenmarx.common.dto.CommentsResponse
import me.maartenmarx.interactionservice.service.CommentService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import me.maartenmarx.common.service.JwtService

@RestController
@RequestMapping("/api/comments")
class CommentController(
    private val commentService: CommentService
) {
    @GetMapping("/thread/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getByThread(@PathVariable id: Long): CommentsResponse {
        return commentService.getByThread(id)
    }

    @GetMapping("/user/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getByUser(@PathVariable id: String): CommentsResponse {
        return commentService.getByUser(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createComment(@RequestBody request: CommentRequest, @RequestHeader("Authorization") bearer: String) {
        val userData = JwtService.getUserData(bearer)

        commentService.createComment(request, userData)
    }
}