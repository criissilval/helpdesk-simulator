package projetohelpdesk.demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TicketRequest {

    @NotBlank
    private String customerId;

    @NotBlank
    private String deviceId;

    @NotBlank
    private String serialNumber;

    @NotBlank
    private String reason;
}
