package taxiapp.passengerpayment.service

import org.springframework.http.ResponseEntity
import taxiapp.passengerpayment.dto.event.CancelRideEvent
import taxiapp.passengerpayment.dto.event.PaymentStatusUpdatedEvent
import taxiapp.passengerpayment.dto.event.GeneratePaymentEvent

interface PassengerPaymentService {
    fun getPaymentLink(rideId: Long): ResponseEntity<String>
    fun createPayment(paymentInfo: GeneratePaymentEvent)
    fun updatePaymentStatus(confirmationInfo: PaymentStatusUpdatedEvent)
    fun refundPayment(cancelRideEvent: CancelRideEvent)
}