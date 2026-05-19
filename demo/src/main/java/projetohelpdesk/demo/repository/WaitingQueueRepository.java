package projetohelpdesk.demo.repository;
                                                                                                                                                   
  import org.springframework.data.jpa.repository.JpaRepository;
  import org.springframework.stereotype.Repository;                                                                                                
  import projetohelpdesk.demo.entity.WaitingQueue;
  import java.util.List;
                                                                                                                                                   
  @Repository
  public interface WaitingQueueRepository extends JpaRepository<WaitingQueue, Long> {                                                              
      List<WaitingQueue> findAllByOrderByCreatedAtAsc();
  }
