package com.example.helloworld.model

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Hello World response")
data class HelloResponse(
    @field:Schema(description = "Greeting message", example = "Hello, World!")
    val message: String,
)
