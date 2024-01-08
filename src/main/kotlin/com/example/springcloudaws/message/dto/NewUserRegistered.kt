package com.example.springcloudaws.message.dto

import java.time.LocalDate

data class NewUserRegistered(
    val firstName:String,
    val lastName:String,
    val email:String,
    val dob:LocalDate
)
