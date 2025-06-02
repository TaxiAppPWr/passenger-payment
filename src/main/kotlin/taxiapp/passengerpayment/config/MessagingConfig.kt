package taxiapp.passengerpayment.config

import org.springframework.amqp.core.Binding
import org.springframework.amqp.core.BindingBuilder
import org.springframework.amqp.core.Queue
import org.springframework.amqp.core.QueueBuilder
import org.springframework.amqp.core.TopicExchange
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class MessagingConfig {
    @Value("\${rabbit.exchange.ride.name}")
    private val rideExchangeName: String? = null

    @Value("\${rabbit.queue.ppayment.name}")
    private val passengerPaymentQueueName: String? = null

    @Value("\${rabbit.topic.ppayment.generate}")
    private val generatePaymentTopic: String? = null

    @Value("\${rabbit.topic.payment.status-updated}")
    private val paymentStatusUpdated: String? = null

    @Value("\${rabbit.routing-key.ride.cancel}")
    private val rideCanceled: String? = null


    @Bean
    fun exchange(): TopicExchange {
        return TopicExchange("$rideExchangeName")
    }

    @Bean
    fun pPaymentQueue(): Queue {
        return QueueBuilder.durable("$passengerPaymentQueueName").build()
    }

    @Bean
    fun pPaymentGenerateBinding(exchange: TopicExchange, pPaymentQueue: Queue): Binding {
        return BindingBuilder.bind(pPaymentQueue).to(exchange).with("$generatePaymentTopic")
    }

    @Bean
    fun paymentStatusUpdatedBinding(exchange: TopicExchange, pPaymentQueue: Queue): Binding {
        return BindingBuilder.bind(pPaymentQueue).to(exchange).with("$paymentStatusUpdated")
    }

    @Bean
    fun rideCanceledBinding(exchange: TopicExchange, pPaymentQueue: Queue): Binding {
        return BindingBuilder.bind(pPaymentQueue).to(exchange).with("$rideCanceled")
    }
}