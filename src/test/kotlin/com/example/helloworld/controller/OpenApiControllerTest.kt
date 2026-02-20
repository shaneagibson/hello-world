package com.example.helloworld.controller

import org.hamcrest.Matchers.containsString
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(OpenApiController::class)
class OpenApiControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `apiDocs should return 200 with yaml content type`() {
        mockMvc
            .get("/api-docs")
            .andExpect {
                status { isOk() }
                header { string("Content-Type", containsString("text/yaml")) }
            }
    }

    @Test
    fun `apiDocs should return OpenAPI spec with expected content`() {
        mockMvc
            .get("/api-docs")
            .andExpect {
                status { isOk() }
                content { string(containsString("openapi:")) }
                content { string(containsString("/hello/world")) }
            }
    }
}
