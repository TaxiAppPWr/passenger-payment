package taxiapp.passengerpayment.controller;

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.beans.factory.annotation.Autowired
import taxiapp.passengerpayment.dto.event.GeneratePaymentEvent
import taxiapp.passengerpayment.service.PassengerPaymentService

@RestController
@RequestMapping("api/passengerPayment")
class PassengerPaymentController @Autowired constructor(
    private val driversService: PassengerPaymentService
){    
    @GetMapping("/paymentLink")
    fun getPaymentLink(@RequestParam rideId: Long): ResponseEntity<Any> {
        return ResponseEntity.ok(driversService.getPaymentLink(rideId))
    }
}
