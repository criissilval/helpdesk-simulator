# Task 01 — Configurar o Projeto Base

## Objetivo
Criar o projeto Spring Boot do zero com todas as dependências necessárias para o simulador de Help Desk.

## Dependências usadas
Gerado via [start.spring.io](https://start.spring.io) com:
- **Project:** Maven
- **Language:** Java 21
- **Spring Boot:** 4.x
- **Artifact:** `helpdesk`

### Dependências adicionadas:
| Dependência | Para que serve |
|---|---|
| Spring Web | Criar as APIs REST |
| Spring Data JPA | Comunicação com o banco de dados |
| PostgreSQL Driver | Conector com o PostgreSQL |
| Spring Validation | Validar dados que chegam na API |
| Lombok | Evitar escrever getters/setters manualmente |
| DevTools | Reinicia a aplicação automaticamente ao salvar arquivos |

## Configuração do application.properties
Arquivo: `src/main/resources/application.properties`

```properties
spring.application.name=helpdesk

spring.datasource.url=jdbc:postgresql://localhost:5432/helpdesk
spring.datasource.driver-class-name=org.postgresql.Driver
spring.datasource.username=${USER}
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.open-in-view=false
```

> `${USER}` pega automaticamente o nome do seu usuário do Mac — não precisa trocar nada.

## Subindo o banco com Homebrew (sem Docker)

```bash
# 1. Instala o PostgreSQL
brew install postgresql@15

# 2. Inicia o banco (e faz ele iniciar junto com o Mac)
brew services start postgresql@15

# 3. Cria o banco do projeto
/opt/homebrew/opt/postgresql@15/bin/createdb helpdesk
```

## Subindo a aplicação

```bash
cd /Users/claurentino/Documents/HelpDesk/demo
./mvnw spring-boot:run
```

## Criterio de aceite
- [x] Aplicação sobe sem erro no terminal
- [x] Banco de dados acessível na porta 5432
- [x] Tabelas criadas automaticamente pelo Hibernate
