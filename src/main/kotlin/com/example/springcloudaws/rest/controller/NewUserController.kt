package com.example.springcloudaws.rest.controller

import com.example.springcloudaws.message.dto.NewUserRegistered
import com.example.springcloudaws.message.publisher.SqsMessagePublisher
import com.example.springcloudaws.rest.dto.NewUserRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(
    description = "Test REST API to create new user",
    name = "NewUserRestController",
)
@RestController
class NewUserController(
    private val sqsMessagePublisher: SqsMessagePublisher
) {

    private val logger = LoggerFactory.getLogger(NewUserController::class.java)

    @PostMapping( path = ["users"])
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Create new user")
    @ApiResponses(
        value = [ApiResponse(responseCode = "201", description = "User created")]
    )
    fun create(
        @RequestBody newUserRequest: NewUserRequest
    ){

        logger.info("Creating new user with request:${newUserRequest}")
        sqsMessagePublisher.send(newUserRequest.toMessage())
    }

    private fun NewUserRequest.toMessage() =
      NewUserRegistered(
          firstName = this.firstName,
          lastName = this.lastName,
          email = this.email,
          dob = this.dob
      )
}