package net.duborenko.fts

import java.util.stream.Stream

/**
 * @author Kiryl Dubarenka
 */
interface FullTextSearchIndex<Doc: Any> {

    fun add(document: Doc)

    fun remove(document: Doc)

    fun search(searchTerm: String): List<SearchResult<Doc>>

}