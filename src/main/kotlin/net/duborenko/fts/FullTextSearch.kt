package net.duborenko.fts

import net.duborenko.fts.core.InMemoryFullTextSearchIndex
import net.duborenko.fts.extractors.AnnotationFtsIdExtractor
import net.duborenko.fts.extractors.AnnotationFtsTextExtractor

/**
 * @author Kiryl Dubarenka
 */
object FullTextSearch {

    fun <Id : Comparable<Id>, Doc : Any> createIndexWithAnnotationExtractor(
            wordFilter: ((String) -> Boolean)? = null,
            rank: ((SearchResult<Doc>) -> Int)? = null
    ): FullTextSearchIndex<Id, Doc> =
            if (wordFilter == null)
                InMemoryFullTextSearchIndex(
                        getId = AnnotationFtsIdExtractor(),
                        textExtractor = AnnotationFtsTextExtractor(),
                        rank = rank
                )
            else
                InMemoryFullTextSearchIndex(
                        getId = AnnotationFtsIdExtractor(),
                        textExtractor = AnnotationFtsTextExtractor(),
                        wordFilter = wordFilter,
                        rank = rank
                )


}

