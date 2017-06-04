package net.duborenko.fts.core

import net.duborenko.fts.FullTextSearchIndex
import java.util.Spliterator
import java.util.Spliterators
import java.util.stream.Stream
import java.util.stream.StreamSupport

/**
 * Primitive imitation of full-text search index.

 * @author Kiryl Dubarenka
 */
internal class InMemoryFullTextSearchIndex<in Id : Comparable<Id>, Doc : Any>(
        private val getId: (Doc) -> Id,
        private val textExtractor: (Doc) -> Stream<String?>,
        private val wordFilter: (String) -> Boolean = { it.length > 3 }) : FullTextSearchIndex<Doc> {

    private val keywords = hashMapOf<Id, Set<String>>()
    private val index = hashMapOf<String, MutableMap<Id, Doc>>()

    override fun add(document: Doc) {
        val existingKeywords = keywords.getOrDefault(document.id, setOf())

        val newKeywords: Set<String> = keywordsSeq(textExtractor(document).asSequence())
                .toHashSet()

        // add new keyword
        (newKeywords - existingKeywords)
                .forEach { addToIndex(document, it) }

        // cleanup old keywords
        (existingKeywords - newKeywords)
                .forEach { removeFromIndex(document, it) }

        keywords.put(document.id, newKeywords)
    }

    private fun keywordsSeq(data: Sequence<String?>): Sequence<String> {
        return data
                .asSequence()
                .filterNotNull()
                .flatMap { it.trim().split("\\s+".toRegex()).asSequence() } // split into words
                .map { it.toLowerCase() }                                   // to lower case
                .map { it.replace("[^a-z0-9]".toRegex(), "") }              // remove all non-alphanumeric characters
                .filter(wordFilter)                                         // filter out insignificant keywords
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
                ?.forEach { removeFromIndex(document, it) }
    }

    override fun search(searchTerm: String): Stream<Doc> {
        val numbersOfMatches: Map<Doc, List<Doc>> = keywordsSeq(listOf(searchTerm).asSequence())
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
                .toStream()
    }

    private val Doc.id
        get() = getId(this)

    private fun <T> Sequence<T>.toStream(): Stream<T> =
            StreamSupport.stream(
                    Spliterators.spliteratorUnknownSize(this.iterator(), Spliterator.ORDERED),
                    false)

    private fun <T> Stream<T>.asSequence() = Sequence { this.iterator() }
}
