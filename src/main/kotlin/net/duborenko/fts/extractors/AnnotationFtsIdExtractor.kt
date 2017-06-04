package net.duborenko.fts.extractors

import net.duborenko.fts.FtsId
import net.duborenko.fts.utils.ReflectionUtils

/**
 * @author Kiryl Dubarenka
 */
internal class AnnotationFtsIdExtractor<in Doc: Any, out Id> : (Doc) -> Id {

    override fun invoke(doc: Doc): Id =
            ReflectionUtils.getAnnotatedValue(doc, FtsId::class.java).second as Id

}