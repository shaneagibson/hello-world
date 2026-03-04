package com.example.helloworld.controller

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.BDDMockito.given
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@WebMvcTest(VersionController::class)
class VersionControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var buildProperties: BuildProperties

    @BeforeEach
    fun setup() {
        given(buildProperties.version).willReturn("1.0.abc1234")
    }

    @Test
    fun `version should return application version`() {
        mockMvc
            .get("/version")
            .andExpect {
                status { isOk() }
                content { contentType("application/json") }
                jsonPath("$.version") { value("1.0.abc1234") }
            }
    }

    @Test
    fun `version should return 200 status`() {
        mockMvc
            .get("/version")
            .andExpect {
                status { isOk() }
            }
    }
}
