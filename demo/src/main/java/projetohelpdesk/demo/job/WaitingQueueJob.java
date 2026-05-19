package projetohelpdesk.demo.job;

import lombok.RequiredArgsConstructor;                                                                                                           
  import lombok.extern.slf4j.Slf4j;
  import org.springframework.scheduling.annotation.Scheduled;                                                                                      
  import org.springframework.stereotype.Component;
  import org.springframework.transaction.annotation.Transactional;
  import org.springframework.web.server.ResponseStatusException;                                                                                   
  
  import projetohelpdesk.demo.dto.TicketRequest;                                                                                                   
  import projetohelpdesk.demo.entity.WaitingQueue;
  import projetohelpdesk.demo.repository.WaitingQueueRepository;                                                                                   
  import projetohelpdesk.demo.service.TicketService;
                                                                                                                                                   
  import java.util.List;  

  @Slf4j
  @Component
  @RequiredArgsConstructor
  public class WaitingQueueJob {

    private final WaitingQueueRepository waitingQueueRepository;
    private final TicketService ticketService;

    @Scheduled(fixedDelay = 180_000)
    @Transactional
    public void processWaitingQueue(){
        List<WaitingQueue> waiting = waitingQueueRepository.findAllByOrderByCreatedAtAsc();

        for (WaitingQueue item : waiting) {
            try{
                TicketRequest request = new TicketRequest();
                request.setCustomerId(item.getCustomerId());
                request.setDeviceId(item.getDeviceId());
                request.setSerialNumber(item.getSerialNumber());
                request.setReason(item.getReason());

                ticketService.create(request);
                waitingQueueRepository.delete(item);
                log.info("Item da fila de espera processado: {}", item.getId());
            } catch (ResponseStatusException e){
                log.info("Ainda sem balcao disponivel. Item {} permance na fila.", item.getId());
                break;
            }
        }
    } 
}                                                                                                                                                 
  
