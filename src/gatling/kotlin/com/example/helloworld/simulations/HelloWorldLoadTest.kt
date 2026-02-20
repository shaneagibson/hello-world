package com.example.helloworld.simulations

import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.forAll
import io.gatling.javaapi.core.CoreDsl.global
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.core.CoreDsl.rampUsersPerSec
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import java.time.Duration

class HelloWorldLoadTest : Simulation() {
    // HTTP protocol configuration
    private val httpProtocol =
        http
            .baseUrl(System.getProperty("baseUrl", "http://localhost:8080"))
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling Load Test")

    // Scenario definition
    private val helloWorldScenario =
        scenario("Hello World Load Test")
            .exec(
                http("GET /hello/world")
                    .get("/hello/world")
                    .check(status().`is`(200))
                    .check(jsonPath("$.message").`is`("Hello, World!")),
            ).pause(Duration.ofMillis(100), Duration.ofMillis(500))

    private val healthCheckScenario =
        scenario("Health Check Load Test")
            .exec(
                http("GET /actuator/health")
                    .get("/actuator/health")
                    .check(status().`is`(200)),
            ).pause(Duration.ofMillis(100))

    init {
        // Load test configuration
        // Ramp up to 100 users over 30 seconds, sustain for 1 minute
        setUp(
            helloWorldScenario
                .injectOpen(
                    rampUsersPerSec(0.0).to(10.0).during(Duration.ofSeconds(30)),
                    constantUsersPerSec(10.0).during(Duration.ofSeconds(60)),
                ).protocols(httpProtocol),
            healthCheckScenario
                .injectOpen(
                    constantUsersPerSec(2.0).during(Duration.ofSeconds(90)),
                ).protocols(httpProtocol),
        ).assertions(
            global().responseTime().percentile(95.0).lt(100),
            global().successfulRequests().percent().gt(99.0),
            forAll().failedRequests().count().`is`(0L),
        )
    }
}
