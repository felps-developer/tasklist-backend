# Tasklist Backend - Sistema TODO List Multi-usuário

![Jtech Logo](http://www.jtech.com.br/wp-content/uploads/2015/06/logo.png)

## Visão Geral da Arquitetura

Este projeto implementa uma API RESTful para gerenciamento de tarefas (TODO List) multi-usuário, seguindo os princípios de **Arquitetura em Camadas (Layered Architecture)**.

A arquitetura foi projetada para garantir **alta testabilidade**, **manutenibilidade** e **escalabilidade**, seguindo rigorosamente os **princípios SOLID** em todas as camadas.

### Decisões Arquiteturais

#### Arquitetura em Camadas

A escolha pela Arquitetura em Camadas permite:

- **Simplicidade**: Estrutura clara e fácil de entender
- **Separação de Responsabilidades**: Cada camada tem uma responsabilidade bem definida
- **Testabilidade**: Facilita a criação de testes unitários com mocks
- **Manutenibilidade**: Mudanças em uma camada são isoladas
- **Padrão Spring Boot**: Alinhada com as melhores práticas do ecossistema Spring

#### Estrutura em Camadas

```
Controller (Presentation Layer)
    ↓
Service (Business Layer)
    ↓
Repository (Data Access Layer)
    ↓
Entity (Domain/Data Layer)
    ↓
Database
```

#### Princípios SOLID Aplicados

1. **Single Responsibility**: Cada classe tem uma única responsabilidade

   - `AuthService`: Apenas lógica de autenticação
   - `TaskService`: Apenas lógica de gerenciamento de tarefas
   - `TaskListService`: Apenas lógica de gerenciamento de listas de tarefas
   - `JwtTokenProvider`: Apenas geração/validação de tokens JWT
   - `AuthController`: Apenas recebe requisições HTTP de autenticação
   - `TaskController`: Apenas recebe requisições HTTP de tarefas
   - `TaskListController`: Apenas recebe requisições HTTP de listas de tarefas

2. **Open/Closed**: Classes abertas para extensão, fechadas para modificação

   - Services podem ser estendidos sem modificar código existente
   - Novos métodos podem ser adicionados sem quebrar funcionalidades existentes

3. **Liskov Substitution**: Implementações respeitam contratos

   - Repositories podem ser substituídos por implementações alternativas
   - Services mantêm contratos consistentes

4. **Interface Segregation**: Interfaces específicas e coesas

   - Repositories têm métodos específicos para cada entidade
   - Services têm responsabilidades bem definidas

5. **Dependency Inversion**: Dependências de abstrações
   - Services dependem de interfaces Repository (Spring Data JPA)
   - Controllers dependem de Services (abstrações de negócio)
   - Injeção de dependências via construtor (Spring IoC)

### Fluxo de Dados

1. **Requisição HTTP** → Controller recebe e valida entrada
2. **Controller** → Chama Service com dados validados
3. **Service** → Executa lógica de negócio e chama Repository
4. **Repository** → Acessa banco de dados via JPA/Hibernate
5. **Response** → Dados retornados através das camadas até o Controller

## Stack Tecnológica

### Linguagem

- **Java 21**: Versão LTS com melhorias de performance e recursos modernos
  - Pattern matching, records, sealed classes
  - Melhorias no Garbage Collector (ZGC, G1)
  - Virtual Threads (Project Loom)

### Framework

- **Spring Boot 3.5.5**: Framework principal para desenvolvimento rápido
  - Auto-configuração inteligente
  - Embedded server (Tomcat)
  - Production-ready features
- **Spring Security**: Autenticação e autorização
  - Filtros de segurança configuráveis
  - Integração com JWT
- **Spring Data JPA**: Abstração de acesso a dados
  - Repositories automáticos
  - Queries derivadas de métodos
- **Spring Validation**: Validação de dados de entrada
  - Bean Validation (JSR-303)
  - Validação customizada

### Persistência

- **Spring Data JPA**: Abstração sobre JPA/Hibernate
  - Redução de boilerplate
  - Type-safe queries
- **Hibernate**: ORM para mapeamento objeto-relacional
  - Mapeamento automático de entidades
  - Cache de segundo nível
  - Lazy loading
- **PostgreSQL 16**: Banco de dados relacional (produção)
  - ACID compliance
  - Suporte a relacionamentos complexos
  - Full-text search
- **Flyway**: Controle de versão de banco de dados (migrations)
  - Migrations versionadas
  - Histórico de mudanças
  - Validação automática
- **H2**: Banco em memória (testes)
  - Testes rápidos e isolados
  - Não requer instalação de banco

### Segurança

- **JWT (JSON Web Tokens)**: Autenticação stateless
  - Biblioteca: `io.jsonwebtoken:jjwt:0.12.3`
  - Access Token: 24 horas
  - Refresh Token: 7 dias
  - Assinatura HMAC SHA-256
- **BCrypt**: Hash seguro de senhas
  - Implementado via `BCryptPasswordEncoder` do Spring Security
  - Salt automático por senha
  - Proteção contra rainbow tables

### Testes

- **JUnit 5**: Framework de testes
  - Testes unitários e de integração
  - Annotations modernas
- **Mockito**: Mocks e stubs para isolamento
  - Mocking de dependências
  - Verificação de interações
- **Spring Boot Test**: Testes de integração
  - Test context loading
  - MockMvc para testes de controllers
- **AssertJ**: Assertions mais legíveis
  - Fluent API
  - Mensagens de erro claras
- **Spring Security Test**: Testes de segurança
  - Mock de autenticação
  - Testes de autorização

### Documentação

- **SpringDoc OpenAPI 2.0.4**: Documentação automática da API (Swagger)
  - Geração automática de documentação
  - Interface Swagger UI
  - Suporte a OpenAPI 3.0

### Build e Ferramentas

- **Gradle**: Gerenciador de dependências e build
  - Build incremental
  - Dependency resolution eficiente
- **Lombok**: Redução de boilerplate
  - Getters/Setters automáticos
  - Builders
  - Loggers

### Justificativas das Escolhas

1. **Java 21**: Versão LTS com melhorias significativas de performance, pattern matching, records e outras features modernas que aumentam a produtividade
2. **Spring Boot 3.5.5**: Ecossistema maduro, grande comunidade, facilita desenvolvimento rápido, suporte nativo a Java 21
3. **Arquitetura em Camadas**: Facilita testes, manutenção e evolução, desacopla responsabilidades
4. **Spring Security**: Framework robusto e amplamente testado para autenticação/autorização, integração nativa com Spring Boot
5. **Spring Data JPA**: Reduz drasticamente o código boilerplate, facilita manutenção de queries, suporte a transações declarativas
6. **PostgreSQL**: Robusto, open-source, ACID compliance, suporte a relacionamentos complexos, extensível
7. **Hibernate**: ORM maduro e poderoso, suporte a relacionamentos complexos, cache de segundo nível
8. **Flyway**: Controle de versão de banco de dados, migrations versionadas, histórico de mudanças
9. **JWT**: Stateless, escalável, adequado para APIs REST, facilita horizontal scaling
10. **BCrypt**: Algoritmo de hash seguro, padrão da indústria, proteção contra rainbow tables
11. **JUnit 5 + Mockito**: Padrão da indústria para testes em Java, excelente suporte a testes unitários e de integração
12. **Gradle**: Build tool moderno, mais rápido que Maven, excelente suporte a multi-projetos
13. **Lombok**: Reduz boilerplate significativamente, melhora legibilidade do código
14. **SpringDoc OpenAPI**: Documentação automática da API, facilita integração e testes

## Como Rodar Localmente

### Pré-requisitos

- **Java 21** ou superior
- **Docker** e **Docker Compose** (recomendado) ou **PostgreSQL 16+** instalado localmente
- **Gradle 7+** (ou usar o wrapper incluído: `gradlew`)

### Configuração do Banco de Dados

#### Opção 1: Usando Docker Compose (Recomendado)

1. Inicie o PostgreSQL usando Docker Compose:

```bash
docker-compose up -d
```

Isso irá:

- Criar um container PostgreSQL 16
- Criar o banco de dados `tasklist_db`
- Configurar usuário `postgres` com senha `postgres`
- Expor a porta `5433` (mapeada para 5432 do container)

2. Verifique se o container está rodando:

```bash
docker-compose ps
```

3. O banco de dados estará pronto para uso. As migrations do Flyway serão executadas automaticamente na primeira execução da aplicação.

#### Opção 2: PostgreSQL Local

1. Instale o PostgreSQL 16+ em sua máquina

2. Crie o banco de dados:

```sql
CREATE DATABASE tasklist_db;
CREATE USER postgres WITH PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE tasklist_db TO postgres;
```

3. Configure as variáveis de ambiente ou edite `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/tasklist_db
    username: postgres
    password: postgres
```

### Migrations do Flyway

O projeto utiliza **Flyway** para controle de versão do banco de dados. As migrations estão localizadas em `src/main/resources/db/migration/`:

- `V1__Create_users_table.sql`: Cria a tabela de usuários
- `V2__Create_tasks_table.sql`: Cria a tabela de tarefas com relacionamento
- `V3__Create_task_lists_table.sql`: Cria a tabela de listas de tarefas

**As migrations são executadas automaticamente** quando a aplicação inicia. O Flyway:

- Valida o estado atual do banco
- Executa apenas migrations pendentes
- Mantém histórico de todas as migrations aplicadas
- Garante que o banco está na versão correta

**Para criar uma nova migration:**

1. Crie um arquivo SQL em `src/main/resources/db/migration/`
2. Nomeie seguindo o padrão: `V{numero}__{descricao}.sql`
3. Exemplo: `V4__Add_task_priority.sql`

### Executando a Aplicação

**⚠️ Certifique-se de que o PostgreSQL está rodando antes de executar a aplicação!**

#### Opção 1: Via Gradle Wrapper

```bash
# Windows
.\gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

#### Opção 2: Build e Executar JAR

```bash
# Build
./gradlew build

# Executar
java -jar build/libs/tasklist-backend-{version}.jar
```

#### Opção 3: Via IDE

- Importe o projeto como projeto Gradle
- Execute a classe `StartTasklist.java`

### Variáveis de Ambiente (Opcional)

```bash
# Database
export DS_URL=localhost
export DS_PORT=5433
export DS_DATABASE=tasklist_db
export DS_USER=postgres
export DS_PASS=postgres

# JWT
export JWT_SECRET=sua_chave_secreta_minimo_256_bits
export JWT_EXPIRATION=86400000  # 24 horas em ms
export JWT_REFRESH_EXPIRATION=604800000  # 7 dias em ms

# Profile
export PROFILE=dev

# JPA
export JPA_SHOW_SQL=false  # Desabilita logs SQL em produção

# Server
export PORT=8080
```

### Parar o Banco de Dados (Docker)

```bash
# Parar o container
docker-compose down

# Parar e remover volumes (apaga dados)
docker-compose down -v
```

### Acessando a Documentação

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:8080/doc/tasklist/v1/api.html
- **API Docs**: http://localhost:8080/doc/tasklist/v3/api-documents

## Como Rodar os Testes

### Todos os Testes

```bash
./gradlew test
```

### Apenas Testes Unitários

```bash
./gradlew test --tests "*Test"
```

### Apenas Testes de Integração

```bash
./gradlew test --tests "*IntegrationTest"
```

### Com Cobertura (Jacoco)

```bash
./gradlew test jacocoTestReport
```

O relatório de cobertura será gerado em: `build/reports/jacoco/test/html/index.html`

### Executar Testes Específicos

```bash
./gradlew test --tests "br.com.jtech.tasklist.service.AuthServiceTest"
```

### Executar Testes em Modo Contínuo

```bash
./gradlew test --continuous
```

## Estrutura de Pastas Detalhada

```
tasklist-backend/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── br/com/jtech/tasklist/
│   │   │       ├── controller/                  # Camada de Apresentação
│   │   │       │   ├── AuthController.java       # Controller de Autenticação
│   │   │       │   ├── TaskController.java       # Controller de Tarefas
│   │   │       │   └── TaskListController.java   # Controller de Listas de Tarefas
│   │   │       ├── service/                      # Camada de Negócio
│   │   │       │   ├── AuthService.java          # Serviço de Autenticação
│   │   │       │   ├── TaskService.java          # Serviço de Tarefas
│   │   │       │   └── TaskListService.java      # Serviço de Listas de Tarefas
│   │   │       ├── repository/                   # Camada de Acesso a Dados
│   │   │       │   ├── UserRepository.java       # Repository de Usuários
│   │   │       │   ├── TaskRepository.java       # Repository de Tarefas
│   │   │       │   └── TaskListRepository.java   # Repository de Listas de Tarefas
│   │   │       ├── entity/                       # Entidades JPA
│   │   │       │   ├── UserEntity.java           # Entidade Usuário
│   │   │       │   ├── TaskEntity.java           # Entidade Tarefa
│   │   │       │   └── TaskListEntity.java       # Entidade Lista de Tarefas
│   │   │       ├── dto/                          # Data Transfer Objects
│   │   │       │   ├── AuthRequest.java          # DTO de Request de Autenticação
│   │   │       │   ├── AuthResponse.java         # DTO de Response de Autenticação
│   │   │       │   ├── RegisterRequest.java      # DTO de Request de Registro
│   │   │       │   ├── TaskRequest.java          # DTO de Request de Tarefa
│   │   │       │   ├── TaskResponse.java         # DTO de Response de Tarefa
│   │   │       │   ├── TaskListRequest.java      # DTO de Request de Lista de Tarefas
│   │   │       │   ├── TaskListResponse.java     # DTO de Response de Lista de Tarefas
│   │   │       │   ├── UserResponse.java         # DTO de Response de Usuário
│   │   │       │   └── PageResponse.java         # DTO de Response de Paginação
│   │   │       ├── config/                       # Configurações
│   │   │       │   └── infra/
│   │   │       │       ├── handlers/             # Exception Handlers
│   │   │       │       │   └── GlobalExceptionHandler.java
│   │   │       │       ├── exceptions/           # Exceções Customizadas
│   │   │       │       │   ├── ApiError.java
│   │   │       │       │   ├── ApiSubError.java
│   │   │       │       │   ├── ApiValidationError.java
│   │   │       │       │   ├── ResourceNotFoundException.java
│   │   │       │       │   └── UnauthorizedException.java
│   │   │       │       ├── security/             # Configurações de Segurança
│   │   │       │       │   ├── SecurityConfig.java
│   │   │       │       │   ├── JwtTokenProvider.java
│   │   │       │       │   ├── JwtAuthenticationFilter.java
│   │   │       │       │   └── UserDetailsServiceImpl.java
│   │   │       │       ├── swagger/              # Configuração Swagger
│   │   │       │       │   └── OpenAPI30Configuration.java
│   │   │       │       └── utils/                # Utilitários
│   │   │       │           ├── GenId.java
│   │   │       │           ├── Jsons.java
│   │   │       │           └── ReadyEventListener.java
│   │   │       └── StartTasklist.java           # Classe Principal
│   │   └── resources/
│   │       ├── application.yml                   # Configurações da Aplicação
│   │       ├── banner.txt                        # Banner da aplicação
│   │       └── db/
│   │           └── migration/                    # Migrations do Flyway
│   │               ├── V1__Create_users_table.sql
│   │               ├── V2__Create_tasks_table.sql
│   │               └── V3__Create_task_lists_table.sql
│   └── test/
│       ├── java/                                 # Testes
│       │   └── br/com/jtech/tasklist/
│       │       ├── controller/                   # Testes de Integração
│       │       │   ├── AuthControllerIntegrationTest.java
│       │       │   └── TaskControllerIntegrationTest.java
│       │       └── service/                      # Testes Unitários
│       │           ├── AuthServiceTest.java
│       │           └── TaskServiceTest.java
│       └── resources/
│           └── application-test.properties        # Configurações para Testes
├── build.gradle                                   # Configuração do Gradle
├── settings.gradle                                # Configuração do projeto
├── gradle.properties                              # Propriedades do Gradle
├── docker-compose.yml                             # Configuração Docker Compose
└── README.md                                      # Este arquivo
```

### Descrição das Camadas

#### 1. **Controller (Presentation Layer)**

- **Responsabilidade**: Receber requisições HTTP, validar entrada, chamar Services
- **Componentes**: `AuthController`, `TaskController`, `TaskListController`
- **DTOs**: Objetos de transferência de dados (Request/Response)
- **Validação**: Uso de `@Valid` para validação de entrada
- **Documentação**: Annotations do Swagger para documentação automática

#### 2. **Service (Business Layer)**

- **Responsabilidade**: Lógica de negócio, orquestração de operações
- **Componentes**: `AuthService`, `TaskService`, `TaskListService`
- **Características**: Transacional, valida regras de negócio
- **Segurança**: Validação de propriedade de recursos (usuário só acessa seus próprios dados)

#### 3. **Repository (Data Access Layer)**

- **Responsabilidade**: Acesso a dados, abstração do banco de dados
- **Componentes**: `UserRepository`, `TaskRepository`, `TaskListRepository`
- **Tecnologia**: Spring Data JPA
- **Queries**: Métodos derivados e queries customizadas quando necessário

#### 4. **Entity (Domain/Data Layer)**

- **Responsabilidade**: Representação das entidades do banco de dados
- **Componentes**: `UserEntity`, `TaskEntity`, `TaskListEntity`
- **Tecnologia**: JPA/Hibernate
- **Relacionamentos**: Mapeamento de relacionamentos entre entidades

#### 5. **DTO (Data Transfer Object)**

- **Responsabilidade**: Transferência de dados entre camadas
- **Separação**: Request DTOs (entrada) e Response DTOs (saída)
- **Segurança**: Evita exposição de dados sensíveis das entidades

#### 6. **Config (Infrastructure)**

- **Security**: Configurações de autenticação/autorização
- **Handlers**: Tratamento centralizado de exceções
- **Swagger**: Documentação da API
- **Utils**: Utilitários diversos (geração de ID, JSON, eventos)

## Decisões Técnicas Aprofundadas

### 1. Arquitetura em Camadas vs Arquitetura Hexagonal

**Escolha**: Arquitetura em Camadas (Layered Architecture)

**Justificativa**:

- **Simplicidade**: Estrutura mais simples e fácil de entender
- **Padrão Spring Boot**: Alinhada com as melhores práticas do ecossistema Spring
- **Produtividade**: Desenvolvimento mais rápido, menos abstrações
- **Testabilidade**: Facilita criação de testes unitários com mocks
- **Manutenibilidade**: Separação clara de responsabilidades entre camadas
- **Adequação ao Projeto**: Atende perfeitamente aos requisitos do projeto sem complexidade desnecessária

**Alternativa Considerada**: Arquitetura Hexagonal foi descartada por adicionar complexidade desnecessária para o escopo atual do projeto.

### 2. JWT vs Session-based Authentication

**Escolha**: JWT (JSON Web Tokens)

**Justificativa**:

- **Stateless**: Não requer armazenamento de sessão no servidor
- **Escalabilidade**: Facilita horizontal scaling (múltiplas instâncias)
- **Mobile-friendly**: Adequado para APIs REST consumidas por mobile
- **Refresh Tokens**: Implementado para melhor segurança e experiência do usuário
- **Performance**: Menos consultas ao banco de dados para validação de sessão

**Desvantagens Consideradas**:

- Tokens não podem ser revogados facilmente (mitigado com expiração curta do access token)
- Tamanho maior que cookies de sessão (aceitável para APIs REST)

### 3. BCrypt para Hash de Senhas

**Justificativa**:

- **Segurança**: Algoritmo de hash unidirecional seguro
- **Salt automático**: Gera salt único para cada senha automaticamente
- **Lentidão intencional**: Protege contra ataques de força bruta
- **Padrão da indústria**: Amplamente utilizado e testado
- **Spring Security**: Integração nativa com `BCryptPasswordEncoder`

**Alternativas Consideradas**:

- Argon2: Mais moderno, mas BCrypt é suficiente e mais amplamente suportado
- SHA-256: Não é adequado para senhas (rápido demais, sem salt adequado)

### 4. Spring Data JPA vs JDBC Direto

**Escolha**: Spring Data JPA

**Justificativa**:

- **Produtividade**: Reduz código boilerplate significativamente
- **Abstração**: Facilita troca de banco de dados (se necessário)
- **Type-safe queries**: Métodos de query gerados automaticamente
- **Transações**: Gerenciamento automático de transações
- **Manutenibilidade**: Queries mais legíveis e fáceis de manter

**Desvantagens Consideradas**:

- Menos controle sobre SQL gerado (aceitável para a maioria dos casos)
- Curva de aprendizado inicial (compensada pela produtividade)

### 5. Flyway para Migrations

**Justificativa**:

- **Versionamento**: Controle de versão do banco de dados
- **Histórico**: Rastreabilidade de todas as mudanças no schema
- **Colaboração**: Migrations versionadas facilitam trabalho em equipe
- **Produção**: Migrations aplicadas automaticamente em deploy
- **Validação**: Flyway valida o estado do banco antes de executar migrations
- **Rollback**: Possibilidade de criar migrations de rollback quando necessário

**Alternativas Consideradas**:

- Liquibase: Similar ao Flyway, mas Flyway tem sintaxe SQL mais simples
- Scripts manuais: Sem versionamento e propenso a erros

### 6. Uso Direto de Entities

**Justificativa**:

- **Simplicidade**: Entities JPA são usadas diretamente, sem camada de domínio adicional
- **Produtividade**: Menos código, menos conversões
- **Spring Data JPA**: Framework já abstrai bem a persistência
- **Adequação**: Para este projeto, a complexidade de separar Domain/Entity não se justifica

**Quando Considerar Domain Layer**:

- Projetos maiores com lógica de negócio complexa
- Necessidade de desacoplar completamente do framework
- Múltiplos adapters de persistência

### 7. Exception Handling Centralizado

**Justificativa**:

- **Consistência**: Respostas de erro padronizadas em toda a API
- **Manutenibilidade**: Um único ponto para tratamento de exceções
- **Logging**: Facilita logging centralizado de erros
- **UX**: Mensagens de erro claras e consistentes para o cliente
- **Segurança**: Evita vazamento de informações sensíveis em erros

**Implementação**: `GlobalExceptionHandler` com `@ControllerAdvice` captura todas as exceções e retorna respostas padronizadas.

### 8. Validação de Propriedade em Tasks e TaskLists

**Justificativa**:

- **Segurança**: Usuário só acessa suas próprias tarefas e listas
- **Implementação**: Validação em todas as operações (GET, PUT, DELETE)
- **Query**: Uso de `findByIdAndUser_Id` para garantir propriedade
- **Prevenção de Ataques**: Protege contra acesso não autorizado a recursos de outros usuários
- **Autorização**: Implementa autorização baseada em recursos (RBAC)

**Exemplo**: Um usuário não pode acessar, modificar ou deletar tarefas de outro usuário, mesmo conhecendo o ID.

### 9. Inversão de Dependência via Spring Data JPA

**Justificativa**:

- **Spring Data JPA**: Repositories são interfaces, já implementam Dependency Inversion
- **Testabilidade**: Facilita criação de mocks nos testes (Mockito)
- **Simplicidade**: Não precisa de camada adicional de Ports/Adapters
- **SOLID**: Respeita o princípio de Dependency Inversion através das interfaces Repository
- **Spring IoC**: Injeção de dependências via construtor garante baixo acoplamento

### 10. Separação entre Request/Response DTOs e Entities

**Justificativa**:

- **Segurança**: Evita exposição de dados internos das entidades (ex: senha, IDs internos)
- **Flexibilidade**: Permite evoluir API sem impactar estrutura de dados
- **Validação**: DTOs têm validações específicas para entrada (`@Valid`, `@NotNull`, etc.)
- **Versionamento**: Facilita versionamento da API
- **Performance**: Controle sobre quais campos são serializados
- **Desacoplamento**: API pode evoluir independentemente do modelo de dados

**Exemplo**: `UserEntity` tem campo `password`, mas `UserResponse` não expõe esse campo.

### 11. Testes com H2 em Memória

**Justificativa**:

- **Performance**: Testes mais rápidos que com banco real
- **Isolamento**: Cada teste roda em ambiente limpo
- **Portabilidade**: Não requer instalação de PostgreSQL para testes
- **CI/CD**: Facilita execução em pipelines de CI/CD
- **Paralelização**: Múltiplos testes podem rodar simultaneamente

**Limitações Aceitas**:

- Diferenças sutis de SQL entre H2 e PostgreSQL (mitigado com testes de integração quando necessário)
- Funcionalidades específicas do PostgreSQL não testáveis (aceitável para a maioria dos casos)

### 12. Paginação com PageResponse

**Justificativa**:

- **Performance**: Evita carregar todos os registros de uma vez
- **Experiência do Usuário**: Melhor para grandes volumes de dados
- **Escalabilidade**: Reduz uso de memória e tempo de resposta
- **Padrão REST**: Alinhado com melhores práticas de APIs REST

**Implementação**: Uso de `Pageable` do Spring Data com DTO customizado `PageResponse` para resposta padronizada.

### 13. SpringDoc OpenAPI para Documentação

**Justificativa**:

- **Documentação Automática**: Geração automática a partir de annotations
- **Interface Interativa**: Swagger UI permite testar a API diretamente
- **Manutenibilidade**: Documentação sempre sincronizada com código
- **Colaboração**: Facilita integração com frontend e outros consumidores
- **Padrão**: OpenAPI 3.0 é padrão da indústria

## Melhorias e Roadmap

### Curto Prazo

1. **Endpoint de Refresh Token**: Implementar renovação de tokens JWT

   - Endpoint `/api/v1/auth/refresh` para renovar access token
   - Validação de refresh token
   - Blacklist de tokens revogados (opcional)

2. **Paginação Completa**: Expandir paginação para todos os endpoints de listagem

   - Parâmetros de ordenação customizáveis
   - Filtros avançados (data, status, prioridade)

3. **Filtros Avançados**: Permitir filtrar tarefas e listas por múltiplos critérios

   - Filtro por status (completa/pendente)
   - Filtro por data (criação, conclusão)
   - Filtro por prioridade (quando implementada)
   - Busca por texto (título, descrição)

4. **Validação de Email Robusta**: Adicionar validação de formato de email mais robusta

   - Validação de formato com regex
   - Verificação de domínio válido (opcional)
   - Prevenção de emails temporários

5. **Logging Estruturado**: Implementar logs estruturados para auditoria

   - Logs em formato JSON
   - Rastreamento de requisições (correlation ID)
   - Logs de auditoria para operações críticas

6. **Testes de Integração Completos**: Expandir cobertura de testes
   - Testes de integração para todos os controllers
   - Testes de segurança (autorização)
   - Testes de performance básicos

### Médio Prazo

1. **Cache**: Implementar cache (Redis) para melhorar performance

   - Cache de consultas frequentes (listas de tarefas)
   - Cache de informações de usuário
   - Invalidação inteligente de cache

2. **Rate Limiting**: Proteção contra abuso da API

   - Implementação com Bucket4j ou Spring Cloud Gateway
   - Limites por usuário e por IP
   - Diferentes limites para diferentes endpoints

3. **WebSockets**: Notificações em tempo real

   - Notificações de atualizações de tarefas
   - Notificações de colaboração (quando implementada)
   - Integração com Spring WebSocket

4. **Upload de Arquivos**: Anexar arquivos às tarefas

   - Armazenamento em S3 ou sistema de arquivos
   - Validação de tipos e tamanhos
   - Gerenciamento de ciclo de vida de arquivos

5. **Categorias/Tags**: Organização de tarefas

   - Sistema de categorias hierárquicas
   - Sistema de tags flexível
   - Filtros por categoria/tag

6. **Busca Full-Text**: Implementar busca avançada

   - PostgreSQL Full-Text Search para busca rápida
   - Busca em título, descrição e tags
   - Sugestões de busca

7. **Auditoria Completa**: Log de todas as operações

   - Tabela de auditoria para rastreabilidade
   - Histórico de mudanças em tarefas
   - Logs de acesso e modificações

8. **Prioridades e Prazos**: Sistema de priorização
   - Níveis de prioridade (baixa, média, alta, urgente)
   - Prazos e lembretes
   - Notificações de tarefas próximas do prazo

### Longo Prazo

1. **Microserviços**: Dividir em serviços menores se necessário

   - Auth Service (autenticação isolada)
   - Task Service (gerenciamento de tarefas)
   - Notification Service (notificações)
   - API Gateway para roteamento

2. **Event Sourcing**: Para auditoria completa

   - Reconstrução de estado a partir de eventos
   - Histórico completo de todas as mudanças
   - Possibilidade de "viajar no tempo"

3. **CQRS**: Separação entre comandos e queries

   - Otimização de leitura e escrita separadamente
   - Read models otimizados para consultas
   - Write models focados em consistência

4. **GraphQL**: Alternativa ao REST

   - Queries flexíveis do cliente
   - Redução de over-fetching e under-fetching
   - Schema tipado e documentação automática

5. **Internacionalização (i18n)**: Suporte a múltiplos idiomas

   - Mensagens de erro em múltiplos idiomas
   - Validações localizadas
   - Suporte a timezones

6. **Analytics e Métricas**: Dashboard de métricas

   - Integração com ferramentas de BI
   - Métricas de uso da API
   - Relatórios de produtividade do usuário

7. **Multi-tenancy**: Suporte a múltiplos tenants/organizações

   - Isolamento de dados por organização
   - Roles e permissões por tenant
   - Billing por organização

8. **API Gateway**: Centralizar funcionalidades

   - Autenticação centralizada
   - Rate limiting global
   - Roteamento inteligente
   - Load balancing

9. **Observabilidade**: Monitoramento completo

   - Distributed tracing (Jaeger, Zipkin)
   - Métricas com Prometheus
   - Logs centralizados (ELK Stack)
   - Alertas proativos

10. **Testes de Carga**: Validação de performance
    - Testes com JMeter ou Gatling
    - Identificação de gargalos
    - Otimização baseada em dados reais

## Endpoints da API

### Autenticação

#### POST /api/v1/auth/register

Registra um novo usuário.

**Request:**

```json
{
  "name": "João Silva",
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Response:** 201 Created

```json
{
  "id": "uuid",
  "name": "João Silva",
  "email": "joao@example.com",
  "createdAt": "2024-01-01T00:00:00Z"
}
```

#### POST /api/v1/auth/login

Autentica um usuário e retorna tokens JWT.

**Request:**

```json
{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Response:** 200 OK

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer"
}
```

### Tarefas (Requer Autenticação)

#### POST /api/v1/tasks

Cria uma nova tarefa.

**Headers:**

```
Authorization: Bearer {accessToken}
```

**Request:**

```json
{
  "title": "Implementar feature X",
  "description": "Descrição da tarefa",
  "completed": false,
  "taskListId": "uuid"
}
```

**Response:** 201 Created

#### GET /api/v1/tasks

Lista todas as tarefas do usuário autenticado.

**Query Parameters:**

- `page`: Número da página (padrão: 0)
- `size`: Tamanho da página (padrão: 20)
- `sort`: Campo de ordenação (padrão: createdAt,desc)

**Response:** 200 OK com paginação

#### GET /api/v1/tasks/{id}

Busca uma tarefa específica por ID.

**Response:** 200 OK

#### PUT /api/v1/tasks/{id}

Atualiza uma tarefa existente.

**Request:**

```json
{
  "title": "Título atualizado",
  "description": "Descrição atualizada",
  "completed": true
}
```

**Response:** 200 OK

#### DELETE /api/v1/tasks/{id}

Remove uma tarefa.

**Response:** 204 No Content

### Listas de Tarefas (Requer Autenticação)

#### POST /api/v1/task-lists

Cria uma nova lista de tarefas.

**Request:**

```json
{
  "name": "Lista de Compras",
  "description": "Itens para comprar"
}
```

**Response:** 201 Created

#### GET /api/v1/task-lists

Lista todas as listas de tarefas do usuário autenticado.

**Query Parameters:**

- `page`: Número da página (padrão: 0)
- `size`: Tamanho da página (padrão: 20)
- `sort`: Campo de ordenação (padrão: createdAt,desc)

**Response:** 200 OK com paginação

#### GET /api/v1/task-lists/{id}

Busca uma lista de tarefas específica por ID.

**Response:** 200 OK

#### PUT /api/v1/task-lists/{id}

Atualiza uma lista de tarefas existente.

**Request:**

```json
{
  "name": "Nome atualizado",
  "description": "Descrição atualizada"
}
```

**Response:** 200 OK

#### DELETE /api/v1/task-lists/{id}

Remove uma lista de tarefas.

**Response:** 204 No Content
