package projetohelpdesk.demo.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import projetohelpdesk.demo.dto.TicketDetailResponse;
import projetohelpdesk.demo.dto.TicketRequest;
import projetohelpdesk.demo.entity.Counter;
import projetohelpdesk.demo.entity.Ticket;
import projetohelpdesk.demo.enums.TicketStatus;
import projetohelpdesk.demo.exception.NoCounterAvailableException;
import projetohelpdesk.demo.repository.CounterRepository;
import projetohelpdesk.demo.repository.TicketRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TicketService {

    private static final int MAX_ACTIVE_TICKETS_PER_COUNTER = 5;

    private final TicketRepository ticketRepository;
    private final CounterRepository counterRepository;

    public Ticket create(TicketRequest request) {
        Counter availableCounter = findAvailableCounter()
                .orElseThrow(() -> new NoCounterAvailableException("Todos os balcões estão cheios."));

        Ticket ticket = new Ticket();
        ticket.setCustomerId(request.getCustomerId());
        ticket.setDeviceId(request.getDeviceId());
        ticket.setSerialNumber(request.getSerialNumber());
        ticket.setReason(request.getReason());
        ticket.setStatus(TicketStatus.ABERTO);
        ticket.setCounter(availableCounter);

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

    private java.util.Optional<Counter> findAvailableCounter() {
        List<Counter> allCounters = counterRepository.findAll();
        return allCounters.stream()
                .filter(counter -> ticketRepository
                        .countByCounterIdAndStatusNot(counter.getId(), TicketStatus.CONCLUIDO) < MAX_ACTIVE_TICKETS_PER_COUNTER)
                .findFirst();
    }

    public Page<Ticket> listAll(Pageable pageable){
        return ticketRepository.findAll(pageable);
    }

    public TicketDetailResponse getDetail(Long id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Chamado não encontrado: " + id));

    
        return TicketDetailResponse.builder()

            .id(ticket.getId())
            .reason(ticket.getReason())

            .customerId(ticket.getCustomerId())
            .customerName("Integração pendente")
            .attendantName(ticket.getCounter().getAttendant())
            .status(ticket.getStatus())

            .createdAt(ticket.getOpenedAt())
            .resolvedAt(ticket.getResolvedAt())

            .build();
    }
}
