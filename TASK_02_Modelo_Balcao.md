# Task 02 — Modelo de Dados: Balcão de Atendimento

## Objetivo
Criar a tabela de balcões no banco de dados e a entidade Java que a representa.

## Migration SQL
Crie o arquivo `src/main/resources/db/migration/V1__create_counters_table.sql`:

```sql
CREATE TABLE counters (
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(100) NOT NULL,
    attendant   VARCHAR(100) NOT NULL,
    created_at  TIMESTAMP NOT NULL DEFAULT NOW()
);
```

## Entidade Java
Arquivo: `src/main/java/projetohelpdesk/demo/entity/ServiceDesk.java`

```java
@Entity
@Table(name = "service_desks")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ServiceDesk {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(name = "attendant_name", nullable = false)
    private String attendantName;

    @OneToMany(mappedBy = "serviceDesk", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Ticket> tickets = new ArrayList<>();
}
```

## Repository
Arquivo: `src/main/java/projetohelpdesk/demo/repository/ServiceDeskRespository.java`

> Atenção: o arquivo tem um typo no nome (Respository em vez de Repository). Não precisa renomear agora para não quebrar nada.

```java
@Repository
public interface ServiceDeskRespository extends JpaRepository<ServiceDesk, Long> {
}
```

## Criterio de aceite
- [x] Aplicação sobe sem erro
- [x] Tabela `service_desks` criada no banco automaticamente pelo Hibernate
