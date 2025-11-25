package org.itmo.protocol.configuration

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.DirectExchange
import org.springframework.amqp.core.Queue
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RabbitMQConfig {

    companion object {
        const val PRODUCER_EXCHANGE = "app.text.processing"
        const val WORKER_QUEUE = "app.worker.text.processing"
        const val WORKER_EXCHANGE = "app.text.results"
        const val AGGREGATOR_QUEUE = "app.aggregator.results"
        const val RK_TEXT_RAW = "text.chunk"
        const val RK_TEXT_PROCESSED = "text.processed"
    }


    @Bean
    fun jsonMessageConverter(): Jackson2JsonMessageConverter {
        return Jackson2JsonMessageConverter()
    }

    @Bean
    fun producerExchange(): DirectExchange {
        return DirectExchange(PRODUCER_EXCHANGE)
    }

    @Bean
    fun workerExchange(): DirectExchange {
        return DirectExchange(WORKER_EXCHANGE)
    }

    @Bean
    fun workerQueue(): Queue {
        return Queue(WORKER_QUEUE, true)
    }

    @Bean
    fun aggregatorQueue(): Queue {
        return Queue(AGGREGATOR_QUEUE, true)
    }

    @Bean
    fun workerInputBinging(): Binding {
        return BindingBuilder.bind(workerQueue())
            .to(producerExchange())
            .with(RK_TEXT_RAW)
    }

    @Bean
    fun aggregatorInputBinging(): Binding {
        return BindingBuilder.bind(aggregatorQueue())
            .to(workerExchange())
            .with(RK_TEXT_PROCESSED)
    }

}