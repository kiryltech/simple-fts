package net.duborenko.fts.utils

import com.google.common.truth.Truth.assertThat
import net.duborenko.fts.Document
import net.duborenko.fts.FtsId
import net.duborenko.fts.FtsIndexed
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

/**
 * @author Kiryl Dubarenka
 */
object ReflectionUtilsSpec : Spek({

    given("kotlin data object: Document") {
        val doc = Document(
                title = "title",
                description = null
        )

        on("list FtsIndexed values") {
            val values = ReflectionUtils.listAnnotatedValues(doc, FtsIndexed::class.java)
            it("should return [('title', 'title'), ('description', null)]") {
                assertThat(values).containsExactly("title" to "title", "description" to null)
            }
        }

        on("list FtsId value") {
            val id = ReflectionUtils.getAnnotatedValue(doc, FtsId::class.java)
            it("should return ('id', ${doc.id})") {
                assertThat(id).isEqualTo("id" to doc.id)
            }
        }
    }

})
