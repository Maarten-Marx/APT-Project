package me.maartenmarx.interactionservice.model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id

@Entity
class Reaction(): Interaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null
    var emoji: String = ""
    override var threadId: Long? = null
    override var userId: String? = null

    constructor(id: Long?, emoji: String, threadId: Long?, userId: String?) : this() {
        this.id = id
        this.emoji = emoji
        this.threadId = threadId
        this.userId = userId
    }
}