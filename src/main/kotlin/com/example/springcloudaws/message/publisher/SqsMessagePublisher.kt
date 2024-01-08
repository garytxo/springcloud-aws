package com.example.springcloudaws.message.publisher

import com.example.springcloudaws.message.dto.NewUserRegistered
import io.awspring.cloud.sqs.operations.SqsTemplate
import org.slf4j.LoggerFactory
import org.springframework.messaging.support.MessageBuilder

class SqsMessagePublisher(
    private val queueName:String,
    private val sqsTemplate: SqsTemplate
) {

    private val logger = LoggerFactory.getLogger(SqsMessagePublisher::class.java)

    fun send(newUserRegistered: NewUserRegistered){
        logger.info("Sending new registration:$newUserRegistered to queue:$queueName")

        val message =  MessageBuilder.withPayload(newUserRegistered).build()

        val result = sqsTemplate.send(queueName,message)
        logger.info("Message sent $result")
    }
}