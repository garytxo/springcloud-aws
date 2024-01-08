package com.example.springcloudaws.config.aws
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(AwsConfigProperties::class)
class AWSClientConfigLoader

@ConfigurationProperties(prefix = "customized")
data class AwsConfigProperties(
    val aws: AwsClientProperties,
)

data class AwsClientProperties(
    val prefix: String,
    val region: String,
    val account: String,
    val accessKey: String,
    val secretAccessKey: String,
    val sqs: SQSProperties,
    val s3: S3Properties
)

data class SQSProperties(
    val endpoint: String,
    val subscriptions: SqsSubscriptionsProperties,
    val publishers: SqsPublishersProperties
)


data class SqsSubscriptionsProperties(
    val newUserRegistered:String,
    val newS3File:String
)

data class SqsPublishersProperties(
    val newUserRegistered:String
)

data class S3Properties(
    val useBackup: Boolean,
    val endpoint: String,
    val buckets: S3BucketProperties
)

data class S3BucketProperties(
    val newFileUploaded: String,
)
