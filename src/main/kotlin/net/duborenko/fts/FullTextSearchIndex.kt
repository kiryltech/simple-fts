package net.duborenko.fts

/**
 * @author Kiryl Dubarenka
 */
interface FullTextSearchIndex<in Id : Comparable<Id>, Doc : Any> {

    fun add(document: Doc)

    fun remove(id: Id)

    fun search(searchTerm: String): List<SearchResult<Doc>>

}