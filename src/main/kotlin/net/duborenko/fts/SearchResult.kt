package net.duborenko.fts

/**
 * @author Kiryl Dubarenka
 */
data class SearchResult<Doc: Any>(
        val document: Doc,
        val matches: Map<String, Set<Keyword>>
)