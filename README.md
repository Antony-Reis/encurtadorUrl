# Encurtador de URLs

Projeto de estudo desenvolvido com **Spring Boot** para praticar a construção de uma API REST de encurtamento de URLs, autenticação com JWT, persistência com PostgreSQL, cache com Redis e processamento assíncrono de métricas utilizando Apache Kafka.

> **Objetivo:** estudar, de forma integrada, conceitos de APIs REST, arquitetura em camadas, JPA/Hibernate, autenticação stateless, cache, mensageria, paginação e testes unitários.

## Tecnologias

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA / Hibernate
- Spring Security
- JWT com JJWT 0.12.6
- PostgreSQL 16
- Redis
- Apache Kafka
- Spring for Apache Kafka
- SpringDoc OpenAPI
- Maven
- JUnit 5 + Mockito
- Docker / Docker Compose

## Funcionalidades

### Encurtamento de URLs

- Criação de URLs encurtadas.
- Geração de identificador utilizando SHA-256 com salt baseado no instante de criação.
- Verificação de colisão antes de salvar o identificador.
- Limite de **10 URLs por usuário**.
- Expiração da URL após **10 dias**.
- Possibilidade de exclusão da URL pelo proprietário.

### Autenticação e usuários

- Cadastro de usuários.
- Senhas armazenadas utilizando BCrypt.
- Login com geração de token JWT.
- Autenticação baseada em `Bearer Token`.
- Aplicação configurada como **stateless**, sem sessão HTTP.
- Recuperação do usuário autenticado pelo `SecurityContextHolder`.

### Redis

O Redis é utilizado como cache para as URLs encurtadas.

Ao criar uma URL, o projeto armazena no Redis a URL original e o ID do registro. O cache possui TTL de **10 dias**.

Na consulta de uma URL encurtada, o sistema tenta primeiro o Redis. Caso o dado não esteja no cache, realiza a busca no PostgreSQL.

### Kafka e métricas

O acesso a uma URL encurtada gera um evento assíncrono.

Fluxo simplificado:

```text
Cliente
   |
   v
GET /v1/url/{urlEncurtada}
   |
   +----> Redis / PostgreSQL
   |
   +----> Evento de acesso
            |
            v
       Kafka: url-accessed
            |
            v
       UrlConsumer
            |
            v
      PostgreSQL (metrics)
```

Dessa forma, o registro das métricas não precisa ser executado diretamente no fluxo de redirecionamento.

### Métricas

- Listagem paginada das URLs do usuário.
- Consulta paginada dos acessos de uma URL.
- Contagem total de acessos de uma URL.
- Registro da data/hora de cada acesso.

## Arquitetura do projeto

O projeto está organizado principalmente por domínio funcional:

```text
src/main/java/com/antony/encurtador
├── config
│   ├── KafkaConsumerConfig.java
│   ├── KafkaProducerConfig.java
│   ├── KafkaTopicConfig.java
│   ├── RedisConfig.java
│   └── security
│       ├── AuthService.java
│       ├── SecurityConfig.java
│       ├── SecurityFilter.java
│       └── TokenService.java
│
├── exceptions
│   ├── ...
│   ├── RestExceptionHandler.java
│   └── RErrorResponseDto.java
│
├── metrics
│   ├── IMetricsRepository.java
│   ├── MetricsController.java
│   ├── MetricsEntity.java
│   ├── MetricsService.java
│   ├── UrlConsumer.java
│   └── utils
│
├── url
│   ├── IUrlRepository.java
│   ├── UrlController.java
│   ├── UrlEntity.java
│   ├── UrlProducer.java
│   ├── UrlService.java
│   └── utils
│
└── user
    ├── IUserRepository.java
    ├── UserController.java
    ├── UserEntity.java
    ├── UserService.java
    └── utils
```

## Banco e infraestrutura

O `docker-compose.yml` disponibiliza três serviços:

| Serviço | Porta | Função |
|---|---:|---|
| PostgreSQL | `5432` | Persistência dos usuários, URLs e métricas |
| Kafka | `9092` | Mensageria dos eventos de acesso |
| Redis | `6379` | Cache das URLs encurtadas |

A aplicação Spring Boot roda na porta **9090**.

## Variáveis de ambiente

O projeto utiliza um arquivo `.env` para as configurações locais.

Exemplo:

```env
POSTGRES_DB=encurtador
POSTGRES_USER=postgres
POSTGRES_PASSWORD=sua_senha
REDIS_PASSWORD=sua_senha_redis
SECURITY_KEY=uma_chave_secreta_com_pelo_menos_32_bytes
```

## Como executar

### 1. Pré-requisitos

Instale:

- JDK 21
- Docker
- Docker Compose
- Git (opcional)

### 2. Configurar o `.env`

Crie o arquivo `.env` na raiz do projeto com as variáveis necessárias:

```env
POSTGRES_DB=encurtador
POSTGRES_USER=postgres
POSTGRES_PASSWORD=postgres
REDIS_PASSWORD=redis
SECURITY_KEY=uma_chave_secreta_com_tamanho_suficiente_para_HS256
```

### 3. Subir os serviços

Na raiz do projeto:

```bash
docker compose up -d
```

Para conferir os containers:

```bash
docker compose ps
```

Para acompanhar os logs:

```bash
docker compose logs -f
```

### 4. Executar a aplicação

Com Maven instalado:

```bash
mvn spring-boot:run
```

No Windows, também pode ser utilizado o Maven Wrapper:

```powershell
.\mvnw.cmd spring-boot:run
```

Em Linux/macOS:

```bash
./mvnw spring-boot:run
```

A API ficará disponível em:

```text
http://localhost:9090
```

## Documentação da API

O projeto utiliza SpringDoc OpenAPI.

Após iniciar a aplicação, a interface do Swagger normalmente estará disponível em:

```text
http://localhost:9090/swagger-ui/index.html
```

A especificação OpenAPI pode ser consultada em:

```text
http://localhost:9090/v3/api-docs
```

## Endpoints principais

### Autenticação

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/v1/auth/register` | Cadastra um usuário |
| `POST` | `/v1/auth/login` | Realiza login e retorna o JWT |
| `PATCH` | `/v1/auth` | Atualiza a senha do usuário |

Exemplo de cadastro/login:

```json
{
  "email": "email@email.com",
  "password": "password"
}
```

O token retornado no login deve ser enviado nas requisições autenticadas:

```http
Authorization: Bearer SEU_TOKEN
```

### URLs

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/v1/url` | Cria uma URL encurtada |
| `GET` | `/v1/url/{urlEncurtada}` | Redireciona para a URL original |
| `DELETE` | `/v1/url/{urlEncurtada}` | Exclui uma URL do usuário autenticado |

Exemplo de criação:

```json
{
  "url": "https://www.exemplo.com"
}
```

O `GET` da URL encurtada responde com redirecionamento HTTP `302 FOUND`.

### Métricas

| Método | Endpoint | Descrição |
|---|---|---|
| `GET` | `/v1/metrics` | Lista as URLs do usuário de forma paginada |
| `GET` | `/v1/metrics/{urlEncurtada}` | Lista os acessos de uma URL |
| `GET` | `/v1/metrics/count/{urlEncurtada}` | Retorna a quantidade de acessos |

Os endpoints de métricas utilizam paginação através de `page` e `size`:

```text
/v1/metrics?page=0&size=10
```

## Fluxo de autenticação

O projeto utiliza JWT de forma stateless.

1. O usuário realiza login com e-mail e senha.
2. O `AuthenticationManager` valida as credenciais.
3. O `TokenService` gera um JWT contendo o e-mail como subject.
4. O cliente envia o token no header `Authorization` usando `Bearer`.
5. O `SecurityFilter` extrai o token, recupera o usuário pelo e-mail e valida o JWT.
6. O usuário autenticado é colocado no `SecurityContextHolder`.
7. Os serviços utilizam o usuário autenticado para validar propriedade das URLs e consultar métricas.

## Fluxo de criação e acesso de uma URL

### Criação

```text
POST /v1/url
        |
        v
SecurityContextHolder
        |
        v
Usuário autenticado
        |
        v
Gera hash da URL
        |
        v
Salva no PostgreSQL
        |
        v
Salva no Redis por 10 dias
```

### Acesso

```text
GET /v1/url/{codigo}
        |
        v
Consulta Redis
   |          |
   | encontrou| não encontrou
   v          v
retorna     PostgreSQL
URL original   |
   |            v
   +------> Evento Kafka
                 |
                 v
            url-accessed
                 |
                 v
            UrlConsumer
                 |
                 v
              metrics
```

## Testes

O projeto possui testes unitários utilizando **JUnit 5** e **Mockito**, com testes voltados principalmente para os serviços de autenticação e usuários.

Para executar os testes:

```bash
mvn test
```

Ou utilizando o Maven Wrapper:

```bash
./mvnw test
```

No Windows:

```powershell
.\mvnw.cmd test
```

## Observações de estudo

Este projeto foi construído com foco em aprendizado e experimentação das tecnologias utilizadas. Algumas decisões são propositalmente simples para facilitar o estudo e podem ser refinadas em uma aplicação de produção.

Um ponto importante é a configuração atual:

```yaml
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
```

Com essa configuração, o schema do banco é recriado a cada execução da aplicação, portanto **não é apropriado para produção** quando os dados precisam ser preservados.

Outro ponto de estudo é que o JWT utilizado é stateless: não existe uma sessão no servidor para invalidar automaticamente o token antes do vencimento. Em um sistema de produção, o fluxo de logout poderia ser complementado com estratégias como blacklist/revogação, tokens de curta duração e refresh tokens, dependendo do requisito do sistema.

## Melhorias futuras

Algumas evoluções possíveis para o projeto:

- Adicionar testes unitários completos para `UrlService` e `MetricsService`.
- Adicionar testes de integração para PostgreSQL, Redis e Kafka.
- Adicionar validação de formato das URLs recebidas.
- Criar endpoint específico de logout com estratégia de revogação de tokens.
- Utilizar migrations com Flyway ou Liquibase.
- Melhorar tratamento de erros e respostas da API.
- Adicionar observabilidade e logs estruturados.
- Configurar ambientes separados para desenvolvimento, teste e produção.
- Fixar versões das imagens Docker utilizadas no ambiente local.

## Autor
Antony Reis

---

Projeto desenvolvido para **estudo de desenvolvimento backend com Java e Spring Boot**.

**IA não foi utilizado neste projeto.**
