# Manicure API 💅

> **API de Gestão de Salão de Beleza** desenvolvida em **Java 21** e **Spring Boot**. 
> O objetivo principal desta API é fornecer os serviços de backend necessários para o gerenciamento completo de serviços de manicure, clientes, profissionais, agendamentos e pagamentos.

---

## 🚀 Tecnologias e Ferramentas

O projeto utiliza o que há de mais moderno no ecossistema Java/Spring:

- **Linguagem:** [Java 21](https://jdk.java.net/21/)
- **Framework:** [Spring Boot 3.x](https://spring.io/projects/spring-boot)
  - Spring Web (REST)
  - Spring Data JPA
  - Spring Validation
  - Spring HATEOAS
- **Banco de Dados:** 
  - [PostgreSQL](https://www.postgresql.org/) (Produção)
  - [H2 Database](https://www.h2database.com/html/main.html) (Desenvolvimento / Testes em memória)
- **Migrations:** [Flyway](https://flywaydb.org/)
- **Utilitários:** 
  - [Lombok](https://projectlombok.org/) (Redução de boilerplate)
  - [MapStruct](https://mapstruct.org/) (Mapeamento de Objetos/DTOs)
- **Documentação da API:** [Springdoc OpenAPI / Swagger UI](https://springdoc.org/)

---

## 📁 Estrutura do Projeto e Entidades Principais

A arquitetura do projeto segue o padrão de camadas (Controller, Service, Repository, Mapper, Entity, DTO).

**Principais Domínios:**
* 👤 **Client (Cliente):** Gerenciamento de clientes do salão.
* 👩‍🎨 **Professional (Profissional):** Gerenciamento de manicures e funcionários.
* 💅 **Service (Serviço):** Catálogo de serviços oferecidos (ex: pé, mão, alongamento).
* 📅 **Appointment (Agendamento):** Marcação de horários conectando Cliente, Profissional e Serviços prestados.
* 💳 **Payment (Pagamento):** Controle financeiro relacionado aos agendamentos.

---

## ⚙️ Configuração e Execução

### Pré-requisitos
* **Java 21** instalado
* **PostgreSQL** instalado e rodando na porta `5432` (para o perfil padrão)
* Ou apenas executar no perfil `dev` com banco em memória (H2).

### Passos para rodar localmente

1. Clone o repositório e navegue até a pasta do projeto Java:
   ```bash
   cd manicure-api
   ```

2. Compile e rode o projeto usando o Maven Wrapper:

   **Ambiente de Desenvolvimento (com Banco H2 em memória):**
   ```bash
   ./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
   ```
   *(No Windows use `mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev`)*

   **Ambiente Principal (com PostgreSQL):**
   Crie um banco de dados no Postgres chamado `manicure_db` com usuário/senha `postgres`.
   ```bash
   ./mvnw spring-boot:run
   ```
   *(O Flyway criará as tabelas automaticamente na inicialização).*

---

## 📖 Documentação da API (Swagger)

A API possui uma interface gráfica interativa do Swagger para facilitar testes e integração.

> **Importante:** Como a API utiliza segurança Stateless com **JWT**, para testar os endpoints protegidos:
> 1. Realize o login no endpoint de autenticação (Professional/Login).
> 2. Copie o `token` gerado na resposta.
> 3. Clique no botão **Authorize** (ícone de cadeado) no topo da página do Swagger.
> 4. Insira o token e clique em Authorize.

Acesse no seu navegador:
- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🛠️ Contribuindo

1. Faça um Fork do projeto
2. Crie sua Feature Branch (`git checkout -b feature/MinhaFeature`)
3. Faça Commit das suas mudanças (`git commit -m 'feat: adicionando nova funcionalidade'`)
4. Faça Push para a Branch (`git push origin feature/MinhaFeature`)
5. Abra um Pull Request

---

## 📄 Licença
Este projeto é de uso livre / não especificado. Sinta-se à vontade para modificar conforme achar necessário.
