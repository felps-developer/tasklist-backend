# Tasklist Backend - Sistema TODO List Multi-usuário

## Visão Geral da Arquitetura

Este projeto implementa uma API RESTful para gerenciamento de tarefas (TODO List) multi-usuário, seguindo os princípios de **Arquitetura em Camadas (Layered Architecture)** com **separação de interfaces e implementações**.

A arquitetura foi projetada para garantir **alta testabilidade**, **manutenibilidade** e **escalabilidade**, seguindo rigorosamente os **princípios SOLID** em todas as camadas.

### Decisões Arquiteturais

#### Arquitetura em Camadas com Separação de Interfaces

A escolha pela Arquitetura em Camadas com separação de interfaces permite:

- **Simplicidade**: Estrutura clara e fácil de entender
- **Separação de Responsabilidades**: Cada camada tem uma responsabilidade bem definida
- **Testabilidade**: Facilita a criação de testes unitários com mocks através de interfaces
- **Manutenibilidade**: Mudanças em uma camada são isoladas
- **Flexibilidade**: Implementações podem ser trocadas sem afetar dependentes
- **Padrão Spring Boot**: Alinhada com as melhores práticas do ecossistema Spring
- **Dependency Inversion**: Controllers dependem de interfaces Service, não de implementações

#### Estrutura em Camadas

```
Controller (Presentation Layer)
    ↓ (depende de interface)
Service Interface (Business Layer Contract)
    ↓ (implementado por)
Service Implementation (Business Logic)
    ↓ (depende de interface)
Repository Interface (Data Access Layer)
    ↓ (implementado por Spring Data JPA)
Entity (Domain/Data Layer)
    ↓
Database
```

#### Princípios SOLID Aplicados

1. **Single Responsibility**: Cada classe tem uma única responsabilidade

   - `AuthService` (interface): Define contrato de autenticação
   - `AuthServiceImpl`: Implementa apenas lógica de autenticação
   - `TaskService` (interface): Define contrato de gerenciamento de tarefas
   - `TaskServiceImpl`: Implementa apenas lógica de gerenciamento de tarefas
   - `TaskListService` (interface): Define contrato de gerenciamento de listas
   - `TaskListServiceImpl`: Implementa apenas lógica de gerenciamento de listas
   - `JwtTokenProvider`: Apenas geração/validação de tokens JWT
   - `AuthController`: Apenas recebe requisições HTTP de autenticação
   - `TaskController`: Apenas recebe requisições HTTP de tarefas
   - `TaskListController`: Apenas recebe requisições HTTP de listas de tarefas

2. **Open/Closed**: Classes abertas para extensão, fechadas para modificação

   - Services podem ser estendidos sem modificar código existente
   - Novos métodos podem ser adicionados nas interfaces sem quebrar funcionalidades existentes
   - Novas implementações podem ser criadas sem alterar controllers

3. **Liskov Substitution**: Implementações respeitam contratos

   - Qualquer implementação de `AuthService` pode substituir `AuthServiceImpl`
   - Repositories podem ser substituídos por implementações alternativas
   - Services mantêm contratos consistentes através de interfaces

4. **Interface Segregation**: Interfaces específicas e coesas

   - Cada Service tem sua própria interface com métodos específicos
   - Repositories têm métodos específicos para cada entidade
   - DTOs de filtro específicos para cada entidade (`TaskFilterDTO`, `TaskListFilterDTO`)

5. **Dependency Inversion**: Dependências de abstrações
   - Controllers dependem de interfaces Service (não de implementações)
   - Services dependem de interfaces Repository (Spring Data JPA)
   - Injeção de dependências via construtor (Spring IoC)
   - Facilita criação de mocks em testes

### Fluxo de Dados

1. **Requisição HTTP** → Controller recebe e valida entrada
2. **Controller** → Chama Service Interface com dados validados
3. **Service Implementation** → Executa lógica de negócio e chama Repository
4. **Repository** → Acessa banco de dados via JPA/Hibernate
5. **Response** → Dados retornados através das camadas até o Controller

### Soft Delete

O sistema implementa **Soft Delete** para tarefas e listas de tarefas:

- Coluna `active` (BOOLEAN) nas tabelas `tasks` e `task_lists`
- Valores padrão: `true` para registros ativos
- Exclusão lógica: `active = false` ao invés de remoção física
- Benefícios:
  - Preservação de histórico
  - Possibilidade de recuperação
  - Integridade referencial mantida
  - Auditoria facilitada

## Stack Tecnológica

### Linguagem

- **Java 21**: Versão LTS com melhorias de performance e recursos modernos
  - Pattern matching, records, sealed classes
  - Melhorias no Garbage Collector (ZGC, G1)
  - Virtual Threads (Project Loom)
  - Performance otimizada para aplicações modernas

### Framework

- **Spring Boot 3.3.5**: Framework principal para desenvolvimento rápido
  - Auto-configuração inteligente
  - Embedded server (Tomcat)
  - Production-ready features
  - Suporte completo a Java 21
- **Spring Security**: Autenticação e autorização
  - Filtros de segurança configuráveis
  - Integração com JWT
  - Stateless authentication
- **Spring Data JPA**: Abstração de acesso a dados
  - Repositories automáticos
  - Queries derivadas de métodos
  - Paginação nativa
- **Spring Validation**: Validação de dados de entrada
  - Bean Validation (JSR-303)
  - Validação customizada
  - Validação em múltiplas camadas
- **Spring Boot Actuator**: Monitoramento e métricas
  - Health checks
  - Métricas de aplicação
  - Endpoints de gerenciamento

### Persistência

- **Spring Data JPA**: Abstração sobre JPA/Hibernate
  - Redução de boilerplate
  - Type-safe queries
  - Paginação e ordenação automáticas
- **Hibernate**: ORM para mapeamento objeto-relacional
  - Mapeamento automático de entidades
  - Cache de segundo nível
  - Lazy loading
  - Batch processing otimizado
- **PostgreSQL 16**: Banco de dados relacional (produção)
  - ACID compliance
  - Suporte a relacionamentos complexos
  - Full-text search
  - Performance otimizada
- **Flyway**: Controle de versão de banco de dados (migrations)
  - Migrations versionadas
  - Histórico de mudanças
  - Validação automática
  - Baseline automático
- **H2**: Banco em memória (testes)
  - Testes rápidos e isolados
  - Não requer instalação de banco
  - Compatibilidade com PostgreSQL

### Segurança

- **JWT (JSON Web Tokens)**: Autenticação stateless
  - Biblioteca: `io.jsonwebtoken:jjwt:0.12.3`
  - Access Token: 24 horas (86400000 ms)
  - Refresh Token: 7 dias (604800000 ms)
  - Assinatura HMAC SHA-256
  - Tokens seguros e escaláveis
- **BCrypt**: Hash seguro de senhas
  - Implementado via `BCryptPasswordEncoder` do Spring Security
  - Salt automático por senha
  - Proteção contra rainbow tables
  - Custo configurável

### Testes

- **JUnit 5**: Framework de testes
  - Testes unitários e de integração
  - Annotations modernas
  - Extensões flexíveis
- **Mockito**: Mocks e stubs para isolamento
  - Mocking de dependências
  - Verificação de interações
  - Spies para testes parciais
- **Spring Boot Test**: Testes de integração
  - Test context loading
  - MockMvc para testes de controllers
  - Test slices para testes específicos
- **AssertJ**: Assertions mais legíveis
  - Fluent API
  - Mensagens de erro claras
  - Assertions customizadas
- **Spring Security Test**: Testes de segurança
  - Mock de autenticação
  - Testes de autorização
  - Testes de filtros de segurança

### Documentação

- **SpringDoc OpenAPI 2.0.4**: Documentação automática da API (Swagger)
  - Geração automática de documentação
  - Interface Swagger UI interativa
  - Suporte a OpenAPI 3.0
  - Grupos de APIs organizados

### Build e Ferramentas

- **Gradle**: Gerenciador de dependências e build
  - Build incremental
  - Dependency resolution eficiente
  - Multi-projeto support
  - Wrapper incluído para portabilidade
- **Lombok**: Redução de boilerplate
  - Getters/Setters automáticos
  - Builders
  - Loggers
  - Data classes simplificadas
- **JaCoCo**: Cobertura de código (configurado)
  - Relatórios de cobertura
  - Análise de qualidade de código

### Justificativas das Escolhas

1. **Java 21**: Versão LTS com melhorias significativas de performance, pattern matching, records e outras features modernas que aumentam a produtividade e performance
2. **Spring Boot 3.3.5**: Ecossistema maduro, grande comunidade, facilita desenvolvimento rápido, suporte nativo a Java 21, versão estável e testada
3. **Arquitetura em Camadas com Interfaces**: Facilita testes, manutenção e evolução, desacopla responsabilidades, permite múltiplas implementações
4. **Spring Security**: Framework robusto e amplamente testado para autenticação/autorização, integração nativa com Spring Boot, suporte a JWT
5. **Spring Data JPA**: Reduz drasticamente o código boilerplate, facilita manutenção de queries, suporte a transações declarativas, paginação automática
6. **PostgreSQL 16**: Robusto, open-source, ACID compliance, suporte a relacionamentos complexos, extensível, performance otimizada
7. **Hibernate**: ORM maduro e poderoso, suporte a relacionamentos complexos, cache de segundo nível, batch processing
8. **Flyway**: Controle de versão de banco de dados, migrations versionadas, histórico de mudanças, validação automática, baseline automático
9. **JWT**: Stateless, escalável, adequado para APIs REST, facilita horizontal scaling, refresh tokens para melhor UX
10. **BCrypt**: Algoritmo de hash seguro, padrão da indústria, proteção contra rainbow tables, integração nativa com Spring Security
11. **JUnit 5 + Mockito**: Padrão da indústria para testes em Java, excelente suporte a testes unitários e de integração, extensível
12. **Gradle**: Build tool moderno, mais rápido que Maven, excelente suporte a multi-projetos, wrapper para portabilidade
13. **Lombok**: Reduz boilerplate significativamente, melhora legibilidade do código, amplamente adotado
14. **SpringDoc OpenAPI**: Documentação automática da API, facilita integração e testes, padrão OpenAPI 3.0
15. **Soft Delete**: Preserva histórico, permite recuperação, mantém integridade referencial, facilita auditoria

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

- Criar um container PostgreSQL 16 Alpine (leve e otimizado)
- Criar o banco de dados `tasklist_db`
- Configurar usuário `postgres` com senha `postgres`
- Expor a porta `5433` (mapeada para 5432 do container)
- Configurar healthcheck automático
- Criar rede Docker isolada

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
- `V4__Add_active_column_to_tasks_and_task_lists.sql`: Adiciona coluna `active` para soft delete

**As migrations são executadas automaticamente** quando a aplicação inicia. O Flyway:

- Valida o estado atual do banco
- Executa apenas migrations pendentes
- Mantém histórico de todas as migrations aplicadas
- Garante que o banco está na versão correta
- Faz baseline automático se necessário

**Para criar uma nova migration:**

1. Crie um arquivo SQL em `src/main/resources/db/migration/`
2. Nomeie seguindo o padrão: `V{numero}__{descricao}.sql`
3. Exemplo: `V5__Add_task_priority.sql`
4. O número deve ser sequencial e maior que o último

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
- **OpenAPI JSON**: http://localhost:8080/doc/tasklist/v3/api-documents

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
│   │   │       ├── service/                      # Camada de Negócio (Interfaces)
│   │   │       │   ├── AuthService.java          # Interface do Serviço de Autenticação
│   │   │       │   ├── TaskService.java          # Interface do Serviço de Tarefas
│   │   │       │   ├── TaskListService.java      # Interface do Serviço de Listas
│   │   │       │   └── impl/                      # Implementações dos Services
│   │   │       │       ├── AuthServiceImpl.java   # Implementação do Serviço de Autenticação
│   │   │       │       ├── TaskServiceImpl.java   # Implementação do Serviço de Tarefas
│   │   │       │       └── TaskListServiceImpl.java # Implementação do Serviço de Listas
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
│   │   │       │   ├── TaskFilterDTO.java        # DTO de Filtro de Tarefas
│   │   │       │   ├── TaskListRequest.java      # DTO de Request de Lista de Tarefas
│   │   │       │   ├── TaskListResponse.java     # DTO de Response de Lista de Tarefas
│   │   │       │   ├── TaskListFilterDTO.java    # DTO de Filtro de Listas de Tarefas
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
│   │               ├── V3__Create_task_lists_table.sql
│   │               └── V4__Add_active_column_to_tasks_and_task_lists.sql
│   └── test/
│       ├── java/                                 # Testes
│       │   └── br/com/jtech/tasklist/
│       │       ├── controller/                   # Testes de Integração
│       │       │   ├── AuthControllerIntegrationTest.java
│       │       │   ├── TaskControllerIntegrationTest.java
│       │       │   └── TaskListControllerIntegrationTest.java
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
- **DTOs**: Objetos de transferência de dados (Request/Response/Filter)
- **Validação**: Uso de `@Valid` para validação de entrada
- **Documentação**: Annotations do Swagger para documentação automática
- **Segurança**: Extração de usuário autenticado via `Authentication`
- **Paginação**: Suporte nativo via `Pageable` e `Page`

#### 2. **Service Interface (Business Layer Contract)**

- **Responsabilidade**: Definir contratos de negócio
- **Componentes**: `AuthService`, `TaskService`, `TaskListService`
- **Características**: Interfaces que definem métodos de negócio
- **Benefícios**: Desacoplamento, testabilidade, flexibilidade

#### 3. **Service Implementation (Business Logic)**

- **Responsabilidade**: Implementar lógica de negócio, orquestração de operações
- **Componentes**: `AuthServiceImpl`, `TaskServiceImpl`, `TaskListServiceImpl`
- **Características**: Transacional, valida regras de negócio
- **Segurança**: Validação de propriedade de recursos (usuário só acessa seus próprios dados)
- **Soft Delete**: Implementa exclusão lógica através da coluna `active`

#### 4. **Repository (Data Access Layer)**

- **Responsabilidade**: Acesso a dados, abstração do banco de dados
- **Componentes**: `UserRepository`, `TaskRepository`, `TaskListRepository`
- **Tecnologia**: Spring Data JPA
- **Queries**: Métodos derivados e queries customizadas quando necessário
- **Soft Delete**: Filtros automáticos para registros ativos quando aplicável

#### 5. **Entity (Domain/Data Layer)**

- **Responsabilidade**: Representação das entidades do banco de dados
- **Componentes**: `UserEntity`, `TaskEntity`, `TaskListEntity`
- **Tecnologia**: JPA/Hibernate
- **Relacionamentos**: Mapeamento de relacionamentos entre entidades
- **Soft Delete**: Campo `active` para exclusão lógica

#### 6. **DTO (Data Transfer Object)**

- **Responsabilidade**: Transferência de dados entre camadas
- **Separação**: Request DTOs (entrada), Response DTOs (saída), Filter DTOs (filtros)
- **Segurança**: Evita exposição de dados sensíveis das entidades
- **Validação**: Validações específicas para entrada (`@Valid`, `@NotNull`, etc.)
- **Filtros**: DTOs específicos para filtros e paginação (`TaskFilterDTO`, `TaskListFilterDTO`)

#### 7. **Config (Infrastructure)**

- **Security**: Configurações de autenticação/autorização
- **Handlers**: Tratamento centralizado de exceções
- **Swagger**: Documentação da API
- **Utils**: Utilitários diversos (geração de ID, JSON, eventos)

## Decisões Técnicas Aprofundadas

### 1. Arquitetura em Camadas com Separação de Interfaces vs Arquitetura Hexagonal

**Escolha**: Arquitetura em Camadas com separação de interfaces e implementações

**Justificativa**:

- **Simplicidade**: Estrutura mais simples que Hexagonal, mas mais organizada que sem interfaces
- **Padrão Spring Boot**: Alinhada com as melhores práticas do ecossistema Spring
- **Produtividade**: Desenvolvimento mais rápido que Hexagonal, mas mantém flexibilidade
- **Testabilidade**: Facilita criação de testes unitários com mocks através de interfaces
- **Manutenibilidade**: Separação clara de responsabilidades entre camadas
- **Dependency Inversion**: Controllers dependem de abstrações (interfaces), não de implementações
- **Flexibilidade**: Permite trocar implementações sem afetar dependentes
- **Adequação ao Projeto**: Atende perfeitamente aos requisitos do projeto com complexidade adequada

**Alternativa Considerada**: Arquitetura Hexagonal foi descartada por adicionar complexidade desnecessária para o escopo atual do projeto. A separação de interfaces já fornece os benefícios principais sem a complexidade adicional.

### 2. JWT vs Session-based Authentication

**Escolha**: JWT (JSON Web Tokens)

**Justificativa**:

- **Stateless**: Não requer armazenamento de sessão no servidor
- **Escalabilidade**: Facilita horizontal scaling (múltiplas instâncias)
- **Mobile-friendly**: Adequado para APIs REST consumidas por mobile
- **Refresh Tokens**: Implementado para melhor segurança e experiência do usuário
- **Performance**: Menos consultas ao banco de dados para validação de sessão
- **Microservices**: Adequado para arquiteturas de microserviços

**Desvantagens Consideradas**:

- Tokens não podem ser revogados facilmente (mitigado com expiração curta do access token e refresh token)
- Tamanho maior que cookies de sessão (aceitável para APIs REST)
- Payload limitado (suficiente para informações básicas do usuário)

### 3. BCrypt para Hash de Senhas

**Justificativa**:

- **Segurança**: Algoritmo de hash unidirecional seguro
- **Salt automático**: Gera salt único para cada senha automaticamente
- **Lentidão intencional**: Protege contra ataques de força bruta
- **Padrão da indústria**: Amplamente utilizado e testado
- **Spring Security**: Integração nativa com `BCryptPasswordEncoder`
- **Custo configurável**: Permite ajustar o custo computacional conforme necessário

**Alternativas Consideradas**:

- Argon2: Mais moderno, mas BCrypt é suficiente e mais amplamente suportado
- SHA-256: Não é adequado para senhas (rápido demais, sem salt adequado)
- PBKDF2: Similar ao BCrypt, mas BCrypt é mais comum no ecossistema Spring

### 4. Spring Data JPA vs JDBC Direto

**Escolha**: Spring Data JPA

**Justificativa**:

- **Produtividade**: Reduz código boilerplate significativamente
- **Abstração**: Facilita troca de banco de dados (se necessário)
- **Type-safe queries**: Métodos de query gerados automaticamente
- **Transações**: Gerenciamento automático de transações
- **Manutenibilidade**: Queries mais legíveis e fáceis de manter
- **Paginação**: Suporte nativo a paginação e ordenação
- **Soft Delete**: Facilita implementação de soft delete através de queries customizadas

**Desvantagens Consideradas**:

- Menos controle sobre SQL gerado (aceitável para a maioria dos casos)
- Curva de aprendizado inicial (compensada pela produtividade)
- Overhead mínimo em comparação com JDBC direto (aceitável para a maioria dos casos)

### 5. Flyway para Migrations

**Justificativa**:

- **Versionamento**: Controle de versão do banco de dados
- **Histórico**: Rastreabilidade de todas as mudanças no schema
- **Colaboração**: Migrations versionadas facilitam trabalho em equipe
- **Produção**: Migrations aplicadas automaticamente em deploy
- **Validação**: Flyway valida o estado do banco antes de executar migrations
- **Baseline**: Suporte a baseline automático para bancos existentes
- **Rollback**: Possibilidade de criar migrations de rollback quando necessário
- **SQL Puro**: Sintaxe SQL nativa, sem abstrações adicionais

**Alternativas Consideradas**:

- Liquibase: Similar ao Flyway, mas Flyway tem sintaxe SQL mais simples e direta
- Scripts manuais: Sem versionamento e propenso a erros
- JPA DDL Auto: Não oferece controle de versão adequado para produção

### 6. Separação de Interfaces e Implementações nos Services

**Justificativa**:

- **Dependency Inversion**: Controllers dependem de abstrações (interfaces), não de implementações
- **Testabilidade**: Facilita criação de mocks nos testes (Mockito trabalha melhor com interfaces)
- **Flexibilidade**: Permite trocar implementações sem afetar dependentes
- **SOLID**: Respeita o princípio de Dependency Inversion
- **Manutenibilidade**: Facilita adicionar novas implementações (ex: cache, logging)
- **Documentação**: Interfaces servem como documentação clara dos contratos

**Quando Considerar Implementação Direta**:

- Projetos muito pequenos onde a separação adiciona complexidade desnecessária
- Quando não há necessidade de múltiplas implementações

### 7. Soft Delete vs Hard Delete

**Escolha**: Soft Delete

**Justificativa**:

- **Preservação de Histórico**: Dados não são perdidos permanentemente
- **Recuperação**: Possibilidade de recuperar dados acidentalmente deletados
- **Integridade Referencial**: Mantém relacionamentos mesmo após "exclusão"
- **Auditoria**: Facilita rastreamento de mudanças e histórico
- **Compliance**: Atende requisitos de retenção de dados
- **Performance**: Evita problemas de cascata em exclusões
- **Implementação Simples**: Apenas uma coluna `active` (BOOLEAN)

**Desvantagens Consideradas**:

- Espaço adicional no banco (aceitável para a maioria dos casos)
- Necessidade de filtrar registros inativos nas queries (mitigado com índices e queries otimizadas)
- Complexidade adicional nas queries (compensada pelos benefícios)

### 8. Exception Handling Centralizado

**Justificativa**:

- **Consistência**: Respostas de erro padronizadas em toda a API
- **Manutenibilidade**: Um único ponto para tratamento de exceções
- **Logging**: Facilita logging centralizado de erros
- **UX**: Mensagens de erro claras e consistentes para o cliente
- **Segurança**: Evita vazamento de informações sensíveis em erros
- **Código Limpo**: Evita try-catch repetitivo em controllers e services

**Implementação**: `GlobalExceptionHandler` com `@ControllerAdvice` captura todas as exceções e retorna respostas padronizadas no formato `ApiError`.

### 9. Validação de Propriedade em Tasks e TaskLists

**Justificativa**:

- **Segurança**: Usuário só acessa suas próprias tarefas e listas
- **Implementação**: Validação em todas as operações (GET, PUT, DELETE)
- **Query**: Uso de métodos como `findByIdAndUser_Id` para garantir propriedade
- **Prevenção de Ataques**: Protege contra acesso não autorizado a recursos de outros usuários
- **Autorização**: Implementa autorização baseada em recursos (RBAC)
- **Multi-tenancy**: Base para futura implementação de multi-tenancy

**Exemplo**: Um usuário não pode acessar, modificar ou deletar tarefas de outro usuário, mesmo conhecendo o ID.

### 10. Inversão de Dependência via Interfaces Service

**Justificativa**:

- **Spring Data JPA**: Repositories são interfaces, já implementam Dependency Inversion
- **Services**: Interfaces Service também implementam Dependency Inversion
- **Testabilidade**: Facilita criação de mocks nos testes (Mockito)
- **Simplicidade**: Não precisa de camada adicional de Ports/Adapters
- **SOLID**: Respeita o princípio de Dependency Inversion através das interfaces
- **Spring IoC**: Injeção de dependências via construtor garante baixo acoplamento

### 11. Separação entre Request/Response DTOs e Entities

**Justificativa**:

- **Segurança**: Evita exposição de dados internos das entidades (ex: senha, IDs internos)
- **Flexibilidade**: Permite evoluir API sem impactar estrutura de dados
- **Validação**: DTOs têm validações específicas para entrada (`@Valid`, `@NotNull`, etc.)
- **Versionamento**: Facilita versionamento da API
- **Performance**: Controle sobre quais campos são serializados
- **Desacoplamento**: API pode evoluir independentemente do modelo de dados
- **Filtros**: DTOs específicos para filtros (`TaskFilterDTO`, `TaskListFilterDTO`) facilitam validação e uso

**Exemplo**: `UserEntity` tem campo `password`, mas `UserResponse` não expõe esse campo.

### 12. Testes com H2 em Memória

**Justificativa**:

- **Performance**: Testes mais rápidos que com banco real
- **Isolamento**: Cada teste roda em ambiente limpo
- **Portabilidade**: Não requer instalação de PostgreSQL para testes
- **CI/CD**: Facilita execução em pipelines de CI/CD
- **Paralelização**: Múltiplos testes podem rodar simultaneamente
- **Custo**: Não requer recursos adicionais de banco de dados

**Limitações Aceitas**:

- Diferenças sutis de SQL entre H2 e PostgreSQL (mitigado com testes de integração quando necessário)
- Funcionalidades específicas do PostgreSQL não testáveis (aceitável para a maioria dos casos)
- Testes de integração podem usar PostgreSQL real quando necessário

### 13. Paginação com PageResponse e Filter DTOs

**Justificativa**:

- **Performance**: Evita carregar todos os registros de uma vez
- **Experiência do Usuário**: Melhor para grandes volumes de dados
- **Escalabilidade**: Reduz uso de memória e tempo de resposta
- **Padrão REST**: Alinhado com melhores práticas de APIs REST
- **Filtros**: DTOs específicos (`TaskFilterDTO`, `TaskListFilterDTO`) facilitam validação e uso
- **Flexibilidade**: Permite combinar paginação com filtros

**Implementação**: Uso de `Pageable` do Spring Data com DTO customizado `PageResponse` para resposta padronizada e DTOs de filtro para validação e organização.

### 14. SpringDoc OpenAPI para Documentação

**Justificativa**:

- **Documentação Automática**: Geração automática a partir de annotations
- **Interface Interativa**: Swagger UI permite testar a API diretamente
- **Manutenibilidade**: Documentação sempre sincronizada com código
- **Colaboração**: Facilita integração com frontend e outros consumidores
- **Padrão**: OpenAPI 3.0 é padrão da indústria
- **Grupos**: Permite organizar APIs em grupos lógicos

### 15. Docker Compose para Desenvolvimento

**Justificativa**:

- **Simplicidade**: Setup rápido e fácil do ambiente de desenvolvimento
- **Consistência**: Ambiente idêntico para todos os desenvolvedores
- **Isolamento**: Não interfere com instalações locais do PostgreSQL
- **Portabilidade**: Funciona em qualquer sistema com Docker
- **Healthcheck**: Verificação automática de saúde do container
- **Volumes**: Persistência de dados entre reinicializações

## Melhorias e Roadmap

### Curto Prazo

1. **Endpoint de Refresh Token**: Implementar renovação de tokens JWT

   - Endpoint `/api/v1/auth/refresh` para renovar access token
   - Validação de refresh token
   - Blacklist de tokens revogados (opcional, usando Redis)
   - Rotação de refresh tokens para maior segurança

2. **Paginação Completa**: Expandir paginação para todos os endpoints de listagem

   - Parâmetros de ordenação customizáveis
   - Filtros avançados (data, status, prioridade)
   - Suporte a múltiplos critérios de ordenação

3. **Filtros Avançados**: Permitir filtrar tarefas e listas por múltiplos critérios

   - Filtro por status (completa/pendente)
   - Filtro por data (criação, conclusão, vencimento)
   - Filtro por prioridade (quando implementada)
   - Busca por texto (título, descrição)
   - Filtros combinados (AND/OR)

4. **Validação de Email Robusta**: Adicionar validação de formato de email mais robusta

   - Validação de formato com regex
   - Verificação de domínio válido (opcional)
   - Prevenção de emails temporários
   - Validação de MX records (opcional)

5. **Logging Estruturado**: Implementar logs estruturados para auditoria

   - Logs em formato JSON
   - Rastreamento de requisições (correlation ID)
   - Logs de auditoria para operações críticas
   - Integração com ferramentas de log aggregation (ELK, Splunk)

6. **Testes de Integração Completos**: Expandir cobertura de testes

   - Testes de integração para todos os controllers
   - Testes de segurança (autorização, autenticação)
   - Testes de performance básicos
   - Testes de soft delete
   - Cobertura mínima de 80%

7. **Documentação de API Melhorada**: Expandir documentação Swagger
   - Exemplos de requisições e respostas
   - Descrições detalhadas de cada endpoint
   - Códigos de erro documentados
   - Schemas de validação

### Médio Prazo

1. **Cache**: Implementar cache (Redis) para melhorar performance

   - Cache de consultas frequentes (listas de tarefas)
   - Cache de informações de usuário
   - Invalidação inteligente de cache
   - Cache distribuído para múltiplas instâncias

2. **Rate Limiting**: Proteção contra abuso da API

   - Implementação com Bucket4j ou Spring Cloud Gateway
   - Limites por usuário e por IP
   - Diferentes limites para diferentes endpoints
   - Headers de rate limit nas respostas

3. **WebSockets**: Notificações em tempo real

   - Notificações de atualizações de tarefas
   - Notificações de colaboração (quando implementada)
   - Integração com Spring WebSocket
   - Suporte a múltiplos clientes por usuário

4. **Upload de Arquivos**: Anexar arquivos às tarefas

   - Armazenamento em S3 ou sistema de arquivos
   - Validação de tipos e tamanhos
   - Gerenciamento de ciclo de vida de arquivos
   - Compressão de imagens

5. **Categorias/Tags**: Organização de tarefas

   - Sistema de categorias hierárquicas
   - Sistema de tags flexível
   - Filtros por categoria/tag
   - Autocomplete de tags

6. **Busca Full-Text**: Implementar busca avançada

   - PostgreSQL Full-Text Search para busca rápida
   - Busca em título, descrição e tags
   - Sugestões de busca
   - Highlighting de resultados

7. **Auditoria Completa**: Log de todas as operações

   - Tabela de auditoria para rastreabilidade
   - Histórico de mudanças em tarefas
   - Logs de acesso e modificações
   - Quem fez o quê e quando

8. **Prioridades e Prazos**: Sistema de priorização

   - Níveis de prioridade (baixa, média, alta, urgente)
   - Prazos e lembretes
   - Notificações de tarefas próximas do prazo
   - Visualização por prioridade

9. **Exportação de Dados**: Permitir exportar tarefas
   - Exportação em JSON, CSV, PDF
   - Filtros na exportação
   - Agendamento de exportações
   - Histórico de exportações

### Longo Prazo

1. **Microserviços**: Dividir em serviços menores se necessário

   - Auth Service (autenticação isolada)
   - Task Service (gerenciamento de tarefas)
   - Notification Service (notificações)
   - API Gateway para roteamento
   - Service Discovery

2. **Event Sourcing**: Para auditoria completa

   - Reconstrução de estado a partir de eventos
   - Histórico completo de todas as mudanças
   - Possibilidade de "viajar no tempo"
   - Event store para persistência

3. **CQRS**: Separação entre comandos e queries

   - Otimização de leitura e escrita separadamente
   - Read models otimizados para consultas
   - Write models focados em consistência
   - Eventual consistency quando apropriado

4. **GraphQL**: Alternativa ao REST

   - Queries flexíveis do cliente
   - Redução de over-fetching e under-fetching
   - Schema tipado e documentação automática
   - Integração com Spring GraphQL

5. **Internacionalização (i18n)**: Suporte a múltiplos idiomas

   - Mensagens de erro em múltiplos idiomas
   - Validações localizadas
   - Suporte a timezones
   - Formatação de datas e números

6. **Analytics e Métricas**: Dashboard de métricas

   - Integração com ferramentas de BI
   - Métricas de uso da API
   - Relatórios de produtividade do usuário
   - Dashboards personalizados

7. **Multi-tenancy**: Suporte a múltiplos tenants/organizações

   - Isolamento de dados por organização
   - Roles e permissões por tenant
   - Billing por organização
   - Onboarding de novos tenants

8. **API Gateway**: Centralizar funcionalidades

   - Autenticação centralizada
   - Rate limiting global
   - Roteamento inteligente
   - Load balancing
   - Circuit breakers

9. **Observabilidade**: Monitoramento completo

   - Distributed tracing (Jaeger, Zipkin)
   - Métricas com Prometheus
   - Logs centralizados (ELK Stack)
   - Alertas proativos
   - APM (Application Performance Monitoring)

10. **Testes de Carga**: Validação de performance

    - Testes com JMeter ou Gatling
    - Identificação de gargalos
    - Otimização baseada em dados reais
    - Benchmarks regulares

11. **CI/CD Completo**: Automação de deploy

    - Pipeline de CI/CD com GitHub Actions ou Jenkins
    - Testes automáticos em cada commit
    - Deploy automático em ambientes de staging
    - Deploy blue-green ou canary em produção
    - Rollback automático em caso de falhas

12. **Segurança Avançada**: Melhorias de segurança
    - OAuth2/OIDC para autenticação
    - 2FA (Two-Factor Authentication)
    - Análise de vulnerabilidades automatizada
    - Security headers configurados
    - WAF (Web Application Firewall)
