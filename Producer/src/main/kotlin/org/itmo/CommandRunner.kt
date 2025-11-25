package org.itmo

import org.itmo.protocol.TextPart
import org.itmo.protocol.configuration.RabbitMQConfig
import org.slf4j.LoggerFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.stereotype.Service
import java.io.File
import java.util.*

@Service
class CommandRunner(
    private val rabbitTemplate: RabbitTemplate,
    private val applicationContext: ConfigurableApplicationContext
) : ApplicationRunner {

    companion object {
        const val DEFAULT_PLACEHOLDER: String = "[NAME]"
        const val MIN_BATCH_SIZE: Int = 16000
        private val log = LoggerFactory.getLogger(CommandRunner::class.java)
    }

    fun sendTextPart(textPart: TextPart) {
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

    override fun run(args: ApplicationArguments) {
        try {
            val placeholder = if (args.containsOption("p")) {
                args.getOptionValues("p")?.first() ?: DEFAULT_PLACEHOLDER
            } else {
                DEFAULT_PLACEHOLDER
            }
            if (args.containsOption("i")) {
                for (inputFileName in args.getOptionValues("i").filterNotNull()) {
                    val taskId = UUID.randomUUID().toString()
                    File(inputFileName).bufferedReader().use { reader ->
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
                    }
                }
            }
        } finally {
            applicationContext.close()
        }
    }


}