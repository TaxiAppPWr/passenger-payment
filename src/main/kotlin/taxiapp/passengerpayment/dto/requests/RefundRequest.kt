package taxiapp.passengerpayment.dto.requests;

data class RefundRequest (
    val orderId: String,
    val description: String = "Refund",
    val amount: String
)
