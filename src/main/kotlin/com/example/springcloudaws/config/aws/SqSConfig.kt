package com.example.springcloudaws.config.aws

import com.example.springcloudaws.message.publisher.SqsMessagePublisher
import com.example.springcloudaws.message.subscriber.SqsMessageSubscriber
import com.fasterxml.jackson.databind.ObjectMapper
import io.awspring.cloud.sqs.config.EndpointRegistrar
import io.awspring.cloud.sqs.config.SqsListenerConfigurer
import io.awspring.cloud.sqs.config.SqsMessageListenerContainerFactory
import io.awspring.cloud.sqs.operations.SqsTemplate
import io.awspring.cloud.sqs.support.converter.SqsMessagingMessageConverter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.messaging.converter.MappingJackson2MessageConverter
import org.springframework.messaging.handler.annotation.support.PayloadMethodArgumentResolver
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.sqs.SqsAsyncClient
import java.net.URI


/**
 * https://howtodoinjava.com/spring-cloud/aws-sqs-with-spring-cloud-aws/
 */
@Configuration
class SqSConfig(
    private val awsConfigProperties: AwsConfigProperties,
    private val objectMapper: ObjectMapper,
) {

    @Bean
    fun sqsMessageSubscriber() = SqsMessageSubscriber()

    @Bean
    fun sqsMessagePublisher() = SqsMessagePublisher(awsConfigProperties.aws.sqs.subscriptions.newUserRegistered,sqsTemplate())

    @Bean
    @Primary
    fun sqsAsyncClient() = awsConfigProperties.toSqsAsyncClient()


    @Bean
    @Primary
    fun sqsTemplate(): SqsTemplate {
        //To ensure that our jackson object mapper is used in the message conversion and not the
        val converter: SqsMessagingMessageConverter = object : SqsMessagingMessageConverter() {}
        converter.setObjectMapper(this.objectMapper)


        return SqsTemplate.builder()
            .sqsAsyncClient(sqsAsyncClient())
            .messageConverter(converter)
            .configure { it.additionalHeaderForReceive("author.id","1231312321") }
            .build()
    }

    @Bean
    @Primary
    fun mappingJackson2MessageConverter() : MappingJackson2MessageConverter{
        val messageConverter =  MappingJackson2MessageConverter()
        messageConverter.isStrictContentTypeMatch = false
        messageConverter.objectMapper = this.objectMapper
        return messageConverter;
    }
    @Bean
    fun sqsListenerConfigurer(): SqsListenerConfigurer {

        return SqsListenerConfigurer {
            registrar: EndpointRegistrar ->
            registrar.objectMapper = this.objectMapper
            registrar.methodArgumentResolversConsumer.accept(listOf(
                PayloadMethodArgumentResolver(
                    mappingJackson2MessageConverter()
                )
            ))
        }
    }
    /**
     * There seems to be an issue with the Sqs autoconiguration that does not inject our specific
     * jackson object mapper thus we are unable to deserialize the object in the sqs listener.
     *
     * See further info in https://github.com/awspring/spring-cloud-aws/discussions/697
     */
    @Bean
    fun defaultSqsListenerContainerFactory(): SqsMessageListenerContainerFactory<String> {
        val converter = SqsMessagingMessageConverter()
        converter.payloadMessageConverter = mappingJackson2MessageConverter()
        converter.setPayloadTypeHeader(APPLICATION_JSON_VALUE)


        val factory = SqsMessageListenerContainerFactory.builder<String>()
            .configure {
                it.messageConverter(converter)
                //it.acknowledgementMode(AcknowledgementMode.MANUAL)

            }
            .sqsAsyncClient(sqsAsyncClient())
            .build()
        return factory
    }



    private fun AwsConfigProperties.toSqsAsyncClient(): SqsAsyncClient {
        return if(this.isAccessKeyEmpty()) {
            SqsAsyncClient
                .builder()
                .region(this.toRegion())
                .endpointOverride(this.toEndpointUri())
                .build()
        }else {
            SqsAsyncClient
                .builder()
                .region(this.toRegion())
                .endpointOverride(this.toEndpointUri())
                .credentialsProvider(
                    StaticCredentialsProvider
                        .create(this.toAwsBasicCredentials())
                )
                .build()
        }

    }

    private fun AwsConfigProperties.isAccessKeyEmpty() = this.aws.accessKey.isEmpty()

    private fun AwsConfigProperties.toEndpointUri() = URI(this.aws.sqs.endpoint)

    private fun AwsConfigProperties.toRegion() = Region.of(this.aws.region)

    private fun AwsConfigProperties.toAwsBasicCredentials() = AwsBasicCredentials.create(this.aws.accessKey, this.aws.secretAccessKey)
}