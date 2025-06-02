package taxiapp.passengerpayment.dto.event

data class GeneratePaymentEvent(
    val generatePaymentEventId: Long,
    val rideId: Long,
    val amount: Int,
    val firstname: String,
    val lastname: String,
    val phoneNumber: String,
    val email: String
)