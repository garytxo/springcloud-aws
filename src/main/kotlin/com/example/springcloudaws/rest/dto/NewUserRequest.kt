package com.example.springcloudaws.rest.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDate

@Schema
class NewUserRequest(
    @field:Schema(name = "firstName", required = true, example = "Joe")
    val firstName:String,
    @field:Schema(name = "lastName", required = true, example = "Blog")
    val lastName:String,
    @field:Schema(name = "email", required = true, example = "joe.blog@gmail.com")
    val email:String,
    @field:Schema(name = "dob", required = true, example = "23/01/1990")
    val dob: LocalDate
)