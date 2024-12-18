package me.maartenmarx.interactionservice.repository

import me.maartenmarx.interactionservice.model.Comment
import org.springframework.data.jpa.repository.JpaRepository

interface CommentRepository: JpaRepository<Comment, Long>, InteractionRepository<Comment>
