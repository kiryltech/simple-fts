package net.duborenko.fts

import net.duborenko.fts.core.FullTextSearchIndex

/**
 * @author Kiryl Dubarenka
 */
object FullTextSearch {

    fun <Id : Comparable<Id>, Doc : Any> createIndexWithAnnotationExtractor() =
            FullTextSearchIndex<Id, Doc>(
                    getId = AnnotationFtsIdExtractor(),
                    textExtractor = AnnotationFtsTextExtractor()
            )

    fun <Id : Comparable<Id>, Doc : Any> createIndexWithAnnotationExtractor(wordFilter: (String) -> Boolean) =
            FullTextSearchIndex<Id, Doc>(
                    getId = AnnotationFtsIdExtractor(),
                    textExtractor = AnnotationFtsTextExtractor(),
                    wordFilter = wordFilter
            )

}

