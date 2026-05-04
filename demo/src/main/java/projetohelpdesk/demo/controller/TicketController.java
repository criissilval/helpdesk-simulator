package projetohelpdesk.demo.controller;
                                                                                                                                                                   
  import jakarta.validation.Valid;
  import lombok.RequiredArgsConstructor;
  import org.springframework.data.domain.Page;
  import org.springframework.data.domain.Pageable;
  import org.springframework.http.HttpStatus;
  import org.springframework.web.bind.annotation.*;
  import projetohelpdesk.demo.dto.TicketRequest;
  import projetohelpdesk.demo.entity.Ticket;                                                                                                                       
  import projetohelpdesk.demo.service.TicketService;
  import projetohelpdesk.demo.dto.TicketDetailResponse;                                                                               
  import org.springframework.data.domain.PageRequest;  
                                                                                                                                                                   
  @RestController 
  @RequestMapping("/tickets")
  @RequiredArgsConstructor
  public class TicketController {
                                                                                                                                                                   
      private final TicketService ticketService;
                                                                                                                                                                   
      @PostMapping
      @ResponseStatus(HttpStatus.CREATED)
      public Ticket create(@RequestBody @Valid TicketRequest request) {
          return ticketService.create(request);
      }
                                                                                                                                                                   
      @GetMapping("/customer/{customerId}")
      public Page<Ticket> getByCustomer(@PathVariable String customerId, Pageable pageable) {                                                                      
          return ticketService.getByCustomer(customerId, pageable);
      }

      @GetMapping("/{id}")                                                                                                                                         
      public TicketDetailResponse getDetail(@PathVariable Long id) {
          return ticketService.getDetail(id);                                                                                                                        
      }           

      @PatchMapping("/{id}/resolve")
      public Ticket resolve(@PathVariable Long id) {
          return ticketService.resolve(id);
      }     
      
      @GetMapping
      public Page<Ticket> listAll(
                @RequestParam(defaultValue = "0") int page,
                @RequestParam(defaultValue = "10") int size){
            return ticketService.listAll(PageRequest.of(page,size));
        }
  }