package projetohelpdesk.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projetohelpdesk.demo.entity.Counter;

public interface CounterRepository extends JpaRepository<Counter, Long> {
}
