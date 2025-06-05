package taxiapp.passengerpayment.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.cognitoidentityprovider.CognitoIdentityProviderClient

@Configuration
class AwsConfig {
    @Value("\${aws.region}")
    private var awsRegion: String? = null

    @Bean
    fun cognitoClient(): CognitoIdentityProviderClient {
        return  CognitoIdentityProviderClient.builder()
            .region(Region.of(awsRegion))
            .build()
    }
}
