package com.example.springcloudaws.config.aws

import com.example.springcloudaws.message.subscriber.S3NewFileMessageSubscriber
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3AsyncClient
import software.amazon.awssdk.transfer.s3.S3TransferManager
import java.net.URI

@Configuration
class S3Config(private val awsConfigProperties: AwsConfigProperties,
        private val objectMapper: ObjectMapper) {


    @Bean
    fun s3NewFileSubscriber() = S3NewFileMessageSubscriber()


    @Bean
    fun s3AsyncClient() =
        awsConfigProperties.toS3Client()

    @Bean
    fun s3TransferManager(): S3TransferManager =
        S3TransferManager.builder()
            .s3Client(s3AsyncClient())
            .build()


    private fun AwsConfigProperties.toS3Client(): S3AsyncClient {

        return if(this.isAccessKeyEmpty()) {
            S3AsyncClient.crtBuilder()
                 .region(this.toRegion())
                 .endpointOverride(this.toEndpointUri())
                 .forcePathStyle(true)
                 .build()
        }else{
            S3AsyncClient.crtBuilder()
                .region(this.toRegion())
                .endpointOverride(this.toEndpointUri())
                .credentialsProvider(
                    StaticCredentialsProvider
                        .create(this.toAwsBasicCredentials())
                )
                .forcePathStyle(true)
                .build()
        }


    }

    private fun AwsConfigProperties.isAccessKeyEmpty() = this.aws.accessKey.isEmpty()

    private fun AwsConfigProperties.toEndpointUri() = URI(this.aws.sqs.endpoint)

    private fun AwsConfigProperties.toRegion() = Region.of(this.aws.region)

    private fun AwsConfigProperties.toAwsBasicCredentials() = AwsBasicCredentials.create(this.aws.accessKey, this.aws.secretAccessKey)

}