package taxiapp.passengerpayment.dto.event

data class CancelRideEvent(
    val cancelRideEventId: Long,
    val rideId: Long,
    val refundPercentage: Int
)