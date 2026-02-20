package com.example.helloworld.simulations

import io.gatling.javaapi.core.CoreDsl.constantUsersPerSec
import io.gatling.javaapi.core.CoreDsl.global
import io.gatling.javaapi.core.CoreDsl.jsonPath
import io.gatling.javaapi.core.CoreDsl.rampUsersPerSec
import io.gatling.javaapi.core.CoreDsl.scenario
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import java.time.Duration

class HelloWorldStressTest : Simulation() {
    // HTTP protocol configuration
    private val httpProtocol =
        http
            .baseUrl(System.getProperty("baseUrl", "http://localhost:8080"))
            .acceptHeader("application/json")
            .contentTypeHeader("application/json")
            .userAgentHeader("Gatling Stress Test")

    // Scenario definition
    private val stressScenario =
        scenario("Hello World Stress Test")
            .exec(
                http("GET /hello/world")
                    .get("/hello/world")
                    .check(status().`is`(200))
                    .check(jsonPath("$.message").exists()),
            )

    init {
        // Stress test configuration
        // Gradually increase load to find breaking point
        // Ramp up to 500 concurrent users over 2 minutes
        setUp(
            stressScenario.injectOpen(
                rampUsersPerSec(0.0).to(50.0).during(Duration.ofSeconds(60)),
                constantUsersPerSec(50.0).during(Duration.ofSeconds(60)),
                rampUsersPerSec(50.0).to(100.0).during(Duration.ofSeconds(60)),
            ).protocols(httpProtocol),
        ).assertions(
            global().responseTime().percentile(99.0).lt(500),
            global().successfulRequests().percent().gt(95.0),
        ).maxDuration(Duration.ofMinutes(5))
    }
}
