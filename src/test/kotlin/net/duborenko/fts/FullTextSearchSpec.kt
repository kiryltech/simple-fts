package net.duborenko.fts

import com.google.common.truth.Truth.assertThat
import net.duborenko.fts.utils.ReflectionUtils
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.util.UUID

/**
 * @author Kiryl Dubarenka
 */
object FullTextSearchSpec : Spek({

    operator fun <Id : Comparable<Id>, Doc : Any> FullTextSearchIndex<Id, Doc>.plusAssign(doc: Doc) = this.add(doc)

    operator fun <Id : Comparable<Id>, Doc : Any> FullTextSearchIndex<Id, Doc>.minusAssign(id: Id) = this.remove(id)

    operator fun Document.get(prop: String) = ReflectionUtils.getPropertyValue(this, prop)

    given("empty FTS index") {
        val fts = FullTextSearch.createIndexWithAnnotationExtractor<UUID, Document>()

        on("search of 'test'") {
            val docs = fts.search("test").map { it.document }
            it("should return nothing") {
                assertThat(docs).isEmpty()
            }
        }
    }

    given("FTS index with two documents and custom ranking") {
        val fts = FullTextSearch.createIndexWithAnnotationExtractor<UUID, Document>(
                wordFilter = { it.isNotEmpty() },
                rank = { it.matches.values.sumBy { it.size } }
        )

        val doc1 = Document(
                title = "Document #1",
                description = "Description of document number one")
        val doc2 = Document(
                title = "Document #2",
                description = "Description of document number two")

        fts += doc1
        fts += doc2

        on("search of 'document'") {
            val results = fts.search("document")
            it("should return both documents") {
                assertThat(results.map { it.document }).containsExactly(doc1, doc2)
            }
            it("should return valid matches") {
                results.forEach { (doc, matches) ->
                    matches.asSequence()
                            .flatMap { (prop, kws) -> kws.asSequence().map { prop to it } }
                            .forEach { (prop, match) ->
                                val actualWord = doc[prop].toString().substring(match.position).toLowerCase()
                                assertThat(actualWord).isEqualTo(match.word)
                            }
                }
            }
        }

        on("search of 'one'") {
            val docs = fts.search("one").map { it.document }
            it("should return document #1") {
                assertThat(docs).containsExactly(doc1)
            }
        }

        on("search of 'two'") {
            val docs = fts.search("two").map { it.document }
            it("should return document #2") {
                assertThat(docs).containsExactly(doc2)
            }
        }

        on("search of 'document two'") {
            val docs = fts.search("document two").map { it.document }
            it("should return [document #2, document #1]") {
                assertThat(docs).containsExactly(doc2, doc1).inOrder()
            }
        }
    }

    given("FTS index with updated document") {
        val fts = FullTextSearch.createIndexWithAnnotationExtractor<UUID, Document>(wordFilter = { it.isNotEmpty() })

        val doc1 = Document(
                title = "Document #1",
                description = "Description of document number one")
        val doc2 = Document(
                title = "Document #2",
                description = "Description of document number two")

        fts += doc1
        fts += doc2

        val doc1Updated = Document(
                id = doc1.id,
                title = "Document #3",
                description = "Description of document number three")
        fts += doc1Updated

        on("search of 'document'") {
            val docs = fts.search("document").map { it.document }
            it("should return both documents") {
                assertThat(docs).containsExactly(doc1, doc2)
            }
        }

        on("search of 'one'") {
            val docs = fts.search("one").map { it.document }
            it("should return nothing") {
                assertThat(docs).isEmpty()
            }
        }

        on("search of 'two'") {
            val docs = fts.search("two").map { it.document }
            it("should return document #2") {
                assertThat(docs).containsExactly(doc2)
            }
        }

        on("search of 'three'") {
            val docs = fts.search("three").map { it.document }
            it("should return updated document #1") {
                assertThat(docs).containsExactly(doc1Updated)
            }
        }
    }

    given("FTS index with removed document") {
        val fts = FullTextSearch.createIndexWithAnnotationExtractor<UUID, Document>(wordFilter = { it.isNotEmpty() })

        val doc1 = Document(
                title = "Document #1",
                description = "Description of document number one")
        val doc2 = Document(
                title = "Document #2",
                description = "Description of document number two")

        fts += doc1
        fts += doc2
        fts -= doc1.id

        on("search of 'document'") {
            val docs = fts.search("document").map { it.document }
            it("should return both documents") {
                assertThat(docs).containsExactly(doc2)
            }
        }

        on("search of 'one'") {
            val docs = fts.search("one").map { it.document }
            it("should return nothing") {
                assertThat(docs).isEmpty()
            }
        }

        on("search of 'two'") {
            val docs = fts.search("two").map { it.document }
            it("should return document #2") {
                assertThat(docs).containsExactly(doc2)
            }
        }
    }

})
