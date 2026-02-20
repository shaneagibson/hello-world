package com.example.helloworld.controller

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(HelloController::class)
class HelloControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Test
    fun `helloWorld should return Hello World message`() {
        mockMvc
            .get("/hello/world")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.message") { value("Hello, World!") }
            }
    }

    @Test
    fun `helloWorld should return 200 status`() {
        mockMvc
            .get("/hello/world")
            .andExpect {
                status { isOk() }
            }
    }
}
