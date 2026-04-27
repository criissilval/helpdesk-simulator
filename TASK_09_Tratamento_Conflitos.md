# Task 09 — Tratamento de Conflitos (409 e 403)

## Objetivo
Implementar as regras de conflito por `serial_number` para evitar chamados duplicados ou não autorizados.

## Regras de negócio
| Situação | Resposta |
|---|---|
| Mesmo `customerId` + mesmo `serialNumber` com chamado não concluído | **409 Conflict** com URL do chamado |
| `customerId` diferente + `serialNumber` com chamado não concluído | **403 Forbidden** |
| Chamado do serial já está **CONCLUIDO** | Permite criar novo normalmente |

## Verificação no TicketService
Adicione as verificações **no início** do método `create`, antes de qualquer outra lógica:

```java
@Transactional
public Ticket create(TicketRequest request) {

    // Busca chamado ativo (não concluído) para este serial
    Optional<Ticket> existingTicket = ticketRepository
        .findBySerialNumberAndStatusNot(request.getSerialNumber(), TicketStatus.CONCLUIDO);

    if (existingTicket.isPresent()) {
        Ticket existing = existingTicket.get();

        // Mesmo usuário → 409 com URL do chamado existente
        if (existing.getCustomerId().equals(request.getCustomerId())) {
            String detailUrl = "/tickets/" + existing.getId();
            throw new ResponseStatusException(
                HttpStatus.CONFLICT,
                "Voce ja possui um chamado aberto para este serial. Detalhes: " + detailUrl
            );
        }

        // Outro usuário → 403 Forbidden
        throw new ResponseStatusException(
            HttpStatus.FORBIDDEN,
            "Este serial ja esta em atendimento por outro usuario."
        );
    }

    // A partir daqui, segue o fluxo normal (Tasks 05 e 08)
    // ...
}
```

## Exemplo de respostas

### 409 Conflict (mesmo usuário)
```json
HTTP/1.1 409 Conflict
{
  "message": "Voce ja possui um chamado aberto para este serial. Detalhes: /tickets/42"
}
```

### 403 Forbidden (outro usuário)
```json
HTTP/1.1 403 Forbidden
{
  "message": "Este serial ja esta em atendimento por outro usuario."
}
```

## Fluxo de decisão
```
POST /tickets
     |
     v
Serial tem chamado ativo (não CONCLUIDO)?
     |
    SIM --> Mesmo customerId?
     |           |
     |          SIM --> 409 + URL do chamado
     |           |
     |          NAO --> 403 Forbidden
     |
    NAO --> Segue criação normal
```

## Criterio de aceite
- [ ] Mesmo usuário + mesmo serial com chamado aberto → 409 com URL
- [ ] Usuário diferente + serial em atendimento → 403
- [ ] Chamado CONCLUIDO para o serial → aceita criar novo normalmente
- [ ] Sem chamado ativo para o serial → aceita criar normalmente
