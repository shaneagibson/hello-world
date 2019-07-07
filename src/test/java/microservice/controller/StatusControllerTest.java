package microservice.controller;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.assertEquals;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class StatusControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void shouldReceiveVersionFromStatusEndpoint() {

        // arrange
        System.setProperty("app.version", "1.0.abc");

        // act
        final ResponseEntity<String> response = restTemplate.getForEntity("/status", String.class);

        // assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("{\"version\":\"1.0.abc\",\"status\":\"OK\"}", response.getBody());
    }

    @Test
    public void shouldReceivePingEndpoint() {
        final ResponseEntity<String> response = restTemplate.getForEntity("/ping", String.class);
        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
    }

}