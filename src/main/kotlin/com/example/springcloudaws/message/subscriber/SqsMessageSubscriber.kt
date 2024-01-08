package com.example.springcloudaws.message.subscriber

import com.example.springcloudaws.message.dto.NewUserRegistered
import io.awspring.cloud.sqs.annotation.SqsListener

import org.slf4j.LoggerFactory
import org.springframework.messaging.Message

class SqsMessageSubscriber {

    private val logger = LoggerFactory.getLogger(SqsMessageSubscriber::class.java)

    @SqsListener( value = ["\${customized.aws.sqs.subscriptions.new-user-registered}"])
    fun receiveMessage(
        message: Message<NewUserRegistered>,
    ) {
        logger.info("Message received with id:${message.headers.id}  payload:${message.payload}")

    }
}