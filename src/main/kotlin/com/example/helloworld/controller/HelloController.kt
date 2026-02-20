package com.example.helloworld.controller

import com.example.helloworld.model.HelloResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Hello", description = "Hello World API endpoints")
class HelloController {
    private val logger = LoggerFactory.getLogger(HelloController::class.java)

    @Operation(
        summary = "Get Hello World message",
        description = "Returns a simple greeting message",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "200",
                description = "Successful operation",
                content = [
                    Content(
                        mediaType = "application/json",
                        schema = Schema(implementation = HelloResponse::class),
                    ),
                ],
            ),
        ],
    )
    @GetMapping("/hello/world")
    fun helloWorld(): HelloResponse {
        logger.info("Received request for hello world endpoint")
        logger.debug("Generating hello world response")
        return HelloResponse("Hello, World!")
    }
}
