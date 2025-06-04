package taxiapp.passengerpayment.service

import org.slf4j.LoggerFactory
import org.springframework.amqp.core.DirectExchange
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import taxiapp.passengerpayment.repository.PassengerPaymentRepository
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder
import taxiapp.passengerpayment.dto.event.CancelRideEvent
import taxiapp.passengerpayment.dto.response.PaymentLinkResponse
import taxiapp.passengerpayment.dto.event.PaymentStatusUpdatedEvent
import taxiapp.passengerpayment.dto.event.GeneratePaymentEvent
import taxiapp.passengerpayment.dto.event.PassengerPaymentStatusUpdatedEvent
import taxiapp.passengerpayment.dto.requests.PaymentLinkRequest
import taxiapp.passengerpayment.dto.requests.RefundRequest
import taxiapp.passengerpayment.model.Buyer
import taxiapp.passengerpayment.model.PaymentInfo
import taxiapp.passengerpayment.model.Status

@Service
class PassengerPaymentServiceImpl @Autowired constructor(
    private val passengerPaymentRepository: PassengerPaymentRepository,
    @Qualifier("rideExchange") private val rideExchange: DirectExchange,
    private val template: RabbitTemplate,
    private val restTemplate: RestTemplate,
    @Value("\${service.address.payment}")
    private val paymentServiceAddress: String,
    @Value("\${rabbit.topic.ppayment.status-updated}")
    private val pPaymentStatusUpdatedTopic: String
) : PassengerPaymentService {
    private val logger = LoggerFactory.getLogger(PassengerPaymentService::class.java)

    override fun getPaymentLink(rideId: Long): ResponseEntity<String> {
        val paymentInfo = passengerPaymentRepository.findById(rideId)
        if (paymentInfo.isEmpty) {
            return ResponseEntity.notFound().build()
        } else if (paymentInfo.get().paymentUrl == null) {
            return ResponseEntity.internalServerError().body("Payment link is missing")
        }
        return ResponseEntity.ok().body(paymentInfo.get().paymentUrl)
    }


    override fun createPayment(paymentInfo: GeneratePaymentEvent) {
        val linkGenerationUri = UriComponentsBuilder
            .fromUriString("$paymentServiceAddress/api/payu/payment")
            .build()
            .toUri()

        // TODO: Retrieve passenger personal data from AWS Cognito by paymentInfo.passengerUsername

        val request = PaymentLinkRequest(
            totalAmount = paymentInfo.amount.toString(),
            buyer = Buyer(
                email = paymentInfo.email,
                phone =  paymentInfo.phoneNumber,
                firstName = paymentInfo.firstname,
                lastName = paymentInfo.lastname
            )
        )

        val response = restTemplate.postForEntity(linkGenerationUri, request, PaymentLinkResponse::class.java)

        if (!response.statusCode.is2xxSuccessful) {
            logger.error("Failed to generate payment link for payu {}", response.body)
            throw  IllegalStateException("Payment link returned status code ${response.statusCode}")
        }

        val body = response.body!!

        val newPayment = PaymentInfo(
            rideId = paymentInfo.rideId,
            fare = paymentInfo.amount,
            paymentId = body.response.orderId,
            paymentUrl = body.response.redirectUri
        )
        logger.info("Payment successfully generated for ride: ${paymentInfo.rideId}")
        passengerPaymentRepository.save(newPayment)
    }

    override fun updatePaymentStatus(statusUpdateInfo: PaymentStatusUpdatedEvent) {
        val payment: PaymentInfo? = passengerPaymentRepository.findByPaymentId(statusUpdateInfo.paymentId)

        val newStatus = try {
            Status.valueOf(statusUpdateInfo.status)
        } catch (e: Error) {
            logger.error("Value of updated payment status is not valid")
            return
        }

        if (payment != null) {
            payment.status = newStatus
            passengerPaymentRepository.save(payment)
            if (payment.status == Status.COMPLETED || payment.status == Status.FAILED || payment.status == Status.CANCELED) {
                val event = PassengerPaymentStatusUpdatedEvent (
                    paymentConfirmedEventId = statusUpdateInfo.paymentStatusUpdatedEventId,
                    rideId = payment.rideId,
                    status = payment.status.toString()
                )
                logger.info("Passenger payment status for ride: ${payment.rideId} updated to ${payment.status}. Event sent to event bus.")
                template.convertAndSend(rideExchange.name, pPaymentStatusUpdatedTopic, event)
            }
        }
    }

    override fun refundPayment(cancelRideEvent: CancelRideEvent) {
        val payment = passengerPaymentRepository.findById(cancelRideEvent.rideId)
        if (payment.isEmpty) {
            return
        }

        if (payment.get().status != Status.COMPLETED) {
            logger.info("Ride ${cancelRideEvent.rideId}'s payment has status ${payment.get().status}, not sending refund request to payment provider.")
            return
        }

        val returnAmount = payment.get().fare * cancelRideEvent.refundPercentage / 100

        val refundUri = UriComponentsBuilder
            .fromUriString("$paymentServiceAddress/api/payu/refund")
            .build()
            .toUri()

        val request = RefundRequest(
            orderId = payment.get().paymentId,
            amount = returnAmount.toString()
        )

        val response = restTemplate.postForEntity(refundUri, request, Object::class.java)
        logger.info("Refunded ride ${cancelRideEvent.rideId} with ${cancelRideEvent.refundPercentage}% refund.")
        return
    }

}

