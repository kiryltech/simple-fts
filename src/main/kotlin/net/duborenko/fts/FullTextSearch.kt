package net.duborenko.fts

import net.duborenko.fts.core.InMemoryFullTextSearchIndex
import net.duborenko.fts.extractors.AnnotationFtsIdExtractor
import net.duborenko.fts.extractors.AnnotationFtsTextExtractor

/**
 * @author Kiryl Dubarenka
 */
object FullTextSearch {

    fun <Id : Comparable<Id>, Doc : Any> createIndexWithAnnotationExtractor(): FullTextSearchIndex<Doc> =
            InMemoryFullTextSearchIndex<Id, Doc>(
                    getId = AnnotationFtsIdExtractor(),
                    textExtractor = AnnotationFtsTextExtractor()
            )

    fun <Id : Comparable<Id>, Doc : Any> createIndexWithAnnotationExtractor(wordFilter: (String) -> Boolean): FullTextSearchIndex<Doc> =
            InMemoryFullTextSearchIndex<Id, Doc>(
                    getId = AnnotationFtsIdExtractor(),
                    textExtractor = AnnotationFtsTextExtractor(),
                    wordFilter = wordFilter
            )

}

