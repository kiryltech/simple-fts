package net.duborenko.fts.extractors

import net.duborenko.fts.FtsIndexed
import net.duborenko.fts.utils.ReflectionUtils

/**
 * @author Kiryl Dubarenka
 */
internal class AnnotationFtsTextExtractor<in Doc : Any> : (Doc) -> Map<String, String?> {

    override fun invoke(doc: Doc): Map<String, String?> = ReflectionUtils
            .listAnnotatedValues(doc, FtsIndexed::class.java)
            .asSequence()
            .map { (field, value) -> field to (value as String) }
            .toMap()

}