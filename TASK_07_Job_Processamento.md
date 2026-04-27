# Task 07 — Job de Processamento de Chamados

## Objetivo
Criar um agendador automático que avança o status dos chamados a cada 2 minutos.

## Fluxo de status
```
ABERTO --> EM_ATENDIMENTO --> CONCLUIDO
```
Cada transição leva 2 minutos (controlado pelo intervalo do Job).

## Habilitando o Scheduling
Adicione `@EnableScheduling` na classe principal:

```java
@EnableScheduling
@SpringBootApplication
public class HelpdeskApplication {
    public static void main(String[] args) {
        SpringApplication.run(HelpdeskApplication.class, args);
    }
}
```

## Job de Processamento
Crie em `src/main/java/.../job/TicketProcessingJob.java`:

```java
@Component
@RequiredArgsConstructor
@Slf4j
public class TicketProcessingJob {

    private final TicketRepository ticketRepository;

    // Executa a cada 2 minutos (120.000 milissegundos)
    @Scheduled(fixedDelay = 120_000)
    @Transactional
    public void processTickets() {
        log.info("Processando chamados...");

        // Avança ABERTO → EM_ATENDIMENTO
        ticketRepository.findByStatus(TicketStatus.ABERTO)
            .forEach(ticket -> {
                ticket.setStatus(TicketStatus.EM_ATENDIMENTO);
                ticketRepository.save(ticket);
                log.info("Chamado {} avancou para EM_ATENDIMENTO", ticket.getId());
            });

        // Avança EM_ATENDIMENTO → CONCLUIDO
        ticketRepository.findByStatus(TicketStatus.EM_ATENDIMENTO)
            .forEach(ticket -> {
                ticket.setStatus(TicketStatus.CONCLUIDO);
                ticket.setResolvedAt(LocalDateTime.now());
                ticketRepository.save(ticket);
                log.info("Chamado {} CONCLUIDO", ticket.getId());
            });
    }
}
```

## Dica para testes
Durante o desenvolvimento, reduza o intervalo para testar mais rápido:
```java
// 10 segundos para testar
@Scheduled(fixedDelay = 10_000)
```
Lembre de voltar para 120_000 antes de entregar.

## Criterio de aceite
- [ ] Após 2 minutos, chamados com status ABERTO mudam para EM_ATENDIMENTO
- [ ] Após mais 2 minutos, chamados EM_ATENDIMENTO mudam para CONCLUIDO
- [ ] Campo `resolvedAt` é preenchido ao concluir
- [ ] Logs aparecem no console a cada execução do Job
