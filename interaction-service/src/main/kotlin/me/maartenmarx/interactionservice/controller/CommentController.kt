package me.maartenmarx.interactionservice.controller

import dto.CommentRequest
import dto.CommentsResponse
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
import service.JwtService

@RestController
@RequestMapping("/api/comments")
class CommentController(
    private val commentService: CommentService
) {
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun getByPost(@PathVariable id: Long): CommentsResponse {
        return commentService.getByThread(id)
    }

    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    fun createComment(@RequestBody request: CommentRequest, @RequestHeader("Authorization") bearer: String) {
        val userData = JwtService.getUserData(bearer)

        commentService.createComment(request, userData)
    }
}