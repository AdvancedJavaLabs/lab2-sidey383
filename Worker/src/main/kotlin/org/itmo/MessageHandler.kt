package org.itmo

import com.rabbitmq.client.Channel
import org.itmo.protocol.ProcessedTextPart
import org.itmo.protocol.TextPart
import org.itmo.protocol.configuration.RabbitMQConfig
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.AmqpHeaders
import org.springframework.messaging.handler.annotation.Header
import org.springframework.messaging.handler.annotation.Payload
import org.springframework.stereotype.Service

@Service
class MessageHandler(
    private val rabbitTemplate: RabbitTemplate,
    private val textProcessor: TextProcessor
) {

    companion object {
        private val log = LoggerFactory.getLogger(MessageHandler::class.java)
    }

    @RabbitListener(queues = [RabbitMQConfig.WORKER_QUEUE])
    fun handleTestPart(
        @Payload textPart: TextPart,
        channel: Channel,
        @Header(AmqpHeaders.DELIVERY_TAG) tag: Long
    ) {
        try {
            log.info("Receive part {} {} isLast:{}", textPart.task, textPart.part, textPart.isLast)
            val result = textProcessor.processText(textPart.text, textPart.namePlaceholder)

            val processedPart = ProcessedTextPart(
                task = textPart.task,
                part = textPart.part,
                isLast = textPart.isLast,
                text = result.text,
                wordCount = result.wordCount,
                wordCountMap = result.wordCountMap,
                textTone = result.textTone,
                sentences = result.sentences
            )
            sendProcessedText(processedPart)
            channel.basicAck(tag, false)
            log.info("Complete part {} {} isLast:{}", textPart.task, textPart.part, textPart.isLast)
        } catch (e: Exception) {
            log.error("Message processing error", e)
            throw e
        }
    }

    fun sendProcessedText(textPart: ProcessedTextPart) {
        rabbitTemplate.convertAndSend(
            RabbitMQConfig.WORKER_EXCHANGE,
            RabbitMQConfig.RK_TEXT_PROCESSED,
            textPart
        )
        if (textPart.isLast) {
            log.info("Send last part {} {}", textPart.task, textPart.part)
        } else {
            log.info("Send part {} {}", textPart.task, textPart.part)
        }
    }

}