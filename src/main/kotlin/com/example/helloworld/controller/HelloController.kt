package com.example.helloworld.controller

import com.example.helloworld.model.HelloResponse
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HelloController {
    private val logger = LoggerFactory.getLogger(HelloController::class.java)

    @GetMapping("/hello/world")
    fun helloWorld(): HelloResponse {
        logger.info("Received request for hello world endpoint")
        logger.debug("Generating hello world response")
        return HelloResponse("Hello, World!")
    }
}
