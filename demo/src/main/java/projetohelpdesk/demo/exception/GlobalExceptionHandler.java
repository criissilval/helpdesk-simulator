package projetohelpdesk.demo.exception;

  import jakarta.persistence.EntityNotFoundException;
  import org.springframework.http.HttpStatus;
  import org.springframework.web.bind.annotation.ExceptionHandler;                                                                                                 
  import org.springframework.web.bind.annotation.ResponseStatus;
  import org.springframework.web.bind.annotation.RestControllerAdvice;                                                                                             
                                                                                                                                                                   
  import java.util.Map;
@RestControllerAdvice 
public class GlobalExceptionHandler {

      @ExceptionHandler(NoCounterAvailableException.class)                                                                                                         
      @ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
      public Map<String, String> handleNoCounterAvailable(NoCounterAvailableException ex) {                                                                        
          return Map.of("error", ex.getMessage());
      }

      @ExceptionHandler(EntityNotFoundException.class)                                                                                                             
      @ResponseStatus(HttpStatus.NOT_FOUND)
      public Map<String, String> handleEntityNotFound(EntityNotFoundException ex) {                                                                                
          return Map.of("error", ex.getMessage());
      }
    
}
