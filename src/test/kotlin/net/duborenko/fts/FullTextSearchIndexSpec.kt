package net.duborenko.fts

import com.google.common.truth.Truth.assertThat
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on
import java.util.UUID
import java.util.stream.Collectors
import java.util.stream.Stream

/**
 * @author Kiryl Dubarenka
 */
object FullTextSearchIndexSpec : Spek({

    fun <T> Stream<T>.asList() = this.collect(Collectors.toList())

    operator fun <Id : Comparable<Id>, Doc> FullTextSearchIndex<Id, Doc>.plusAssign(doc: Doc) =
        this.add(doc)

    operator fun <Id : Comparable<Id>, Doc> FullTextSearchIndex<Id, Doc>.minusAssign(doc: Doc) =
        this.remove(doc)

    given("empty FTS index") {
        val fts = FullTextSearchIndex<UUID, Document>(
                getId = { it.id },
                textExtractor = { Stream.of(it.title, it.description) }
        )

        on("search of 'test'") {
            val docs = fts.search("test").asList()
            it("should return nothing") {
                assertThat(docs).isEmpty()
            }
        }
    }

    given("FTS index with two documents") {
        val fts = FullTextSearchIndex<UUID, Document>(
                getId = { it.id },
                textExtractor = { Stream.of(it.title, it.description) },
                wordFilter = { it != null && it.isNotEmpty() }
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
            val docs = fts.search("document").asList()
            it("should return both documents") {
                assertThat(docs).containsExactly(doc1, doc2)
            }
        }

        on("search of 'one'") {
            val docs = fts.search("one").asList()
            it("should return document #1") {
                assertThat(docs).containsExactly(doc1)
            }
        }

        on("search of 'two'") {
            val docs = fts.search("two").asList()
            it("should return document #2") {
                assertThat(docs).containsExactly(doc2)
            }
        }
    }

    given("FTS index with updated document") {
        val fts = FullTextSearchIndex<UUID, Document>(
                getId = { it.id },
                textExtractor = { Stream.of(it.title, it.description) },
                wordFilter = { it != null && it.isNotEmpty() }
        )

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
            val docs = fts.search("document").asList()
            it("should return both documents") {
                assertThat(docs).containsExactly(doc1, doc2)
            }
        }

        on("search of 'one'") {
            val docs = fts.search("one").asList()
            it("should return nothing") {
                assertThat(docs).isEmpty()
            }
        }

        on("search of 'two'") {
            val docs = fts.search("two").asList()
            it("should return document #2") {
                assertThat(docs).containsExactly(doc2)
            }
        }

        on("search of 'three'") {
            val docs = fts.search("three").asList()
            it("should return updated document #1") {
                assertThat(docs).containsExactly(doc1Updated)
            }
        }
    }

    given("FTS index with removed document") {
        val fts = FullTextSearchIndex<UUID, Document>(
                getId = { it.id },
                textExtractor = { Stream.of(it.title, it.description) },
                wordFilter = { it != null && it.isNotEmpty() }
        )

        val doc1 = Document(
                title = "Document #1",
                description = "Description of document number one")
        val doc2 = Document(
                title = "Document #2",
                description = "Description of document number two")

        fts += doc1
        fts += doc2
        fts -= doc1

        on("search of 'document'") {
            val docs = fts.search("document").asList()
            it("should return both documents") {
                assertThat(docs).containsExactly(doc2)
            }
        }

        on("search of 'one'") {
            val docs = fts.search("one").asList()
            it("should return nothing") {
                assertThat(docs).isEmpty()
            }
        }

        on("search of 'two'") {
            val docs = fts.search("two").asList()
            it("should return document #2") {
                assertThat(docs).containsExactly(doc2)
            }
        }
    }

})
