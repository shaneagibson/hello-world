package com.example.helloworld.contract

import com.atlassian.oai.validator.restassured.OpenApiValidationFilter
import io.restassured.builder.RequestSpecBuilder
import io.restassured.module.kotlin.extensions.Given
import io.restassured.module.kotlin.extensions.Then
import io.restassured.module.kotlin.extensions.When
import io.restassured.specification.RequestSpecification
import org.hamcrest.Matchers.equalTo
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ApiContractTest {
    @LocalServerPort
    private var port: Int = 0

    private lateinit var requestSpec: RequestSpecification
    private lateinit var validationFilter: OpenApiValidationFilter

    @BeforeEach
    fun setup() {
        requestSpec =
            RequestSpecBuilder()
                .setBaseUri("http://localhost")
                .setPort(port)
                .build()

        validationFilter =
            OpenApiValidationFilter(
                "http://localhost:$port/api-docs",
            )
    }

    @Test
    fun `GET hello world endpoint should comply with OpenAPI specification`() {
        Given {
            spec(requestSpec)
            filter(validationFilter)
        } When {
            get("/hello/world")
        } Then {
            statusCode(200)
            contentType("application/json")
            body("message", equalTo("Hello, World!"))
        }
    }

    @Test
    fun `API should return valid JSON matching OpenAPI schema`() {
        Given {
            spec(requestSpec)
            filter(validationFilter)
        } When {
            get("/hello/world")
        } Then {
            statusCode(200)
        }
    }

    @Test
    fun `API should have correct content type as per OpenAPI spec`() {
        Given {
            spec(requestSpec)
            filter(validationFilter)
            accept("application/json")
        } When {
            get("/hello/world")
        } Then {
            statusCode(200)
            contentType("application/json")
        }
    }
}
