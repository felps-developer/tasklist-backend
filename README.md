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
   - `JwtTokenProvider`: Apenas geração/validação de tokens JWT
   - `AuthController`: Apenas recebe requisições HTTP de autenticação
   - `TaskController`: Apenas recebe requisições HTTP de tarefas

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

## Stack Tecnológica

### Linguagem

- **Java 21**: Versão LTS com melhorias de performance e recursos modernos

### Framework

- **Spring Boot 3.5.5**: Framework principal para desenvolvimento rápido
- **Spring Security**: Autenticação e autorização
- **Spring Data JPA**: Abstração de acesso a dados
- **Spring Validation**: Validação de dados de entrada

### Persistência

- **Spring Data JPA**: Abstração sobre JPA/Hibernate
- **Hibernate**: ORM para mapeamento objeto-relacional
- **PostgreSQL**: Banco de dados relacional (produção)
- **H2**: Banco em memória (testes)

### Segurança

- **JWT (JSON Web Tokens)**: Autenticação stateless
  - Biblioteca: `io.jsonwebtoken:jjwt:0.12.3`
  - Access Token: 24 horas
  - Refresh Token: 7 dias
- **BCrypt**: Hash seguro de senhas
  - Implementado via `BCryptPasswordEncoder` do Spring Security

### Testes

- **JUnit 5**: Framework de testes
- **Mockito**: Mocks e stubs para isolamento
- **Spring Boot Test**: Testes de integração
- **AssertJ**: Assertions mais legíveis
- **Spring Security Test**: Testes de segurança

### Documentação

- **SpringDoc OpenAPI**: Documentação automática da API (Swagger)

### Build

- **Gradle**: Gerenciador de dependências e build
- **Lombok**: Redução de boilerplate

### Justificativas das Escolhas

1. **Java 21**: Versão LTS com melhorias significativas de performance, pattern matching, records e outras features modernas que aumentam a produtividade
2. **Spring Boot 3.5.5**: Ecossistema maduro, grande comunidade, facilita desenvolvimento rápido, suporte nativo a Java 21
3. **Arquitetura Hexagonal**: Facilita testes, manutenção e evolução, desacopla o domínio de frameworks
4. **Spring Security**: Framework robusto e amplamente testado para autenticação/autorização, integração nativa com Spring Boot
5. **Spring Data JPA**: Reduz drasticamente o código boilerplate, facilita manutenção de queries, suporte a transações declarativas
6. **PostgreSQL**: Robusto, open-source, ACID compliance, suporte a relacionamentos complexos, extensível
7. **JWT**: Stateless, escalável, adequado para APIs REST, facilita horizontal scaling
8. **BCrypt**: Algoritmo de hash seguro, padrão da indústria, proteção contra rainbow tables
9. **JUnit 5 + Mockito**: Padrão da indústria para testes em Java, excelente suporte a testes unitários e de integração
10. **Gradle**: Build tool moderno, mais rápido que Maven, excelente suporte a multi-projetos
11. **Lombok**: Reduz boilerplate significativamente, melhora legibilidade do código
12. **SpringDoc OpenAPI**: Documentação automática da API, facilita integração e testes

## Como Rodar Localmente

### Pré-requisitos

- Java 21 ou superior
- PostgreSQL 12+ (ou Docker)
- Gradle 7+ (ou usar o wrapper incluído)

### Configuração do Banco de Dados

1. Crie um banco de dados PostgreSQL:

```sql
CREATE DATABASE tasklist_db;
```

2. Configure as variáveis de ambiente ou edite `application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/tasklist_db
    username: seu_usuario
    password: sua_senha
```

### Executando a Aplicação

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
export DS_PORT=5432
export DS_DATABASE=tasklist_db
export DS_USER=postgres
export DS_PASS=postgres

# JWT
export JWT_SECRET=sua_chave_secreta_minimo_256_bits
export JWT_EXPIRATION=86400000  # 24 horas em ms
export JWT_REFRESH_EXPIRATION=604800000  # 7 dias em ms

# Profile
export PROFILE=dev
```

### Acessando a Documentação

Após iniciar a aplicação, acesse:

- **Swagger UI**: http://localhost:{port}/doc/tasklist/v1/api.html
- **API Docs**: http://localhost:{port}/doc/tasklist/v3/api-documents

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
./gradlew test --tests "br.com.jtech.tasklist.application.core.usecases.AuthUseCaseTest"
```

## Estrutura de Pastas Detalhada

```
src/
├── main/
│   ├── java/
│   │   └── br/com/jtech/tasklist/
│   │       ├── controller/                  # Camada de Apresentação
│   │       │   ├── AuthController.java     # Controller de Autenticação
│   │       │   └── TaskController.java     # Controller de Tarefas
│   │       ├── service/                     # Camada de Negócio
│   │       │   ├── AuthService.java        # Serviço de Autenticação
│   │       │   └── TaskService.java        # Serviço de Tarefas
│   │       ├── repository/                  # Camada de Acesso a Dados
│   │       │   ├── UserRepository.java     # Repository de Usuários
│   │       │   └── TaskRepository.java     # Repository de Tarefas
│   │       ├── entity/                      # Entidades JPA
│   │       │   ├── UserEntity.java         # Entidade Usuário
│   │       │   └── TaskEntity.java         # Entidade Tarefa
│   │       ├── dto/                         # Data Transfer Objects
│   │       │   ├── AuthRequest.java         # DTO de Request de Autenticação
│   │       │   ├── AuthResponse.java        # DTO de Response de Autenticação
│   │       │   ├── RegisterRequest.java    # DTO de Request de Registro
│   │       │   ├── TaskRequest.java        # DTO de Request de Tarefa
│   │       │   └── TaskResponse.java       # DTO de Response de Tarefa
│   │       ├── config/                      # Configurações
│   │       │   └── infra/
│   │       │       ├── handlers/           # Exception Handlers
│   │       │       │   └── GlobalExceptionHandler.java
│   │       │       ├── exceptions/         # Exceções Customizadas
│   │       │       │   ├── ResourceNotFoundException.java
│   │       │       │   └── UnauthorizedException.java
│   │       │       ├── security/           # Configurações de Segurança
│   │       │       │   ├── SecurityConfig.java
│   │       │       │   ├── JwtTokenProvider.java
│   │       │       │   ├── JwtAuthenticationFilter.java
│   │       │       │   └── UserDetailsServiceImpl.java
│   │       │       └── swagger/            # Configuração Swagger
│   │       │           └── OpenAPI30Configuration.java
│   │       └── StartTasklist.java          # Classe Principal
│   └── resources/
│       ├── application.yml                 # Configurações da Aplicação
│       └── banner.txt
└── test/
    ├── java/                                # Testes
    │   └── br/com/jtech/tasklist/
    │       ├── controller/                  # Testes de Integração
    │       │   ├── AuthControllerIntegrationTest.java
    │       │   └── TaskControllerIntegrationTest.java
    │       └── service/                     # Testes Unitários
    │           ├── AuthServiceTest.java
    │           └── TaskServiceTest.java
    └── resources/
        └── application-test.properties     # Configurações para Testes
```

### Descrição das Camadas

#### 1. **Controller (Presentation Layer)**
- **Responsabilidade**: Receber requisições HTTP, validar entrada, chamar Services
- **Componentes**: `AuthController`, `TaskController`
- **DTOs**: Objetos de transferência de dados (Request/Response)

#### 2. **Service (Business Layer)**
- **Responsabilidade**: Lógica de negócio, orquestração de operações
- **Componentes**: `AuthService`, `TaskService`
- **Características**: Transacional, valida regras de negócio

#### 3. **Repository (Data Access Layer)**
- **Responsabilidade**: Acesso a dados, abstração do banco de dados
- **Componentes**: `UserRepository`, `TaskRepository`
- **Tecnologia**: Spring Data JPA

#### 4. **Entity (Domain/Data Layer)**
- **Responsabilidade**: Representação das entidades do banco de dados
- **Componentes**: `UserEntity`, `TaskEntity`
- **Tecnologia**: JPA/Hibernate

#### 5. **Config (Infrastructure)**
- **Security**: Configurações de autenticação/autorização
- **Handlers**: Tratamento centralizado de exceções
- **Swagger**: Documentação da API

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

### 2. JWT vs Session-based Authentication

**Escolha**: JWT (JSON Web Tokens)

**Justificativa**:

- **Stateless**: Não requer armazenamento de sessão no servidor
- **Escalabilidade**: Facilita horizontal scaling
- **Mobile-friendly**: Adequado para APIs REST consumidas por mobile
- **Refresh Tokens**: Implementado para melhor segurança

### 3. BCrypt para Hash de Senhas

**Justificativa**:

- **Segurança**: Algoritmo de hash unidirecional seguro
- **Salt automático**: Gera salt único para cada senha
- **Lentidão intencional**: Protege contra ataques de força bruta
- **Padrão da indústria**: Amplamente utilizado e testado

### 4. Spring Data JPA vs JDBC Direto

**Escolha**: Spring Data JPA

**Justificativa**:

- **Produtividade**: Reduz código boilerplate
- **Abstração**: Facilita troca de banco de dados
- **Type-safe queries**: Métodos de query gerados automaticamente
- **Transações**: Gerenciamento automático de transações

### 5. Uso Direto de Entities

**Justificativa**:

- **Simplicidade**: Entities JPA são usadas diretamente, sem camada de domínio adicional
- **Produtividade**: Menos código, menos conversões
- **Spring Data JPA**: Framework já abstrai bem a persistência
- **Adequação**: Para este projeto, a complexidade de separar Domain/Entity não se justifica

### 6. Exception Handling Centralizado

**Justificativa**:

- **Consistência**: Respostas de erro padronizadas
- **Manutenibilidade**: Um único ponto para tratamento de exceções
- **Logging**: Facilita logging centralizado
- **UX**: Mensagens de erro claras para o cliente

### 7. Validação de Propriedade em Tasks

**Justificativa**:

- **Segurança**: Usuário só acessa suas próprias tarefas
- **Implementação**: Validação em todas as operações (GET, PUT, DELETE)
- **Query**: Uso de `findByIdAndUser_Id` para garantir propriedade
- **Prevenção de Ataques**: Protege contra acesso não autorizado a recursos de outros usuários

### 8. Inversão de Dependência via Spring Data JPA

**Justificativa**:

- **Spring Data JPA**: Repositories são interfaces, já implementam Dependency Inversion
- **Testabilidade**: Facilita criação de mocks nos testes (Mockito)
- **Simplicidade**: Não precisa de camada adicional de Ports/Adapters
- **SOLID**: Respeita o princípio de Dependency Inversion através das interfaces Repository

### 9. Separação entre Request/Response DTOs e Entities

**Justificativa**:

- **Segurança**: Evita exposição de dados internos das entidades (ex: senha)
- **Flexibilidade**: Permite evoluir API sem impactar estrutura de dados
- **Validação**: DTOs têm validações específicas para entrada (@Valid)
- **Versionamento**: Facilita versionamento da API
- **Performance**: Controle sobre quais campos são serializados

### 10. Testes com H2 em Memória

**Justificativa**:

- **Performance**: Testes mais rápidos que com banco real
- **Isolamento**: Cada teste roda em ambiente limpo
- **Portabilidade**: Não requer instalação de PostgreSQL para testes
- **CI/CD**: Facilita execução em pipelines de CI/CD

## Melhorias e Roadmap

### Curto Prazo

1. **Endpoint de Refresh Token**: Implementar renovação de tokens
2. **Paginação**: Adicionar paginação na listagem de tarefas
3. **Filtros**: Permitir filtrar tarefas por status, data, etc.
4. **Validação de Email**: Adicionar validação de formato de email mais robusta
5. **Logging Estruturado**: Implementar logs estruturados para auditoria

### Médio Prazo

1. **Cache**: Implementar cache (Redis) para melhorar performance de consultas frequentes
2. **Rate Limiting**: Proteção contra abuso da API usando Spring Cloud Gateway ou Bucket4j
3. **WebSockets**: Notificações em tempo real para atualizações de tarefas
4. **Upload de Arquivos**: Anexar arquivos às tarefas com armazenamento em S3 ou similar
5. **Categorias/Tags**: Organização de tarefas por categorias e sistema de tags
6. **Busca Full-Text**: Implementar busca avançada usando PostgreSQL Full-Text Search ou Elasticsearch
7. **Auditoria**: Log de todas as operações para rastreabilidade

### Longo Prazo

1. **Microserviços**: Dividir em serviços menores (Auth Service, Task Service) se necessário para escalabilidade
2. **Event Sourcing**: Para auditoria completa e reconstrução de estado
3. **CQRS**: Separação entre comandos (write) e queries (read) para otimização
4. **GraphQL**: Alternativa ao REST para clientes que precisam de queries flexíveis
5. **Internacionalização**: Suporte a múltiplos idiomas nas mensagens e validações
6. **Analytics**: Dashboard de métricas e relatórios com integração a ferramentas de BI
7. **Multi-tenancy**: Suporte a múltiplos tenants/organizações
8. **API Gateway**: Centralizar autenticação, rate limiting e roteamento

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

#### POST /api/v1/auth/login

Autentica um usuário e retorna tokens JWT.

**Request:**

```json
{
  "email": "joao@example.com",
  "password": "senha123"
}
```

**Response:**

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
  "completed": false
}
```

#### GET /api/v1/tasks

Lista todas as tarefas do usuário autenticado.

#### GET /api/v1/tasks/{id}

Busca uma tarefa específica por ID.

#### PUT /api/v1/tasks/{id}

Atualiza uma tarefa existente.

#### DELETE /api/v1/tasks/{id}

Remove uma tarefa.

## Contribuindo

1. Fork o projeto
2. Crie uma branch para sua feature (`git checkout -b feature/AmazingFeature`)
3. Commit suas mudanças (`git commit -m 'Add some AmazingFeature'`)
4. Push para a branch (`git push origin feature/AmazingFeature`)
5. Abra um Pull Request

## Licença

Este projeto é propriedade da J-Tech Soluções em Informática. Todos os direitos reservados.

## Contato

Para dúvidas ou sugestões, entre em contato com a equipe de desenvolvimento.
