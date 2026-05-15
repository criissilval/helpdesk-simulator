package projetohelpdesk.demo.service;
                                                                                                                                                   
  import jakarta.persistence.EntityNotFoundException;                                                                                              
  import lombok.RequiredArgsConstructor;
  import org.springframework.data.domain.Page;                                                                                                     
  import org.springframework.data.domain.Pageable;
  import org.springframework.http.HttpStatus;                                                                                                      
  import org.springframework.stereotype.Service;
  import org.springframework.transaction.annotation.Transactional;                                                                                 
  import org.springframework.web.server.ResponseStatusException;
                                                                                                                                                   
  import projetohelpdesk.demo.dto.TicketDetailResponse;
  import projetohelpdesk.demo.dto.TicketRequest;                                                                                                   
  import projetohelpdesk.demo.entity.Counter;                                                                                                      
  import projetohelpdesk.demo.entity.Ticket;
  import projetohelpdesk.demo.entity.WaitingQueue;                                                                                                 
  import projetohelpdesk.demo.enums.TicketStatus;                                                                                                  
  import projetohelpdesk.demo.repository.CounterRepository;
  import projetohelpdesk.demo.repository.TicketRepository;                                                                                         
  import projetohelpdesk.demo.repository.WaitingQueueRepository;
  import projetohelpdesk.demo.client.DataVaultClient;

                                                                                                                                                   
  import java.time.LocalDateTime;
  import java.util.List;                                                                                                                           
  import java.util.Optional;

  @Service
  @RequiredArgsConstructor
  public class TicketService {

      private static final int MAX_ACTIVE_TICKETS_PER_COUNTER = 5;                                                                                 
   
      private final TicketRepository ticketRepository;
      private final CounterRepository counterRepository;
      private final WaitingQueueRepository waitingQueueRepository;
      private final DataVaultClient dataVaultClient;

      @Transactional  
  public Ticket create(TicketRequest request) {                                                                                                    
                                                                                                                                                   
      Optional<Ticket> existingTicket = ticketRepository
          .findBySerialNumberAndStatusNot(request.getSerialNumber(), TicketStatus.CONCLUIDO);                                                      
                  
      if (existingTicket.isPresent()) {                                                                                                            
          Ticket existing = existingTicket.get();
                                                                                                                                                   
          if (existing.getCustomerId().equals(request.getCustomerId())) {
              String detailUrl = "/tickets/" + existing.getId();
              throw new ResponseStatusException(                                                                                                   
                  HttpStatus.CONFLICT,
                  "Voce ja possui um chamado aberto para este serial. Detalhes: " + detailUrl                                                      
              );                                                                                                                                   
          }
                                                                                                                                                   
          throw new ResponseStatusException(
              HttpStatus.FORBIDDEN,
              "Este serial ja esta em atendimento por outro usuario."                                                                              
          );
      }                                                                                                                                            
                                                                                                                                                   
      Optional<Counter> availableCounter = findAvailableCounter();
                                                                                                                                                   
      if (availableCounter.isEmpty()) {
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
                  
      public Page<Ticket> getByCustomer(String customerId, Pageable pageable) {                                                                    
          return ticketRepository.findByCustomerId(customerId, pageable);
      }                                                                                                                                            
                  
      public Ticket getById(Long id) {
          return ticketRepository.findById(id)
                  .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + id));
      }                                                                                                                                            
  
      public Ticket resolve(Long id) {                                                                                                             
          Ticket ticket = getById(id);
          ticket.setStatus(TicketStatus.CONCLUIDO);                                                                                                
          ticket.setResolvedAt(LocalDateTime.now());
          return ticketRepository.save(ticket);                                                                                                    
      }           

      private Optional<Counter> findAvailableCounter() {                                                                                           
          List<Counter> allCounters = counterRepository.findAll();
          return allCounters.stream()                                                                                                              
                  .filter(counter -> ticketRepository
                          .countByCounterIdAndStatusNot(counter.getId(), TicketStatus.CONCLUIDO) < MAX_ACTIVE_TICKETS_PER_COUNTER)                 
                  .findFirst();                                                                                                                    
      }                                                                                                                                            
                                                                                                                                                   
      public Page<Ticket> listAll(Pageable pageable) {                                                                                             
          return ticketRepository.findAll(pageable);
      }                                                                                                                                            
                  
      public TicketDetailResponse getDetail(Long id) {
          Ticket ticket = ticketRepository.findById(id)
              .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + id));

          String customerName = dataVaultClient.getCustomerName(ticket.getCustomerId());

          return TicketDetailResponse.builder()
              .id(ticket.getId())
              .reason(ticket.getReason())
              .customerId(ticket.getCustomerId())
              .customerName(customerName)
              .attendantName(ticket.getCounter().getAttendant())
              .status(ticket.getStatus())
              .createdAt(ticket.getOpenedAt())
              .resolvedAt(ticket.getResolvedAt())
              .build();
      }
  }       