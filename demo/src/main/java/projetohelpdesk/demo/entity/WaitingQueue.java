package projetohelpdesk.demo.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "waiting_queue")
@Data
@NoArgsConstructor
public class WaitingQueue {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)  
    private String customerId;

    @Column(nullable = false)  
    private String deviceId;

    @Column(nullable = false)  
    private String serialNumber;

    @Column(nullable = false)  
    private String reason;

    @Column(nullable = false)  
    private LocalDateTime createdAt = LocalDateTime.now();
}
