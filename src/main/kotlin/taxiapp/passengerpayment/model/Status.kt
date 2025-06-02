package taxiapp.passengerpayment.model

enum class Status {
    NEW,
    SUCCESS,
    COMPLETED,
    WAITING_FOR_CONFIRMATION,
    PENDING,
    CANCELED,
    FAILED,
    FINALIZED
}
