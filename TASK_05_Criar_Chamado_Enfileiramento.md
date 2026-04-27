# Task 05 — Criar Chamado com Lógica de Enfileiramento

## Objetivo
Implementar a API de criação de chamado com a regra de enfileiramento: ao criar um chamado, o sistema deve escolher automaticamente um balcão que tenha disponibilidade (menos de 5 chamados não concluídos).

## Regra de negócio
- Cada balcão suporta até **5 chamados** com status diferente de CONCLUIDO
- O sistema escolhe o primeiro balcão disponível
- Se nenhum balcão tiver vaga, lança exceção (tratado na Task 08)

## DTO de Request
Crie em `src/main/java/.../dto/TicketRequest.java`:

```java
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
```

## Exception
Crie em `src/main/java/.../exception/NoCounterAvailableException.java`:

```java
public class NoCounterAvailableException extends RuntimeException {
    public NoCounterAvailableException(String message) {
        super(message);
    }
}
```

## Service
Crie em `src/main/java/.../service/TicketService.java`:

```java
@Service
@RequiredArgsConstructor
public class TicketService {

    private static final int MAX_QUEUE_SIZE = 5;

    private final TicketRepository ticketRepository;
    private final CounterRepository counterRepository;

    @Transactional
    public Ticket create(TicketRequest request) {
        Counter availableCounter = findAvailableCounter()
            .orElseThrow(() -> new NoCounterAvailableException("Todos os balcões estão cheios."));

        Ticket ticket = new Ticket();
        ticket.setCustomerId(request.getCustomerId());
        ticket.setDeviceId(request.getDeviceId());
        ticket.setSerialNumber(request.getSerialNumber());
        ticket.setReason(request.getReason());
        ticket.setStatus(TicketStatus.ABERTO);
        ticket.setCounter(availableCounter);

        return ticketRepository.save(ticket);
    }

    private Optional<Counter> findAvailableCounter() {
        return counterRepository.findAll().stream()
            .filter(counter -> ticketRepository
                .countByCounterIdAndStatusNot(counter.getId(), TicketStatus.CONCLUIDO) < MAX_QUEUE_SIZE)
            .findFirst();
    }
}
```

## Controller
Crie em `src/main/java/.../controller/TicketController.java`:

```java
@RestController
@RequestMapping("/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Ticket create(@RequestBody @Valid TicketRequest request) {
        return ticketService.create(request);
    }
}
```

## Exemplo de request
```json
POST /tickets
{
  "customerId": "user-123",
  "deviceId": "device-456",
  "serialNumber": "SN-789",
  "reason": "Maquininha não liga"
}
```

## Criterio de aceite
- [ ] `POST /tickets` cria o chamado com status ABERTO
- [ ] Chamado é associado automaticamente a um balcão com vaga
- [ ] Quando não há vaga, lança NoCounterAvailableException
