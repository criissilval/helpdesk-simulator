# Task 08 — Fila de Espera (Overflow de Balcões)

## Objetivo
Quando todos os balcões estiverem com 5 chamados na fila, o novo chamado não é salvo diretamente — ele vai para uma fila de espera. A cada 3 minutos, essa fila é processada e, se houver vaga, o chamado é criado normalmente.

## Migration SQL
Crie `src/main/resources/db/migration/V3__create_waiting_queue_table.sql`:

```sql
CREATE TABLE waiting_queue (
    id              BIGSERIAL PRIMARY KEY,
    customer_id     VARCHAR(100) NOT NULL,
    device_id       VARCHAR(100) NOT NULL,
    serial_number   VARCHAR(100) NOT NULL,
    reason          TEXT NOT NULL,
    created_at      TIMESTAMP NOT NULL DEFAULT NOW()
);
```

## Entidade
Crie em `src/main/java/.../entity/WaitingQueue.java`:

```java
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
```

## Repository
Crie em `src/main/java/.../repository/WaitingQueueRepository.java`:

```java
@Repository
public interface WaitingQueueRepository extends JpaRepository<WaitingQueue, Long> {
    List<WaitingQueue> findAllByOrderByCreatedAtAsc();
}
```

## Atualizando o TicketService
Substitua o lançamento de exceção pela lógica de fila de espera:

```java
@Transactional
public Ticket create(TicketRequest request) {
    Optional<Counter> availableCounter = findAvailableCounter();

    if (availableCounter.isEmpty()) {
        // Salva na fila de espera em vez de lançar erro
        WaitingQueue waiting = new WaitingQueue();
        waiting.setCustomerId(request.getCustomerId());
        waiting.setDeviceId(request.getDeviceId());
        waiting.setSerialNumber(request.getSerialNumber());
        waiting.setReason(request.getReason());
        waitingQueueRepository.save(waiting);

        throw new ResponseStatusException(HttpStatus.ACCEPTED,
            "Sem balcao disponivel. Chamado adicionado a fila de espera.");
    }

    Ticket ticket = new Ticket();
    ticket.setCustomerId(request.getCustomerId());
    ticket.setDeviceId(request.getDeviceId());
    ticket.setSerialNumber(request.getSerialNumber());
    ticket.setReason(request.getReason());
    ticket.setStatus(TicketStatus.ABERTO);
    ticket.setCounter(availableCounter.get());

    return ticketRepository.save(ticket);
}
```

Lembre de injetar o `WaitingQueueRepository` no `TicketService`.

## Job da Fila de Espera
Crie em `src/main/java/.../job/WaitingQueueJob.java`:

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class WaitingQueueJob {

    private final WaitingQueueRepository waitingQueueRepository;
    private final TicketService ticketService;

    // Executa a cada 3 minutos (180.000 milissegundos)
    @Scheduled(fixedDelay = 180_000)
    @Transactional
    public void processWaitingQueue() {
        List<WaitingQueue> waiting = waitingQueueRepository.findAllByOrderByCreatedAtAsc();

        for (WaitingQueue item : waiting) {
            try {
                TicketRequest request = new TicketRequest();
                request.setCustomerId(item.getCustomerId());
                request.setDeviceId(item.getDeviceId());
                request.setSerialNumber(item.getSerialNumber());
                request.setReason(item.getReason());

                ticketService.create(request);
                waitingQueueRepository.delete(item);
                log.info("Item da fila de espera processado: {}", item.getId());
            } catch (ResponseStatusException e) {
                log.info("Ainda sem balcao disponivel. Item {} permanece na fila.", item.getId());
                break; // Para e tenta novamente no próximo ciclo
            }
        }
    }
}
```

## Criterio de aceite
- [ ] Com todos os balcões cheios, `POST /tickets` retorna 202 e salva na fila de espera
- [ ] Tabela `waiting_queue` contém o registro pendente
- [ ] Após 3 minutos, o Job processa a fila e cria o chamado se houver vaga
- [ ] Após processado com sucesso, o item é removido da fila de espera
