# 💅 Manicure API (SaaS Multi-Tenant)

> **Plataforma SaaS Multi-Tenant corporativa de Gestão de Salões de Beleza** desenvolvida em **Java 21** e **Spring Boot 3**, com arquitetura isolada de dados por salão (Tenant), controle de assinaturas/planos e conformidade com padrões de produção corporativa.

---

## 🌐 Ambiente de Produção (Live)

| Recurso | Link |
| :--- | :--- |
| 📖 **Swagger UI (Documentação Interativa)** | [manicure-api-vi63.onrender.com/swagger-ui/index.html](https://manicure-api-vi63.onrender.com/swagger-ui/index.html) |
| ⚙️ **API Base URL** | `https://manicure-api-vi63.onrender.com` |
| 💻 **Repositório GitHub** | [github.com/Davidds5/manicure_api](https://github.com/Davidds5/manicure_api) |

> ⚠️ **Nota:** No plano gratuito do Render, a aplicação pode levar até ~50 segundos para inicializar na primeira requisição após período de inatividade.

---

## 🏢 Arquitetura Multi-Tenant & Funcionalidades

Diferente de APIs tradicionais de salão único, a **Manicure API** opera em arquitetura **Multi-Tenant (Shared Database, Pooled Schema)**:

- **🏢 Auto-onboarding de Salões (`POST /tenants/signup`):** Permite que qualquer dono de salão crie sua conta e gere uma instância isolada instantaneamente.
- **🛡️ Isolamento Estrito de Dados:** Cada requisição autenticada extrai o `tenant_id` do JWT para um `TenantContext` (`ThreadLocal`), aplicando filtros globais no Hibernate para blindagem total contra vazamento cross-tenant.
- **💳 Gestão de Planos & Assinaturas (`FREE`, `PRO`, `ENTERPRISE`):** Bloqueio automático de novos cadastros de profissionais/serviços ao atingir limites do plano com retorno `HTTP 402 Payment Required`.
- **👑 Painel SUPER_ADMIN (`/admin/tenants`):** Visão administrativa global com métricas de salões ativos, inadimplência e cálculo de MRR.
- **📅 Gestão Completa de Salão:** Clientes, Profissionais, Catálogo de Serviços, Agendamentos e Histórico de Pagamentos.

---

## 🚀 Stack Tecnológica

| Camada | Tecnologia |
| :--- | :--- |
| **Linguagem & Runtime** | Java 21 (LTS) |
| **Framework** | Spring Boot 3.x (Web, Security, Data JPA, Validation, AOP) |
| **Segurança & Auth** | Spring Security + JWT com Claims Customizados (`tenant_id`, `role`) + BCrypt |
| **Banco de Dados & ORM** | PostgreSQL + Hibernate / Spring Data JPA |
| **Database Migrations** | Flyway (Schema versionado V1 a V13) |
| **Documentação Interativa** | Springdoc OpenAPI 3 / Swagger UI |
| **Containerização & Deploy** | Docker (Multi-stage Build) + Render Cloud PaaS |
| **Testes Automatizados** | JUnit 5 + Mockito + MockMvc |

---

## 📁 Estrutura de Pacotes

```
src/main/java/br/com/davidds5/manicure_api/
├── config/           # Configurações de Segurança, Swagger, TenantContext e Interceptors
├── controller/       # Endpoints REST (Tenants, Auth, Admin, Agendamentos, etc.)
├── service/          # Lógica de negócio, validação de limites de planos e tenancy
├── repository/       # Interfaces Spring Data JPA
├── entity/           # Entidades JPA com tenant_id (Tenant, Client, Professional, Appointment...)
├── dto/              # Records e DTOs de Request / Response
├── exception/        # GlobalExceptionHandler e Exceptions customizadas de negócio
└── aspect/           # AOP para aplicação automática do filtro de Tenant no Hibernate
```

---

## ⚙️ Como Rodar Localmente

### Pré-requisitos
- **Java 21**
- **Docker & Docker Compose** (ou PostgreSQL local)

### 1. Clonar o Repositório
```bash
git clone https://github.com/Davidds5/manicure_api.git
cd manicure_api
```

### 2. Rodar via Docker Compose (Recomendado)
```bash
docker-compose up --build
```
A API inicializará em: `http://localhost:8080`

### 3. Rodar via Maven
Configure as variáveis do PostgreSQL no `application.yml` ou exporte:
```bash
export DB_HOST=localhost
export DB_PORT=5432
export DB_NAME=manicure_db
export DB_USER=postgres
export DB_PASSWORD=sua_senha
export JWT_SECRET=chave_secreta_jwt_minimo_256_bits

./mvnw spring-boot:run
```

---

## 🔐 Autenticação & Swagger

1. Acesse o Swagger UI: `http://localhost:8080/swagger-ui/index.html`
2. Crie um salão em `POST /tenants/signup` ou autentique-se em `POST /login`.
3. Copie o token JWT retornado.
4. Clique no botão **`Authorize 🔒`** no Swagger, informe `Bearer SEU_TOKEN` e teste os endpoints protegidos por Tenant!

---

## 👨‍💻 Autor & Manutenção

**David Silva** — Desenvolvedor Backend Java / Spring Boot  
- GitHub: [@Davidds5](https://github.com/Davidds5)  
- LinkedIn: [David Silva](https://www.linkedin.com/in/david-silva-17b2882bb)