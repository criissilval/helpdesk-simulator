package projetohelpdesk.demo.dto;

import lombok.Builder;
import lombok.Data;
import projetohelpdesk.demo.enums.TicketStatus;
                                                                                                                                                                                                                     
import java.time.LocalDateTime;


@Data
@Builder
public class TicketDetailResponse {
    private Long id;
    private String reason;
    private String customerId;
    private String customerName;
    private String attendantName;
    private TicketStatus status;
    private LocalDateTime createdAt;

    private LocalDateTime resolvedAt;
    
}
