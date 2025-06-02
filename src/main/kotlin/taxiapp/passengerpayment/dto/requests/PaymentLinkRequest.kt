package taxiapp.passengerpayment.dto.requests

import taxiapp.passengerpayment.model.Buyer

data class PaymentLinkRequest (
    val totalAmount: String,
    val buyer: Buyer
)