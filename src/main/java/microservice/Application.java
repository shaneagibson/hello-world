package microservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"microservice.controller"})
public class Application {

    public static void main(final String... args) {
        SpringApplication.run(Application.class, args);
    }

}