# Task 06 — APIs de Listagem e Detalhes de Chamados

## Objetivo
Implementar os 3 endpoints de consulta de chamados com paginação e detalhamento.

## DTO de Detalhe
Crie em `src/main/java/.../dto/TicketDetailResponse.java`:

```java
@Data
@Builder
public class TicketDetailResponse {
    private Long id;
    private String reason;
    private String customerId;
    private String customerName;   // virá do Data Vault (Task 10)
    private String attendantName;  // nome do atendente do balcão
    private TicketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime resolvedAt;
}
```

## Métodos no TicketService
Adicione em `TicketService.java`:

```java
public Page<Ticket> listAll(Pageable pageable) {
    return ticketRepository.findAll(pageable);
}

public Page<Ticket> listByCustomer(String customerId, Pageable pageable) {
    return ticketRepository.findByCustomerId(customerId, pageable);
}

public TicketDetailResponse getDetail(Long id) {
    Ticket ticket = ticketRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

    return TicketDetailResponse.builder()
        .id(ticket.getId())
        .reason(ticket.getReason())
        .customerId(ticket.getCustomerId())
        .customerName("Integração pendente")  // atualizar na Task 10
        .attendantName(ticket.getCounter().getAttendant())
        .status(ticket.getStatus())
        .createdAt(ticket.getCreatedAt())
        .resolvedAt(ticket.getResolvedAt())
        .build();
}
```

## Endpoints no TicketController
Adicione em `TicketController.java`:

```java
@GetMapping
public Page<Ticket> listAll(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    return ticketService.listAll(PageRequest.of(page, size));
}

@GetMapping("/customer/{customerId}")
public Page<Ticket> listByCustomer(
        @PathVariable String customerId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
    return ticketService.listByCustomer(customerId, PageRequest.of(page, size));
}

@GetMapping("/{id}")
public TicketDetailResponse getDetail(@PathVariable Long id) {
    return ticketService.getDetail(id);
}
```

## Endpoints disponíveis
| Método | URL | Descrição |
|---|---|---|
| GET | /tickets?page=0&size=10 | Lista todos os chamados paginado |
| GET | /tickets/customer/{customerId}?page=0&size=10 | Lista chamados de um cliente |
| GET | /tickets/{id} | Detalhes de um chamado específico |

## Criterio de aceite
- [ ] `GET /tickets` retorna lista paginada
- [ ] `GET /tickets/customer/{id}` filtra por cliente com paginação
- [ ] `GET /tickets/{id}` retorna os detalhes completos do chamado
- [ ] Chamado não encontrado retorna 404
