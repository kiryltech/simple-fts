package net.duborenko.fts.extractors

import net.duborenko.fts.FtsIndexed
import net.duborenko.fts.utils.ReflectionUtils
import java.util.stream.Stream

/**
 * @author Kiryl Dubarenka
 */
internal class AnnotationFtsTextExtractor<in Doc: Any> : (Doc) -> Stream<String?> {

    override fun invoke(doc: Doc): Stream<String?> = ReflectionUtils
            .listAnnotatedValues(doc, FtsIndexed::class.java)
            .stream()
            .map { it as String }

}