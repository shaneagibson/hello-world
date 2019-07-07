package microservice.controller;

import microservice.model.Message;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.web.bind.annotation.RequestMethod.*;

@RestController
public class HelloWorldController extends AbstractController {

    public HelloWorldController() {
        super(LoggerFactory.getLogger(HelloWorldController.class));
    }

    @RequestMapping(method = GET, path = "/hello-world", produces = APPLICATION_JSON_VALUE)
    @ResponseBody
    @ResponseStatus(OK)
    public Message helloWorld() {
        return new Message("Hello, World.");
    }

}