package taxiapp.passengerpayment.messaging

import com.rabbitmq.client.Channel
import org.springframework.amqp.core.Message
import taxiapp.passengerpayment.dto.event.GeneratePaymentEvent
import taxiapp.passengerpayment.service.PassengerPaymentService
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component
import taxiapp.passengerpayment.dto.event.CancelRideEvent
import taxiapp.passengerpayment.dto.event.PaymentStatusUpdatedEvent

@RabbitListener(queues = ["\${rabbit.queue.ppayment.name}"], ackMode = "MANUAL")
@Component
class PaymentMessagesReceiver(
    private val passengerPaymentService: PassengerPaymentService
) {
    @RabbitHandler
    fun receiveGeneratePaymentEvent(event: GeneratePaymentEvent, channel: Channel, message: Message) {
        try {
            passengerPaymentService.createPayment(event)
            channel.basicAck(message.messageProperties.deliveryTag, false)
        } catch (ex: Exception) {
            channel.basicNack(message.messageProperties.deliveryTag, false, true)
        }
    }

    @RabbitHandler
    fun receivePaymentStatusUpdatedEvent(event: PaymentStatusUpdatedEvent, channel: Channel, message: Message) {
        passengerPaymentService.updatePaymentStatus(event)
        channel.basicAck(message.messageProperties.deliveryTag, false)
    }

    @RabbitHandler
    fun rideCanceledEvent(event: CancelRideEvent, channel: Channel, message: Message) {
        passengerPaymentService.refundPayment(event)
        channel.basicAck(message.messageProperties.deliveryTag, false)
    }

    @RabbitHandler(isDefault = true)
    fun receiveDefault(event: Any) {
        println("Payment default event")
    }
}