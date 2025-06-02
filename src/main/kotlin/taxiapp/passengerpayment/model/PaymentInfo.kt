package taxiapp.passengerpayment.model;

import jakarta.persistence.*

@Entity(name = "payment_info")
data class PaymentInfo (
    @Id
    var rideId: Long,

    @Column
    var fare: Int,
    @Column
    val paymentId: String,
    @Column(length = 2048)
    var paymentUrl: String?,
    @Column
    @Enumerated(EnumType.STRING)
    var status: Status = Status.PENDING
)