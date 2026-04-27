# Task 10 — Integração com Data Vault

## Objetivo
No endpoint de detalhes do chamado (`GET /tickets/{id}`), buscar o nome do cliente em um serviço externo chamado Data Vault para enriquecer a resposta.

## Dependência necessária
Adicione no `pom.xml` (para usar o WebClient):

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

## Configuração no application.yml
```yaml
data-vault:
  url: https://api.data-vault.internal  # substituir pela URL real
```

## DTO de resposta do Data Vault
Crie em `src/main/java/.../dto/CustomerResponse.java`:

```java
@Data
public class CustomerResponse {
    private String id;
    private String name;
    private String email;
}
```

## Cliente HTTP
Crie em `src/main/java/.../client/DataVaultClient.java`:

```java
@Component
public class DataVaultClient {

    private final WebClient webClient;

    public DataVaultClient(@Value("${data-vault.url}") String baseUrl) {
        this.webClient = WebClient.builder()
            .baseUrl(baseUrl)
            .build();
    }

    public String getCustomerName(String customerId) {
        try {
            return webClient.get()
                .uri("/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(CustomerResponse.class)
                .map(CustomerResponse::getName)
                .block();
        } catch (Exception e) {
            // Fallback: se o serviço estiver fora, não quebra a API
            return "Cliente nao encontrado";
        }
    }
}
```

## Atualizando o TicketService
Injete o `DataVaultClient` e atualize o método `getDetail`:

```java
@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final CounterRepository counterRepository;
    private final WaitingQueueRepository waitingQueueRepository;
    private final DataVaultClient dataVaultClient; // injetar aqui

    public TicketDetailResponse getDetail(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        // Busca nome do cliente no Data Vault
        String customerName = dataVaultClient.getCustomerName(ticket.getCustomerId());

        return TicketDetailResponse.builder()
            .id(ticket.getId())
            .reason(ticket.getReason())
            .customerId(ticket.getCustomerId())
            .customerName(customerName)          // agora vem do Data Vault
            .attendantName(ticket.getCounter().getAttendant())
            .status(ticket.getStatus())
            .createdAt(ticket.getCreatedAt())
            .resolvedAt(ticket.getResolvedAt())
            .build();
    }
}
```

## Resposta esperada do endpoint
```json
GET /tickets/42

{
  "id": 42,
  "reason": "Maquininha nao liga",
  "customerId": "user-123",
  "customerName": "Maria Oliveira",
  "attendantName": "João Silva",
  "status": "EM_ATENDIMENTO",
  "createdAt": "2026-04-15T10:00:00",
  "resolvedAt": null
}
```

## Criterio de aceite
- [ ] `GET /tickets/{id}` retorna `customerName` vindo do Data Vault
- [ ] Se o Data Vault estiver fora do ar, a API retorna "Cliente nao encontrado" sem quebrar
- [ ] Os demais campos continuam corretos
