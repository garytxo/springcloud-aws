package com.example.springcloudaws.message.subscriber

import com.example.springcloudaws.message.dto.S3EventNotification
import io.awspring.cloud.sqs.annotation.SqsListener
import org.slf4j.LoggerFactory
import org.springframework.messaging.Message

class S3NewFileMessageSubscriber {

    private val logger = LoggerFactory.getLogger(S3NewFileMessageSubscriber::class.java)


    @SqsListener(
        value = ["\${customized.aws.sqs.subscriptions.new-s3-file}"]
    )
    fun received(s3EventNotification:Message<S3EventNotification>){

        logger.info("s3 Message received with $s3EventNotification")

    }

}

