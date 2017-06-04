package net.duborenko.fts

import java.util.UUID

data class Document(
        @FtsId val id: UUID = UUID.randomUUID(),
        @FtsIndexed val title: String,
        @FtsIndexed val description: String?
)