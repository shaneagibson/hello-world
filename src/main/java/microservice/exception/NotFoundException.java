package microservice.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException() {
        super("Not Found");
    }

}
