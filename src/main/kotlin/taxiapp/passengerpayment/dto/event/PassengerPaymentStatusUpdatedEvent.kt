package taxiapp.passengerpayment.dto.event

data class PassengerPaymentStatusUpdatedEvent (
    val paymentConfirmedEventId: Long,
    val rideId: Long,
    val status: String
)