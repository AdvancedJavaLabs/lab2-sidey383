package org.itmo

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.CoreEntityMention
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import org.itmo.protocol.Sentence
import org.springframework.stereotype.Service
import kotlin.streams.asSequence

@Service
class TextProcessor (
    val stanfordCoreNLP: StanfordCoreNLP
) {

    fun processText(text: String, namePlaceholder: String): Result {
        val document = CoreDocument(text)
        stanfordCoreNLP.annotate(document)

        val wordCount = document.tokens().size

        val wordCounterMap = document.tokens().stream().map { l -> l.lemma() }
            .asSequence()
            .filterNotNull()
            .groupingBy { s -> s }
            .eachCount()
            .toMap()

        val sentiments = document.sentences().stream()
            .map { s -> s.sentiment()}
            .asSequence()
            .mapNotNull { s -> s ?: "Unknown" }
            .groupingBy { s -> s }
            .eachCount()
            .toMap()

        val sentences = document.sentences().asSequence()
            .map { s -> s.charOffsets() }
            .map { o -> Sentence(o.first(), o.second(), o.second() - o.first())  }
            .sorted()
            .toList()
            .asReversed()

        val coreEntities = document.entityMentions().stream()
            .sorted(compareBy<CoreEntityMention>{ em -> em.charOffsets().first})
            .toList()

        val textBuilder = StringBuilder()

        var lastIndex = 0

        for (coreEntity in coreEntities) {
            val offsets = coreEntity.charOffsets()
            textBuilder.append(text.substring(lastIndex, offsets.first()))
            textBuilder.append(namePlaceholder)
            lastIndex = offsets.second()
        }

        textBuilder.append(text.substring(lastIndex))

        return Result(
            textBuilder.toString(),
            wordCount,
            wordCounterMap,
            sentiments,
            sentences
        )
    }

    class Result(
        val text: String,
        val wordCount: Int,
        val wordCountMap: Map<String, Int>,
        val textTone: Map<String, Int>,
        val sentences: List<Sentence>
    )

}