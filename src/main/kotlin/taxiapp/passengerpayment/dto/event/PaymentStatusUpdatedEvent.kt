package taxiapp.passengerpayment.dto.event

data class PaymentStatusUpdatedEvent(
    val paymentStatusUpdatedEventId: Long,
    val paymentId: String,
    val status: String
)