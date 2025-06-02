package taxiapp.passengerpayment.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter
import org.springframework.amqp.support.converter.MessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import taxiapp.passengerpayment.dto.event.CancelRideEvent
import taxiapp.passengerpayment.dto.event.GeneratePaymentEvent
import taxiapp.passengerpayment.dto.event.PaymentStatusUpdatedEvent

@Configuration
class RabbitConfig {

    @Bean
    fun jsonMessageConverter(): MessageConverter {
        val mapper = ObjectMapper().apply {
            registerKotlinModule()
        }

        val typeMapper = DefaultJackson2JavaTypeMapper().apply {
            setTrustedPackages("*")
            setIdClassMapping(
                mapOf(
                    "taxiapp.ride.dto.event.GeneratePaymentEvent" to GeneratePaymentEvent::class.java,
                    "com.example.payu.dto.PaymentStatusUpdatedEvent" to PaymentStatusUpdatedEvent::class.java,
                    "taxiapp.ride.dto.event.CancelRideEvent" to CancelRideEvent::class.java
                )
            )
        }

        return Jackson2JsonMessageConverter(mapper).apply {
            setJavaTypeMapper(typeMapper)
        }
    }


    @Bean
    fun rabbitTemplate(connectionFactory: ConnectionFactory, jsonMessageConverter: MessageConverter): RabbitTemplate {
        return RabbitTemplate(connectionFactory).apply {
            messageConverter = jsonMessageConverter
        }
    }
}
