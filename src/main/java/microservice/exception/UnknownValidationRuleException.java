package microservice.exception;

public class UnknownValidationRuleException extends RuntimeException {

    public UnknownValidationRuleException(final String message) {
        super(message);
    }

}