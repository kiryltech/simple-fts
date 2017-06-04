package net.duborenko.fts.extractors

import net.duborenko.fts.FtsIndexed
import net.duborenko.fts.utils.ReflectionUtils
import java.util.stream.Stream

/**
 * @author Kiryl Dubarenka
 */
internal class AnnotationFtsTextExtractor<in Doc: Any> : (Doc) -> List<String?> {

    override fun invoke(doc: Doc): List<String?> = ReflectionUtils
            .listAnnotatedValues(doc, FtsIndexed::class.java)
            .map { it as String }

}