package net.duborenko.fts.utils

import kotlin.reflect.KProperty

/**
 * @author Kiryl Dubarenka
 */
internal object ReflectionUtils {

    private fun <Doc: Any> listProperties(doc: Doc) =
        doc::class.members.asSequence()
                .filter { it is KProperty }

    private fun <Doc : Any> annotatedValuesSequence(doc: Doc, annotationType: Class<*>): Sequence<Pair<String, Any?>> {
        return listProperties(doc)
                .filter { it.annotations.find { a -> a.annotationClass.java == annotationType } != null }
                .map { it.name to it.call(doc) }
    }

    fun <Doc: Any> listAnnotatedValues(doc: Doc, annotationType: Class<*>) =
            annotatedValuesSequence(doc, annotationType).toList()

    fun <Doc: Any> getAnnotatedValue(doc: Doc, annotationType: Class<*>) =
            annotatedValuesSequence(doc, annotationType).first()

    fun <Doc: Any> getPropertyValue(doc: Doc, prop: String) =
            listProperties(doc).first { it.name == prop }.call(doc)

}