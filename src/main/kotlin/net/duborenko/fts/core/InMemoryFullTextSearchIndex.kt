package net.duborenko.fts.core

import net.duborenko.fts.FullTextSearchIndex
import net.duborenko.fts.Keyword
import net.duborenko.fts.SearchResult

/**
 * Primitive imitation of full-text search index.

 * @author Kiryl Dubarenka
 */
internal class InMemoryFullTextSearchIndex<in Id : Comparable<Id>, Doc : Any>(
        private val getId: (Doc) -> Id,
        private val textExtractor: (Doc) -> Map<String, String?>,
        private val wordFilter: (String) -> Boolean = { it.length > 3 }) : FullTextSearchIndex<Doc> {

    private val keywords = hashMapOf<Id, Map<String, Set<Keyword>>>()
    private val index = hashMapOf<String, MutableMap<Id, Doc>>()

    override fun add(document: Doc) {
        val existingKeywords = keywords.getOrDefault(document.id, mapOf())
                .values
                .asSequence()
                .flatten()
                .map { it.word }
                .toSet()

        val newKeywordsMap = keywordsMap(textExtractor(document))
        val newKeywords = newKeywordsMap
                .values
                .asSequence()
                .flatten()
                .map { it.word }
                .toSet()

        // add new keyword
        (newKeywords - existingKeywords)
                .forEach { addToIndex(document, it) }

        // cleanup old keywords
        (existingKeywords - newKeywords)
                .forEach { removeFromIndex(document, it) }

        keywords.put(document.id, newKeywordsMap)
    }

    private fun keywordsMap(data: Map<String, String?>): Map<String, Set<Keyword>> {
        return data
                .asSequence()
                .filterNotNull()
                .map { (field, text) -> field to extractKeywords(text) }    // split into words
                .toMap()
    }

    private fun extractKeywords(text: String?): Set<Keyword> {
        text ?: return emptySet()
        return Regex("[a-z0-9]+", RegexOption.IGNORE_CASE)
                .findAll(text)
                .filter { wordFilter(it.value) }
                .map { Keyword(it.value.toLowerCase(), it.range) }
                .toSet()
    }

    private fun removeFromIndex(document: Doc, word: String) {
        val documents = index[word] ?: return
        documents -= document.id
        if (documents.isEmpty()) {
            index -= word
        }
    }

    private fun addToIndex(document: Doc, word: String) {
        index.computeIfAbsent(word) { hashMapOf() } += document.id to document
    }

    override fun remove(document: Doc) {
        keywords.remove(document.id)
                ?.values
                ?.asSequence()
                ?.flatten()
                ?.map { it.word }
                ?.toSet()
                ?.forEach { removeFromIndex(document, it) }
    }

    override fun search(searchTerm: String): List<SearchResult<Doc>> {
        val kws = extractKeywords(searchTerm)
                .map { it.word }
        val numbersOfMatches: Map<Doc, List<Doc>> = kws
                .asSequence()
                .map { index[it]?.values }
                .filterNotNull()
                .flatMap { it.asSequence() }
                .groupBy { it }

        return numbersOfMatches.entries
                .asSequence()
                .sortedWith(compareByDescending<Map.Entry<Doc, List<Doc>>> { it.value.size }
                        .thenComparing { e -> e.key.id })
                .map { it.key }
                .map { SearchResult(it, getMatches(keywords[it.id]!!, kws.toSet())) }
                .toList()
    }

    private fun getMatches(allKeywords: Map<String, Set<Keyword>>, searchKeywords: Set<String>) =
            allKeywords
                    .asSequence()
                    .map { (field, kws) ->
                        field to kws
                                .filter { it.word in searchKeywords }
                                .toSet()
                    }
                    .toMap()

    private val Doc.id
        get() = getId(this)

}
