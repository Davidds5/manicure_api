# Manicure API 💅
> **API de Gestão de Salão de Beleza** desenvolvida em **Java 21** e **Spring Boot**. 
> O objetivo principal desta API é fornecer os serviços de backend necessários para o gerenciamento completo de serviços de manicure, clientes, profissionais, agendamentos e pagamentos.
---
## 🎯 Proposta de Valor
Diferente de sistemas acadêmicos simples (CRUDs básicos), esta API foi projetada para resolver problemas reais de produção, focando em:
- **Segurança Robusta:** Proteção de endpoints sensíveis utilizando autenticação Stateless via JWT.
- **Autorização Inteligente:** Sistema polimórfico de login que distingue dinamicamente entre `Clientes` (usuários comuns) e `Profissionais` (Admins).
- **Consultas Otimizadas:** Implementação de filtros dinâmicos de busca utilizando *Spring Data JPA Specifications*, permitindo consultas complexas sem sobrecarregar o banco de dados.
- **Resiliência:** Tratamento de exceções global e validação rigorosa de dados de entrada (Bean Validation).
---
## 🚀 Tecnologias e Ferramentas
O projeto utiliza o que há de mais moderno no ecossistema Java/Spring:
- **Linguagem:** [Java 21](https://jdk.java.net/21/)
- **Framework:** [Spring Boot 3.x](https://spring.io/projects/spring-boot)
  - Spring Web (REST)
  - Spring Data JPA
  - Spring Security + JWT (JSON Web Token)
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
- **Testes:** JUnit 5 & Mockito
---
## 📁 Estrutura do Projeto e Entidades Principais
A arquitetura do projeto segue o padrão de camadas corporativo (Controller, Service, Repository, Mapper, Entity, DTO).
**Principais Domínios:**
* 👤 **Client (Cliente):** Gerenciamento de clientes do salão.
* 👩‍🎨 **Professional (Profissional):** Gerenciamento de manicures e funcionários (Admins).
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
Compile e rode o projeto usando o Maven Wrapper:

Ambiente Principal (com PostgreSQL): Crie um banco de dados no Postgres chamado manicure_db com usuário postgres e senha 12345.

bash
./mvnw spring-boot:run
(O Flyway criará as tabelas e o esquema do banco automaticamente na inicialização).

Ambiente de Desenvolvimento (com Banco H2 em memória):

bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
📖 Documentação da API (Swagger)
A API possui uma interface gráfica interativa do Swagger para facilitar testes e integração.

Importante (Segurança): Como a API utiliza segurança Stateless com JWT, para testar os endpoints protegidos:

Realize o login no endpoint /login com as credenciais de um Profissional (para ter acesso total).
Copie o token gerado na resposta.
Clique no botão Authorize (ícone de cadeado) no topo da página do Swagger.
Insira o token (cole diretamente, o prefixo Bearer é automático) e clique em Authorize.
Acesse no seu navegador (com a aplicação rodando):

Swagger UI: http://localhost:8080/swagger-ui.html
OpenAPI JSON: http://localhost:8080/v3/api-docs
🛠️ Contribuindo
Faça um Fork do projeto
Crie sua Feature Branch (git checkout -b feature/MinhaFeature)
Faça Commit das suas mudanças (git commit -m 'feat: adicionando nova funcionalidade')
Faça Push para a Branch (git push origin feature/MinhaFeature)
Abra um Pull Request
📄 Licença
Este projeto é de uso livre / educacional e parte de portfólio profissional.