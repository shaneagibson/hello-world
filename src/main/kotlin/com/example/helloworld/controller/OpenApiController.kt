package com.example.helloworld.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.core.io.Resource
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class OpenApiController(
    @Value("classpath:openapi.yaml") private val specResource: Resource,
) {
    private val spec: String by lazy { specResource.inputStream.bufferedReader().readText() }

    @GetMapping("/api-docs", produces = ["text/yaml"])
    fun apiDocs() = spec
}
