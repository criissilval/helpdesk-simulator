package projetohelpdesk.demo.exception;

public class NoCounterAvailableException extends RuntimeException {

    public NoCounterAvailableException() {
        super("No counter available. All counters have reached the maximum number of active tickets.");
    }

    public NoCounterAvailableException(String message) {
        super(message);
    }
}
