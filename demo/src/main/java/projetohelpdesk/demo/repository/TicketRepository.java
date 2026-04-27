package projetohelpdesk.demo.repository;
                                                                                                                                                                   
  import org.springframework.data.domain.Page;
  import org.springframework.data.domain.Pageable;                                                                                                                 
  import org.springframework.data.jpa.repository.JpaRepository;                                                                                                    
  import projetohelpdesk.demo.entity.Ticket;
  import projetohelpdesk.demo.enums.TicketStatus;                                                                                                                  
                                                                                                                                                                   
  public interface TicketRepository extends JpaRepository<Ticket, Long> {
                                                                                                                                                                   
      long countByCounterIdAndStatusNot(Long counterId, TicketStatus status);

      Page<Ticket> findByCustomerId(String customerId, Pageable pageable);                                                                                         
  }
