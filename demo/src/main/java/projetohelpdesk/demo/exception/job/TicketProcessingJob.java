package projetohelpdesk.demo.exception.job;
                                                                                                                                      
  import lombok.RequiredArgsConstructor;
  import lombok.extern.slf4j.Slf4j;                                                                                                   
  import org.springframework.scheduling.annotation.Scheduled;
  import org.springframework.stereotype.Component;
  import org.springframework.transaction.annotation.Transactional;                                                                    
  import projetohelpdesk.demo.enums.TicketStatus;
  import projetohelpdesk.demo.repository.TicketRepository;                                                                            
                                                                                                                                      
  import java.time.LocalDateTime;


@Slf4j
@Component
@RequiredArgsConstructor
public class TicketProcessingJob {
    
    private final  TicketRepository ticketRepository;

    @Scheduled(fixedDelay = 120_000)
    @Transactional
    public void processTickets() {
        log.info("Processando chamados...");

        ticketRepository.findByStatus(TicketStatus.EM_ATENDIMENTO)
            .forEach(ticket ->{
                ticket.setStatus(TicketStatus.CONCLUIDO);
                ticket.setResolvedAt(LocalDateTime.now());
                ticketRepository.save(ticket);
                log.info("Chamado {} CONCLUIDO", ticket.getId());
            });
        ticketRepository.findByStatus(TicketStatus.ABERTO)
            .forEach(ticket -> {
                ticket.setStatus(TicketStatus.EM_ATENDIMENTO);
                ticketRepository.save(ticket);
                log.info("Chamado {} avancou para EM_ATENDIMENTO", ticket.getId());
            });
    }
}
