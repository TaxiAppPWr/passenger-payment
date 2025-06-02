package taxiapp.passengerpayment

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class DriversApplication

fun main(args: Array<String>) {
	runApplication<DriversApplication>(*args)
}