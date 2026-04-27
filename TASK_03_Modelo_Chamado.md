# Task 03 — Modelo de Dados: Chamado

## Objetivo
Criar a tabela de chamados com todos os campos do requisito, o enum de status e a entidade Java.

## Migration SQL
Crie o arquivo `src/main/resources/db/migration/V2__create_tickets_table.sql`:

```sql
CREATE TYPE ticket_status AS ENUM ('ABERTO', 'EM_ESPERA', 'EM_ATENDIMENTO', 'CONCLUIDO');

CREATE TABLE tickets (
    id              BIGSERIAL PRIMARY KEY,
    customer_id     VARCHAR(100) NOT NULL,
    device_id       VARCHAR(100) NOT NULL,
    serial_number   VARCHAR(100) NOT NULL,
    reason          TEXT NOT NULL,
    status          ticket_status NOT NULL DEFAULT 'ABERTO',
    counter_id      BIGINT REFERENCES counters(id),
    created_at      TIMESTAMP NOT NULL DEFAULT NOW(),
    resolved_at     TIMESTAMP
);
```

## Enum de Status
Crie em `src/main/java/.../enums/TicketStatus.java`:

```java
public enum TicketStatus {
    ABERTO,
    EM_ESPERA,
    EM_ATENDIMENTO,
    CONCLUIDO
}
```

## Entidade Java
Crie em `src/main/java/.../entity/Ticket.java`:

```java
@Entity
@Table(name = "tickets")
@Data
@NoArgsConstructor
public class Ticket {

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

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TicketStatus status = TicketStatus.ABERTO;

    @ManyToOne
    @JoinColumn(name = "counter_id")
    private Counter counter;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    private LocalDateTime resolvedAt;
}
```

## Repository
Arquivo: `src/main/java/projetohelpdesk/demo/repository/TicketRepository.java`

```java
@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Já implementado
    Page<Ticket> findByCustomerId(String customerId, Pageable pageable);
    long countByServiceDeskIdAndStatusNot(Long serviceDeskId, TicketStatus status);

    // Adicionar na Task 09 (conflitos)
    Optional<Ticket> findBySerialNumberAndStatusNot(String serial, TicketStatus status);

    // Adicionar na Task 07 (job de processamento)
    List<Ticket> findByStatus(TicketStatus status);
}
```

> Os dois últimos métodos ainda não estão no arquivo — serão adicionados nas Tasks 07 e 09.

## Criterio de aceite
- [x] Aplicação sobe sem erro
- [x] Tabela `tickets` criada no banco automaticamente pelo Hibernate
- [x] Enum TicketStatus criado com os 4 status
