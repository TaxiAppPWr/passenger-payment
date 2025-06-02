package taxiapp.passengerpayment.repository;

import taxiapp.passengerpayment.model.PaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

interface PassengerPaymentRepository : JpaRepository<PaymentInfo, Long> {
    fun findByPaymentId(paymentId: String): PaymentInfo?
}
