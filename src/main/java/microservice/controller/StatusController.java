package microservice.controller;

import microservice.model.Status;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RestController
public class StatusController extends AbstractController {

    public StatusController() {
        super(LoggerFactory.getLogger(AbstractController.class));
    }

    @RequestMapping(path = "/status", method = GET, produces = APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public Status status() {
        return Status.ok(System.getProperty("app.version"));
    }

    @RequestMapping(path = "/ping", method = GET)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void ping() {
    }
}