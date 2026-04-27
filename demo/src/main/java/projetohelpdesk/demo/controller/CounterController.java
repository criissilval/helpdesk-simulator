package projetohelpdesk.demo.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import projetohelpdesk.demo.dto.CounterRequest;
import projetohelpdesk.demo.entity.Counter;
import projetohelpdesk.demo.service.CounterService;

import java.util.List;

@RestController
@RequestMapping("/counters")
@RequiredArgsConstructor
public class CounterController {

    private final CounterService counterService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Counter create(@RequestBody @Valid CounterRequest request) {
        return counterService.create(request);
    }

    @GetMapping
    public List<Counter> listAll() {
        return counterService.listAll();
    }

    @GetMapping("/{id}")
    public Counter getById(@PathVariable Long id) {
        return counterService.getById(id);
    }

    @PutMapping("/{id}")
    public Counter update(@PathVariable Long id, @RequestBody @Valid CounterRequest request) {
        return counterService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        counterService.delete(id);
    }
}
