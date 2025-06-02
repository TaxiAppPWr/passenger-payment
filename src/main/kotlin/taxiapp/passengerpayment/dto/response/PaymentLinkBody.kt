package taxiapp.passengerpayment.dto.response

data class PaymentLinkBody(
    val status: Object,
    val redirectUri: String,
    val orderId: String
)