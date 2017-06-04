package net.duborenko.fts

import java.util.stream.Stream

/**
 * @author Kiryl Dubarenka
 */
class AnnotationFtsTextExtractor<in Doc: Any> : (Doc) -> Stream<String?> {

    override fun invoke(doc: Doc): Stream<String?> = ReflectionUtils
            .listAnnotatedValues(doc, FtsIndexed::class.java)
            .stream()
            .map { it as String }

}