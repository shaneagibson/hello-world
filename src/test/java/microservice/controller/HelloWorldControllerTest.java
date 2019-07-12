package microservice.controller;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;
import static org.springframework.http.HttpStatus.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
class HelloWorldControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void shouldSayHelloWorld() {
        final ResponseEntity<String> response = restTemplate.getForEntity("/message", String.class);
        assertEquals(OK, response.getStatusCode());
        assertEquals("{\"text\":\"Hello, World!!!\"}", response.getBody());

    }

}