package me.maartenmarx.interactionservice.repository

import me.maartenmarx.interactionservice.model.Reaction
import org.springframework.data.jpa.repository.JpaRepository

interface ReactionRepository : JpaRepository<Reaction, Long>, InteractionRepository<Reaction>