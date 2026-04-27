package projetohelpdesk.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CounterRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String attendant;
}
