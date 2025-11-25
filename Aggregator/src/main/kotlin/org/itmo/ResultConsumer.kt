package org.itmo

import com.rabbitmq.client.Channel
import org.itmo.protocol.ProcessedTextPart
import org.itmo.protocol.configuration.RabbitMQConfig
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service
import java.util.concurrent.ConcurrentHashMap
import kotlin.io.path.Path
import kotlin.io.path.bufferedWriter

@Service
class ResultConsumer {

    val collectors: ConcurrentHashMap<String, Pair<ResultCollector, MutableList<Runnable>>> = ConcurrentHashMap()

    companion object {
        private val log = LoggerFactory.getLogger(ResultConsumer::class.java)
    }

    @RabbitListener(queues = [RabbitMQConfig.AGGREGATOR_QUEUE])
    fun handleProcessedTestPart(
        @Payload part: ProcessedTextPart,
        channel: Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) tag: Long
    ) {
        log.info("Receive part {} {} isLast:{}", part.task, part.part, part.isLast)
        val taskData = collectors.computeIfAbsent(part.task) { task ->
            Pair(ResultCollector(task = task), mutableListOf())
        }

        taskData.second.add {
            channel.basicAck(tag, false)
        }

        val resultConsumer = taskData.first

        if (resultConsumer.consume(part)) {
            log.info("Collect all results for task {}", resultConsumer.task)
            saveResult(taskData.first)
            collectors.remove(part.task)
            taskData.second.forEach{ r -> r.run()}
            log.info("Complete task task {}", resultConsumer.task)
        } else {
            val totalCount = resultConsumer.totalCount.get()
            log.info("Process part {} {}/{}", resultConsumer.task, resultConsumer.textParts.size , if (totalCount == 0) "Unknown" else totalCount)
        }
    }

    private fun saveResult(collector: ResultCollector) {
        log.info("Start save results for {}", collector.task)
        val sentenceOutFile = Path("out", collector.task + "_sentence.txt")
        val wordOutFile = Path("out", collector.task + "_words.json")
        val textOutFile = Path("out", collector.task + ".txt")
        val textParts = collector.textParts
        sentenceOutFile.bufferedWriter().use { writer ->
            val sentencesMap = collector.sentences
            sentencesMap.entries
                .asSequence()
                .flatMap { entry ->
                    entry.value.asSequence().map { sentence ->
                        Pair(entry.key, sentence)
                    }
                }
                .sortedByDescending { it.second }
                .forEach { (key, sentence) ->
                    val part = textParts[key]
                    val len = sentence.length
                    val sentenceString = part!!.substring(sentence.start, sentence.end)
                    writer.append("$len $sentenceString\n")
                }
        }
        wordOutFile.bufferedWriter().use { writer ->
            val wordCount = collector.worldCount
            writer.append("""
            {
                "totalWords": $wordCount,
            """.trimIndent()).append("\n")
            collector.textTone
                .asSequence()
                .joinTo(writer, ",\n") { (tone, count) ->
                    "\t\"$tone\": $count"
                }
            writer.append(",\n")
            writer.append("\t\"words\": {\n")
            collector.wordCountMap
                .asSequence()
                .sortedBy { it.value }
                .joinTo(writer, ",\n") { (key, value) ->
                    "\t\t\"$key\":$value"
                }

            writer.append("\n").append("""
                }
            }
            """.trimIndent())
        }
        textOutFile.bufferedWriter().use { writer ->
            collector.textParts
                .asSequence()
                .sortedBy { it.key }
                .map { it.value }
                .joinTo(writer, "\n") {v -> v}
        }
        log.info("Write all results for task {}", collector.task)
    }

}