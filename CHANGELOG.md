# Changelog — Correções Críticas BelasUnhas (P0, P1, P2, P3)

Data: 23/09/2026

## 🚀 Resumo das Entregas

Todas as 4 prioridades do relatório de auditoria foram implementadas no backend (Java 21 + Spring Boot 3) e frontend (Next.js 16), validadas com 49 testes automatizados locais (BUILD SUCCESS) e testadas ponta a ponta com requisições HTTP reais no ambiente de produção do Render.

---

### [P0] Eliminação do Falso Sucesso no Agendamento Público
- **Novo Endpoint Público:** Criado `POST /api/v1/appointments/public` que recebe `clientName`, `clientEmail`, `clientPhone`, `professionalId`, `serviceId`, `dateTime`, `tenantSlug` e `notes`.
- **Cadastro Automático sem Senha:** O cliente é reutilizado ou cadastrado dinamicamente pelo vínculo de e-mail/telefone com o tenant. Para respeitar a constraint `NOT NULL` de senha do banco sem exigir que a cliente digite senha no portal, é gerado um hash seguro via `BCryptPasswordEncoder`.
- **Eliminação de Fallback Perigoso:** Removido o fallback que forçava `clientId = 1` no catch. Se o cliente ou serviço não existir, a requisição retorna o erro real (HTTP 400/404) e é exibida diretamente na tela.
- **Ajuste de Campo:** Corrigido envio de `appointmentDateTime` para `dateTime`.
- **Validação com Evidência:** Agendamentos de teste criados com sucesso retornando HTTP 201 Created (IDs gerados e clientes vinculados dinamicamente no banco).

---

### [P1] Conflito de Horário por Intervalo e Slots Ocupados
- **Sobreposição de Intervalo:** `validateTimeConflict` em [AppointmentService.java](file:///c:/Users/david/Desktop/itens/dev/manicure_api-main/manicure_api-main/src/main/java/br/com/davidds5/manicure_api/service/AppointmentService.java) agora calcula a duração do serviço (`duration`) e checa sobreposição de intervalos:
  $$\text{newStart} < \text{existingEnd} \quad \land \quad \text{newEnd} > \text{existingStart}$$
- **Cancelamentos Ignorados:** Agendamentos com status `CANCELLED` são sumariamente ignorados na checagem de conflitos, liberando os horários automaticamente.
- **Novo Endpoint de Slots:** Criado `GET /api/v1/appointments/occupied-slots` (por `professionalId` e `date`), exposto publicamente para o portal de agendamento.
- **Remoção de Horários Hardcoded:** A lista fixa de 8 horários no portal público foi substituída por geração dinâmica de slots (08:00 às 18:30 a cada 30 minutos). Os horários conflitantes ou no passado são riscados (`line-through`) e desabilitados com a tag "Ocupado".
- **Validação com Evidência:** Tentativa de agendamento sobreposto (14:20 quando já existia 14:00-14:45) foi bloqueada com HTTP 400 (`"Horário ocupado"`). O endpoint `/occupied-slots` retornou o intervalo ocupado com status 200.

---

### [P2] Suporte a Chave Pix Real por Tenant
- **Migration Flyway V17:** Criado `V17__add_pix_key_to_tenants.sql` adicionando a coluna `pix_key VARCHAR(150)` na tabela `tenants`.
- **Entidades e DTOs:** Coluna `pixKey` adicionada em [TenantEntity.java](file:///c:/Users/david/Desktop/itens/dev/manicure_api-main/manicure_api-main/src/main/java/br/com/davidds5/manicure_api/entity/TenantEntity.java), [TenantUpdateDTO.java](file:///c:/Users/david/Desktop/itens/dev/manicure_api-main/manicure_api-main/src/main/java/br/com/davidds5/manicure_api/dto/TenantUpdateDTO.java), [TenantDetailsDTO.java](file:///c:/Users/david/Desktop/itens/dev/manicure_api-main/manicure_api-main/src/main/java/br/com/davidds5/manicure_api/dto/TenantDetailsDTO.java) e [TenantResponseDTO.java](file:///c:/Users/david/Desktop/itens/dev/manicure_api-main/manicure_api-main/src/main/java/br/com/davidds5/manicure_api/dto/TenantResponseDTO.java).
- **Configurações do Salão:** O campo de Chave Pix em `/dashboard/configuracoes` agora persiste de verdade no banco via `PATCH /tenants/me`.
- **Endpoint Público de Tenant:** Criado `GET /tenants/public/{slug}` para consultar branding e Chave Pix pública do salão.
- **QR Code Dinâmico:** `/agendar/[slug]` consome a Chave Pix real do salão para gerar o QR Code e código Copia e Cola. Caso o salão não tenha cadastrado Pix, o portal orienta o pagamento presencial.
- **Validação com Evidência:** Chave Pix cadastrada via `PATCH /tenants/me` retornou HTTP 200 e foi confirmada via `GET /tenants/public/studio-bella`.

---

### [P3] Remoção de Fallback Inseguro no Dashboard
- **Eliminação de Mock Fixo:** Removido o mock que forçava dados do "Studio Bella Nails (id: 1)" quando `/tenants/me` falhava.
- **Resiliência e Retry:** Adicionado mecanismo de retry automático (3 tentativas com intervalo de 1.5s).
- **Tela de Erro Real:** Se a falha persistir, o usuário visualiza uma tela limpa de erro com botões de "Tentar novamente" e "Sair da conta", impedindo o vazamento de dados de outro tenant.

---

## 🧪 Evidências de Testes
- **Suíte de Testes Unitários:** 49 testes executados e aprovados via Maven (`BUILD SUCCESS`).
- **Build Frontend Next.js 16:** Compilação Turbopack concluída com sucesso (0 erros de tipagem TypeScript).
- **Testes Ponta a Ponta Live:** Script automatizado executou todas as chamadas contra o Render e validou respostas HTTP 201, 400 e 200.
