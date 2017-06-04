package net.duborenko.fts

/**
 * @author Kiryl Dubarenka
 */
class AnnotationFtsIdExtractor<in Doc: Any, out Id> : (Doc) -> Id {

    override fun invoke(doc: Doc): Id =
            ReflectionUtils.getAnnotatedValue(doc, FtsId::class.java) as Id

}