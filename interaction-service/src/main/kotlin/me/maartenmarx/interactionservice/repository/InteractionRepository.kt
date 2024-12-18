package me.maartenmarx.interactionservice.repository

import me.maartenmarx.interactionservice.model.Interaction

interface InteractionRepository<T : Interaction> {
    fun findByThreadId(id: Long): Iterable<T>
}