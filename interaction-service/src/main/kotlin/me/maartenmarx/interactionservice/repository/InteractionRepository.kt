package me.maartenmarx.interactionservice.repository

import me.maartenmarx.interactionservice.model.Interaction

interface InteractionRepository<T : Interaction> {
    fun findByThreadId(id: Long): List<T>
    fun findByUserId(id: String): List<T>
}