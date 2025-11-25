package org.itmo

import org.itmo.protocol.TextPart
import org.itmo.protocol.configuration.RabbitMQConfig
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.util.*

@Service
class TaskProducer(
    private val rabbitTemplate: RabbitTemplate,
) {

    companion object {
        const val MIN_BATCH_SIZE: Int = 16000
        private val log = LoggerFactory.getLogger(TaskProducer::class.java)
    }

    private fun sendTextPart(textPart: TextPart) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.PRODUCER_EXCHANGE,
            RabbitMQConfig.RK_TEXT_RAW,
            textPart
        )
        if (textPart.isLast) {
            log.info("Send last part {} {}", textPart.task, textPart.part)
        } else {
            log.info("Send part {} {}", textPart.task, textPart.part)
        }
    }

    fun startTask(inputStream: InputStream, placeholder: String, optTaskId: String?): String {
        val taskId = optTaskId ?: UUID.randomUUID().toString()
        BufferedReader(InputStreamReader(inputStream)).use { reader ->
            var builder = StringBuilder()
            var partNumber = 0

            var line: String
            while (reader.readLine().also { line = it } != null) {
                if (builder.isNotEmpty() && builder.length > MIN_BATCH_SIZE) {
                    sendTextPart(TextPart(taskId, ++partNumber, placeholder, false, builder.toString()))
                    builder = StringBuilder()
                }
                builder.append(line).append("\n")
            }

            sendTextPart(TextPart(taskId, ++partNumber, placeholder, true, builder.toString()))
            return taskId
        }
    }

}