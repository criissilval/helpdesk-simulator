# Task 04 — CRUD de Balcão de Atendimento

## Objetivo
Criar as APIs para cadastrar e listar balcões de atendimento.

## DTO de Request
Crie em `src/main/java/.../dto/CounterRequest.java`:

```java
@Data
public class CounterRequest {
    @NotBlank
    private String name;
    @NotBlank
    private String attendant;
}
```

## Service
Crie em `src/main/java/.../service/CounterService.java`:

```java
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
}
```

## Controller
Crie em `src/main/java/.../controller/CounterController.java`:

```java
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
}
```

## Endpoints disponíveis
| Método | URL | Descrição |
|---|---|---|
| POST | /counters | Cadastra um novo balcão |
| GET | /counters | Lista todos os balcões |

## Exemplo de request
```json
POST /counters
{
  "name": "Balcão 1",
  "attendant": "João Silva"
}
```

## Criterio de aceite
- [ ] `POST /counters` cria e retorna o balcão
- [ ] `GET /counters` lista todos os balcões cadastrados
