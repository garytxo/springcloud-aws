package com.example.springcloudaws.rest.controller

import com.example.springcloudaws.rest.dto.NewUserRequest
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.PostMapping

import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.client.RestTemplate
import java.time.LocalDate

@Tag(
    description = "Call another endpoint and the trace and customized id should be propated",
    name = "CallTheNewUserController",
)
@RestController
class CallTheNewUserController(
    private val restTemplate: RestTemplate
) {

    private val logger = LoggerFactory.getLogger(CallTheNewUserController::class.java)

    @PostMapping( path = ["test-create-user"])
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(description = "Test call create new user")
    @ApiResponses(
        value = [ApiResponse(responseCode = "201", description = "User created")]
    )
    fun call(){

        logger.info("Calling create user other endpoint ")
        val result = restTemplate.postForEntity("http://localhost:8001/api-springcloud-aws/users",asNewUserRequest(),NewUserRequest::class.java)

        if(result.statusCode.is2xxSuccessful){
            logger.info("Successfully called endpoint headers:${result.headers} body:${result.body}")
        }else{
            logger.warn("Issue calling endpoint code:${result.statusCode} ")
        }
    }

    private fun asNewUserRequest() =
        NewUserRequest(
          firstName = "First Name",
          lastName = "Last Name",
          email = "test@gamil.com",
          dob = LocalDate.now()
      )
}