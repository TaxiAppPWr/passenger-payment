package taxiapp.passengerpayment.dto.response

data class PaymentLinkResponse(
    val status: String,
    val response: PaymentLinkBody
)