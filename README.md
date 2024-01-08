# Spring Boot 3.0.1 and Spring Cloud AWS integration

Whilst migrating a project from Spring Boot 2.X to 3.0.1 there were some issues with spring boot 3.X with spring cloud aws.

As a result I created this repo illustrating how to configure a spring boot 3.0.1 application with spring cloud AWS 3.1.0 which
requires some additional setup for configuration SQS messages and S3 with the latest AWS SDK that come bundled with spring cloud aws.  



## Spring Cloud AWS compatibility with Spring Boot
There has been some changes since Spring Boot 2.* version which we should ensure we have the correct compatibility 
with the spring boot version https://github.com/awspring/spring-cloud-aws#compatibility-with-spring-project-versions

See how in Spring Cloud AWS how to define the sqs etc.
https://docs.awspring.io/spring-cloud-aws/docs/3.1.0/reference/html/index.html#sqs-integration 

## AWS SDK Version 2
Spring Cloud AWS now bundles up AWS SDK version 2 which has different package naming convention
from `com.amazonaws.*` to now `software.amazon.*` and similarly the maven dependency packages have changes too.


## S3EventNotification 
There is an ongoing [discussion](https://github.com/aws/aws-sdk-java-v2/issues/1197) that is opened almost 5 years regarding migrating the SQS S3EventNotification message from SDK version 1.
After reading the thread for this project it made more sense to extract the [class(https://docs.aws.amazon.com/AWSJavaSDK/latest/javadoc/com/amazonaws/services/s3/event/S3EventNotification.html)]  and
generate our own version  this we avoid adding AWS SDK version 1 dependencies


## AWS S3 Transfer Manager 
Adding the following dependencies should automatically define the AWS file transfer utility
```
<dependency>
  <groupId>software.amazon.awssdk</groupId>
  <artifactId>s3-transfer-manager</artifactId>
</dependency>

<dependency>
  <groupId>software.amazon.awssdk.crt</groupId>
  <artifactId>aws-crt</artifactId>
</dependency>
```
See further info [here](https://docs.awspring.io/spring-cloud-aws/docs/3.1.0/reference/html/index.html#using-s3transfermanager-and-crt-based-s3-client)

Alternatively you can instantiate your own `S3AsyncClient` and `S3TransferManager` as done in the `S3Config.kt`
```kotlin
  @Bean
  fun s3Client() =
    S3AsyncClient.crtBuilder()
      .region(Region.of("eu-west-1"))
      .endpointOverride(URI("http://localhost:4516"))
      .build()
  
  @Bean
  fun s3TransferManager(): S3TransferManager =
    S3TransferManager.builder()
      .s3Client(s3Client())
      .build()
```

## Create application SQS queue and S3 bucket
Execute the following commands to create the SQS queue and S3 bucket for this test application

### SQS 
Create basic SQS queue for new user registered evene 
```shell
  aws --endpoint-url=http://localhost:4516 sqs create-queue --queue-name new-user-registered-event
```

Create a S3 specific SQS queue which is trigger when a new file is uploaded to bucket
```shell
  aws --endpoint-url=http://localhost:4516 sqs create-queue --queue-name test-s3-bucket-event
```

Create S3 Bucket
```shell
 aws --endpoint-url=http://localhost:4516 s3 mb "s3://test-s3-bucket"
```

Obtain the ARN of the test-s3-bucket-event queue and set bucket notification configuration
```shell
UPLOAD_S3_QUEUE_ARN=$(aws --endpoint-url=http://localhost:4516 sqs get-queue-attributes --queue-url http://localhost:4516/000000000000/test-s3-bucket-event --attribute-names QueueArn | jq -r '.Attributes.QueueArn')
aws --endpoint-url=http://localhost:4516 s3api put-bucket-notification-configuration --bucket test-s3-bucket --notification-configuration "{\"QueueConfigurations\":[{\"QueueArn\":\"$UPLOAD_S3_QUEUE_ARN\",\"Events\":[\"s3:ObjectCreated:*\"]}]}"
```



## Swagger
Access the REST endpoint that trigger publishing a message to a SQS queue
http://localhost:8001/api-springcloud-aws/swagger-ui/index.html#/

## Upload file to S3 
Example of how to upload a test file to the S3 bucket 'test-s3-bucket' which then trigger the S3NewFileMessageSubscriber

```shell

aws --endpoint-url=http://localhost:4516 s3 cp "samples/HELLO.txt" "s3://test-s3-bucket/HELLO.txt"

```


## Things to consider in further iterations
1. It seems that current spring cloud aws [does support distributed tracing](https://github.com/awspring/spring-cloud-aws/discussions/902#discussioncomment-7188170) 




### Reference Documentation

For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/3.2.1/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/3.2.1/maven-plugin/reference/html/#build-image)
* [Spring Web](https://docs.spring.io/spring-boot/docs/3.2.1/reference/htmlsingle/index.html#web)



## Useful resources

The following are some useful resources that we used during the migration:

* [AWS sdk configuration](https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/index.html#amazon-sdk-configuration)
* [AWS Spring Cloud and Spring Boot compatibility](https://github.com/awspring/spring-cloud-aws#compatibility-with-spring-project-versions)
* [AWS Spring Cloud Documentation](https://docs.awspring.io/spring-cloud-aws/docs/current/reference/html/appendix.html)
* [AWS Spring Cloud Code Example](https://github.com/thombergs/code-examples/tree/master/aws/springcloudsqs)
* [AWS S3 Transfer Manger](https://aws.amazon.com/blogs/developer/introducing-crt-based-s3-client-and-the-s3-transfer-manager-in-the-aws-sdk-for-java-2-x/)
* [How to setup aws sqs and spring could demo](https://howtodoinjava.com/spring-cloud/aws-sqs-with-spring-cloud-aws/)

