package projetohelpdesk.demo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import projetohelpdesk.demo.dto.CounterRequest;
import projetohelpdesk.demo.entity.Counter;
import projetohelpdesk.demo.repository.CounterRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CounterService {

    private final CounterRepository counterRepository;

    public Counter create(CounterRequest request) {
        Counter counter = new Counter();
        counter.setName(request.getName());
        counter.setAttendant(request.getAttendant());
        return counterRepository.save(counter);
    }

    public List<Counter> listAll() {
        return counterRepository.findAll();
    }

    public Counter getById(Long id) {
        return counterRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Balcão não encontrado: " + id));
    }

    public Counter update(Long id, CounterRequest request) {
        Counter counter = getById(id);
        counter.setName(request.getName());
        counter.setAttendant(request.getAttendant());
        return counterRepository.save(counter);
    }

    public void delete(Long id) {
        Counter counter = getById(id);
        counterRepository.delete(counter);
    }
}
