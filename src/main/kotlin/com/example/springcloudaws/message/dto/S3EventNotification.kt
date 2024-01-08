package com.example.springcloudaws.message.dto

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * There is an ongoing discussion that is opened almost 5 years regarding
 * migrating the SQS S3EventNotification message https://github.com/aws/aws-sdk-java-v2/issues/1197
 * Therefore to avoid adding AWS SDK version 1 dependencies it made more sense to extract the class  and
 * generate our own version https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/event/S3EventNotification.html
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
data class S3EventNotification(
    @JsonProperty(value = "Records")
    val records: List<S3EventNotificationRecord>? = emptyList()
)

data class S3EventNotificationRecord(
    @JsonProperty(value = "eventVersion") val eventVersion: String,
    @JsonProperty(value = "eventSource") val eventSource: String,
    @JsonProperty(value = "awsRegion") val awsRegion: String,
    @JsonProperty(value = "eventTime") val eventTime: String,
    @JsonProperty(value = "eventName") val eventName: String,
    @JsonProperty(value = "userIdentity") val userIdentity: UserIdentity?,
    @JsonProperty(value = "requestParameters") val requestParameters: RequestParameters?,
    @JsonProperty(value = "responseElements") val responseElements: ResponseElements?,
    @JsonProperty(value = "s3") val s3: S3Entity,

    @JsonProperty(value = "glacierEventData") val glacierEventData: String?,
    @JsonProperty(value = "lifecycleEventData") val lifecycleEventData: String?,
    @JsonProperty(value = "intelligentTieringEventData") val intelligentTieringEventData: String?,
    @JsonProperty(value = "replicationEventData") val replicationEventData: String?
)

data class UserIdentity(
    val principalId: String
)

data class RequestParameters(
    val sourceIPAddress: String
)

data class ResponseElements(
    @JsonProperty(value = "x-amz-id-2") val xAmzRequestId: String,
    @JsonProperty(value = "x-amz-request-id") val xAmzId2: String
)

data class S3Entity(
    @JsonProperty(value = "s3SchemaVersion") val s3SchemaVersion: String,
    @JsonProperty(value = "configurationId") val configurationId: String,
    @JsonProperty(value = "bucket") val s3Bucket: S3BucketEntity?,
    @JsonProperty(value = "object") val s3Object: S3ObjectEntity
)

data class S3BucketEntity(
    @JsonProperty(value = "name") val name: String,
    @JsonProperty(value = "ownerIdentity") val ownerIdentity: OwnerIdentity,
    @JsonProperty(value = "arn") val arn: String
)

data class OwnerIdentity(
    val principalId: String
)

data class S3ObjectEntity(
    @JsonProperty(value = "key") val key: String,
    @JsonProperty(value = "sequencer") val sequencer: String,
    @JsonProperty(value = "size") val size: Int,
    @JsonProperty(value = "eTag") val eTag: String
)