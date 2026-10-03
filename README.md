# Ticket Prioritization

API REST que organiza chamados de acordo com a sua prioridade. A aplicação recebe uma lista de chamados, ordena os que possuem prioridade válida e sinaliza aqueles cuja prioridade é desconhecida.

## Tecnologias

- Java 21
- Quarkus 3.33.1
- Maven
- Jakarta REST (quarkus-rest) com serialização JSON via Jackson
- Jakarta Validation (Hibernate Validator)
- Lombok
- JUnit 5, Mockito, REST-assured e AssertJ (testes)

## Regras de negócio

- Prioridades válidas: `Alta`, `Média` e `Baixa`.
- Ordenação por prioridade: `Alta` → `Média` → `Baixa`.
- Entre chamados de mesma prioridade, a ordem de chegada é preservada (ordenação estável).
- A normalização da prioridade remove espaços nas extremidades (trim) e ignora diferenças entre maiúsculas e minúsculas. Também são aceitos os nomes do enum (`ALTA`, `MEDIA`, `BAIXA`).
- Prioridades desconhecidas, nulas ou vazias são sinalizadas e nunca convertidas para uma prioridade válida.
- A validação da prioridade ocorre antes da ordenação.
- Uma lista vazia (ou nula) resulta em uma resposta vazia, sem erro.

## Endpoint

### `POST /tickets/prioritize`

Recebe uma lista de chamados e retorna os chamados priorizados e os sinalizados.

- `Content-Type: application/json`
- `Accept: application/json`

#### Corpo da requisição

Lista de chamados. Cada chamado possui:

| Campo      | Tipo   | Obrigatório | Descrição |
| ---------- | ------ | ----------- | --------- |
| `id`       | string | Sim         | Identificador do chamado (não pode ser vazio). |
| `priority` | string | Não         | Valor textual da prioridade. Pode ser nulo, vazio ou desconhecido. |

```json
[
  { "id": "C1", "priority": "Média" },
  { "id": "C2", "priority": "Alta" },
  { "id": "C3", "priority": "Urgente" },
  { "id": "C4", "priority": "" }
]
```

#### Corpo da resposta

| Campo         | Descrição |
| ------------- | --------- |
| `prioritized` | Chamados válidos ordenados por prioridade, preservando a ordem de chegada entre iguais. |
| `flagged`     | Chamados sinalizados por prioridade desconhecida, na ordem de chegada. |

Cada item de `prioritized` contém `id` e `priority` (rótulo válido: `Alta`, `Média` ou `Baixa`).

Cada item de `flagged` contém `id`, `receivedValue` (valor recebido, ou `null` quando a prioridade era nula/vazia) e `message` (descrição da sinalização).

```json
{
  "prioritized": [
    { "id": "C2", "priority": "Alta" },
    { "id": "C1", "priority": "Média" }
  ],
  "flagged": [
    { "id": "C3", "receivedValue": "Urgente", "message": "prioridade desconhecida: Urgente" },
    { "id": "C4", "receivedValue": null, "message": "prioridade desconhecida" }
  ]
}
```

## Como executar

Modo de desenvolvimento (live reload):

```bash
mvn quarkus:dev
```

A aplicação sobe em `http://localhost:8080` (configurável via `quarkus.http.port` em `src/main/resources/application.properties`).

## Como testar

```bash
mvn test
```

## Build

```bash
mvn package
```

## Estrutura do projeto

```
src/main/java/com/example/prioritization
├── api
│   ├── TicketPrioritizationResource.java   # Endpoint REST
│   └── dto
│       ├── TicketRequest.java              # Chamado de entrada
│       ├── PrioritizationResponse.java     # Resposta (prioritized + flagged)
│       ├── PrioritizedTicket.java          # Chamado priorizado
│       └── FlaggedTicket.java              # Chamado sinalizado
├── domain
│   └── Priority.java                       # Enum de prioridades e normalização
└── service
    └── TicketPrioritizationService.java    # Regra de priorização
```

Os cenários de teste detalhados estão documentados em [`docs/requirements.md`](docs/requirements.md).
