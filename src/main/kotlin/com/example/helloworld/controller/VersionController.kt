package com.example.helloworld.controller

import com.example.helloworld.model.VersionResponse
import org.springframework.boot.info.BuildProperties
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class VersionController(private val buildProperties: BuildProperties) {
    @GetMapping("/version")
    fun version() = VersionResponse(buildProperties.version)
}
