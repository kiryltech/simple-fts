package net.duborenko.fts

import java.util.UUID

data class Document(
        val id: UUID = UUID.randomUUID(),
        val title: String,
        val description: String?
)