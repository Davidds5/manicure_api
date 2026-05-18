# 💅 Manicure API

> **API REST de Gestão de Salão de Beleza** desenvolvida em **Java 21** e **Spring Boot 3**, com foco total em padrões de produção corporativa.
>
> O objetivo desta API é fornecer um backend robusto e seguro para o gerenciamento completo de um salão de beleza: clientes, profissionais, serviços, agendamentos e pagamentos.

---

## 💡 O Problema Real que Este Projeto Resolve

Salões de beleza e estúdios de manicure gerenciam sua agenda de forma manual — agendamentos anotados em cadernos, grupos de WhatsApp para confirmação de horários e planilhas de Excel para controle financeiro.

Esse modelo gera problemas diários e concretos:

- 📵 **Conflito de horários:** Sem um sistema centralizado, dois clientes podem ser agendados no mesmo horário para a mesma profissional.
- 💸 **Perda de receita:** Sem histórico de pagamentos integrado ao agendamento, fica difícil identificar quais serviços foram pagos ou estão em aberto.
- 👩‍💼 **Falta de controle de acesso:** Qualquer pessoa com acesso ao WhatsApp do salão pode ver ou alterar informações sensíveis de clientes e profissionais.
- 📊 **Ausência de dados para decisão:** Sem um sistema, é impossível saber quais serviços são mais procurados, quais clientes retornam com mais frequência ou qual profissional gera mais receita.

**A Manicure API resolve todos esses problemas** fornecendo uma camada de backend segura, escalável e pronta para produção, que pode alimentar qualquer aplicativo mobile ou web de agendamento para salões de beleza.

---

## 🌐 Ambiente de Produção (Live)

A aplicação está publicada e rodando na nuvem. Acesse diretamente sem precisar instalar nada:

| Recurso | Link |
| :--- | :--- |
| 📖 **Swagger UI (Documentação Interativa)** | [manicure-api-vi63.onrender.com/swagger-ui/index.html](https://manicure-api-vi63.onrender.com/swagger-ui/index.html) |
| ⚙️ **API Base URL** | `https://manicure-api-vi63.onrender.com` |
| 💻 **Repositório GitHub** | [github.com/Davidds5/manicure_api](https://github.com/Davidds5/manicure_api) |
| 🗂️ **Portfólio do Desenvolvedor** | [davidds5.github.io/portfolio_clovin](https://davidds5.github.io/portfolio_clovin/) |

> ⚠️ **Aviso:** O servidor do Render pode entrar em modo sleep após períodos de inatividade. A primeira requisição pode levar até 60 segundos para acordar a instância.

---

## 🎯 Proposta de Valor

Diferente de sistemas acadêmicos simples, esta API foi arquitetada para resolver problemas reais de produção:

- **🔐 Segurança Robusta:** Autenticação Stateless com tokens JWT e autorização por roles (`CLIENTE` / `ADMIN`).
- **🧠 Autorização Inteligente:** Login polimórfico que distingue dinamicamente Clientes de Profissionais (Admins).
- **🔍 Consultas Otimizadas:** Filtros dinâmicos com *Spring Data JPA Specifications* e paginação com `Pageable`.
- **🛡️ Resiliência:** Tratamento de exceções global com `@RestControllerAdvice` e validação rigorosa com Bean Validation.
- **🐳 Containerização:** Imagem Docker otimizada com **Multi-stage Build**, separando as fases de compilação e execução.
- **🗄️ Versionamento de Banco:** Controle de schema via **Flyway Migrations**, garantindo integridade total do banco de dados.

---

## 🚀 Tecnologias e Ferramentas

| Categoria | Tecnologia |
| :--- | :--- |
| **Linguagem** | Java 21 |
| **Framework** | Spring Boot 3.x |
| **Segurança** | Spring Security + JWT (JJWT) |
| **Persistência** | Spring Data JPA + Hibernate |
| **Banco de Dados** | PostgreSQL (Produção) |
| **Migrations** | Flyway |
| **Documentação** | Springdoc OpenAPI 3 / Swagger UI |
| **Containerização** | Docker (Multi-stage Build) |
| **Deploy** | Render (Cloud PaaS) |
| **Testes** | JUnit 5 + Mockito |
| **Utilitários** | Lombok, MapStruct |

---

## 📁 Arquitetura e Domínios

O projeto segue o padrão de camadas corporativo:

```
src/main/java/br/com/davidds5/manicure_api/
├── controller/      # Camada de entrada HTTP (endpoints REST)
├── service/         # Regras de negócio
├── repository/      # Acesso ao banco de dados (JPA)
├── entity/          # Entidades JPA (tabelas do banco)
├── dto/             # Objetos de transferência de dados (Request/Response)
├── mapper/          # Conversão Entity <-> DTO (MapStruct)
├── security/        # Configuração de Segurança e filtros JWT
└── exception/       # Tratamento de exceções global
```

**Principais Domínios de Negócio:**

| Domínio | Descrição |
| :--- | :--- |
| 👤 **Client** | Gerenciamento completo de clientes do salão |
| 👩‍🎨 **Professional** | Gerenciamento de manicures e funcionários (Admins) |
| 💅 **Service** | Catálogo de serviços oferecidos (mão, pé, alongamento, etc.) |
| 📅 **Appointment** | Agendamentos conectando Cliente, Profissional e Serviços |
| 💳 **Payment** | Controle financeiro dos agendamentos |

---

## ⚙️ Como Rodar o Projeto Localmente

### Pré-requisitos

Antes de começar, certifique-se de ter instalado:
- [Java 21 (JDK)](https://jdk.java.net/21/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop/) *(recomendado)*
- [Git](https://git-scm.com/)

### Opção 1: Rodando com Docker (Recomendado ✅)

Esta é a forma mais fácil e rápida. Não precisa instalar PostgreSQL manualmente.

**1. Clone o repositório:**
```bash
git clone https://github.com/Davidds5/manicure_api.git
cd manicure_api
```

**2. Suba os containers com Docker Compose:**
```bash
docker-compose up --build
```

A API estará disponível em: `http://localhost:8080`

**3. Para derrubar os containers:**
```bash
docker-compose down
```

---

### Opção 2: Rodando com Maven (Requer PostgreSQL instalado)

**1. Clone o repositório:**
```bash
git clone https://github.com/Davidds5/manicure_api.git
cd manicure_api
```

**2. Configure o banco de dados:**

Crie um banco PostgreSQL com as seguintes credenciais (ou ajuste o `application.yml`):
- **Database:** `manicure_db`
- **Usuário:** `postgres`
- **Senha:** `12345`
- **Porta:** `5432`

**3. Execute o projeto:**
```bash
./mvnw spring-boot:run
```

> O **Flyway** criará automaticamente todas as tabelas e o schema do banco na inicialização.

A API estará disponível em: `http://localhost:8080`

---

## 📖 Como Usar a Documentação Swagger

A API possui uma interface gráfica interativa do Swagger para facilitar testes e integração.

**Acesse localmente:**
- **Swagger UI:** `http://localhost:8080/swagger-ui.html`
- **OpenAPI JSON:** `http://localhost:8080/v3/api-docs`

**Acesse em produção:**
- **Swagger UI:** `https://manicure-api-vi63.onrender.com/swagger-ui/index.html`

### 🔐 Como testar endpoints protegidos por JWT:

Como a API usa segurança Stateless com JWT, siga estes passos para acessar os endpoints protegidos:

**Passo 1 – Faça o login:**

No Swagger, localize o endpoint `POST /login` e faça uma requisição com as credenciais de um `Profissional` (que possui role de Admin):
```json
{
  "email": "seu-email@exemplo.com",
  "password": "sua-senha"
}
```

**Passo 2 – Copie o token:**

Copie o valor do token JWT que aparece na resposta da requisição.

**Passo 3 – Autorize no Swagger:**

1. Clique no botão **`Authorize 🔒`** no topo da página do Swagger.
2. Cole o token JWT no campo e clique em **Authorize**.
3. Feche o modal. Agora todos os endpoints protegidos estarão disponíveis para teste!

---

## 🐳 Docker – Detalhes Técnicos

O projeto utiliza um **Dockerfile com Multi-stage Build** para otimizar a imagem final:

- **Stage 1 (Build):** Usa a imagem `maven:3.9-eclipse-temurin-21` para compilar e empacotar o `.jar` completo.
- **Stage 2 (Runtime):** Usa apenas o `eclipse-temurin:21-jre-alpine` (ultra leve) para executar o `.jar` gerado.

> **Resultado:** Imagem de produção enxuta, sem o Maven ou os arquivos de código-fonte, reduzindo vulnerabilidades e tamanho da imagem final.

---

## 🛠️ Como Contribuir

1. Faça um **Fork** do projeto
2. Crie sua **Feature Branch:**
   ```bash
   git checkout -b feature/MinhaFeature
   ```
3. Faça **Commit** das suas mudanças:
   ```bash
   git commit -m 'feat: adicionando nova funcionalidade'
   ```
4. Faça **Push** para a Branch:
   ```bash
   git push origin feature/MinhaFeature
   ```
5. Abra um **Pull Request**

---

## 👨‍💻 Desenvolvedor

**David Silva** — Desenvolvedor Java Backend

[![Portfolio](https://img.shields.io/badge/Portfólio-000000?style=for-the-badge&logo=github&logoColor=white)](https://davidds5.github.io/portfolio_clovin/)
[![GitHub](https://img.shields.io/badge/GitHub-100000?style=for-the-badge&logo=github&logoColor=white)](https://github.com/Davidds5)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/david-silva-17b2882bb)

---

## 📄 Licença

Este projeto é de uso livre / educacional e faz parte do portfólio profissional do desenvolvedor.