# Cenários de Teste — Priorização de Chamados

## Descrição

Implementação responsável por organizar chamados de acordo com sua prioridade.

### Regras de negócio

- Prioridades válidas: `Alta`, `Média` e `Baixa`.
- A ordenação deve seguir: `Alta` → `Média` → `Baixa`.
- Entre chamados de mesma prioridade, deve ser preservada a ordem de chegada.
- Prioridades desconhecidas devem ser sinalizadas.
- Prioridades desconhecidas não devem ser silenciosamente convertidas para uma prioridade válida.

---

## Cenários de Teste

### CT01 — Lista com todas as prioridades

**Entrada:**

`[C1-Alta, C2-Média, C3-Baixa]`

**Saída esperada:**

`[C1, C2, C3]`

**Regras esperadas:**

- Ordenar por prioridade: Alta → Média → Baixa.

---

### CT02 — Chamados fora de ordem de prioridade

**Entrada:**

`[C1-Baixa, C2-Alta, C3-Média]`

**Saída esperada:**

`[C2, C3, C1]`

**Regras esperadas:**

- A prioridade deve prevalecer sobre a ordem de chegada.

---

### CT03 — Vários chamados de prioridade Alta

**Entrada:**

`[C1-Alta, C2-Alta, C3-Alta]`

**Saída esperada:**

`[C1, C2, C3]`

**Regras esperadas:**

- Preservar a ordem de chegada entre chamados de mesma prioridade.

---

### CT04 — Vários chamados de prioridade Média

**Entrada:**

`[C1-Média, C2-Média, C3-Média]`

**Saída esperada:**

`[C1, C2, C3]`

**Regras esperadas:**

- Preservar a ordem de chegada entre chamados de mesma prioridade.

---

### CT05 — Vários chamados de prioridade Baixa

**Entrada:**

`[C1-Baixa, C2-Baixa, C3-Baixa]`

**Saída esperada:**

`[C1, C2, C3]`

**Regras esperadas:**

- Preservar a ordem de chegada entre chamados de mesma prioridade.

---

### CT06 — Mistura de prioridades com empates

**Entrada:**

`[C1-Média, C2-Alta, C3-Média, C4-Baixa, C5-Alta]`

**Saída esperada:**

`[C2, C5, C1, C3, C4]`

**Regras esperadas:**

- Ordenar por prioridade.
- Preservar a ordem original dentro de cada grupo de prioridade.
- `C2` deve permanecer antes de `C5`, pois ambos possuem prioridade Alta.
- `C1` deve permanecer antes de `C3`, pois ambos possuem prioridade Média.

---

### CT07 — Prioridade desconhecida

**Entrada:**

`[C1-Alta, C2-Urgente, C3-Baixa]`

**Saída esperada:**

Chamados priorizados:

`[C1, C3]`

Chamado sinalizado:

`C2 - prioridade desconhecida: Urgente`

**Regras esperadas:**

- `Urgente` não pertence às prioridades válidas.
- O chamado deve ser sinalizado.
- Não deve ser assumido que `Urgente` equivale a `Alta`.

---

### CT08 — Todas as prioridades desconhecidas

**Entrada:**

`[C1-Urgente, C2-Crítica]`

**Saída esperada:**

- `C1` sinalizado como prioridade desconhecida.
- `C2` sinalizado como prioridade desconhecida.
- Nenhum chamado deve ser classificado como Alta, Média ou Baixa.

---

### CT09 — Prioridade vazia

**Entrada:**

`[C1-Alta, C2-"", C3-Baixa]`

**Saída esperada:**

Chamados priorizados:

`[C1, C3]`

Chamado sinalizado:

`C2 - prioridade desconhecida`

**Regras esperadas:**

- Prioridade vazia deve ser considerada desconhecida.
- O chamado deve ser sinalizado.

---

### CT10 — Prioridade nula

**Entrada:**

`[C1-Alta, C2-null, C3-Média]`

**Saída esperada:**

Chamados priorizados:

`[C1, C3]`

Chamado sinalizado:

`C2 - prioridade desconhecida`

**Regras esperadas:**

- `null` não é uma prioridade válida.
- O chamado deve ser sinalizado.

---

### CT11 — Lista vazia

**Entrada:**

`[]`

**Saída esperada:**

`[]`

**Regras esperadas:**

- Não deve ocorrer erro.
- O resultado deve permanecer vazio.

---

### CT12 — Apenas um chamado

**Entrada:**

`[C1-Média]`

**Saída esperada:**

`[C1]`

**Regras esperadas:**

- Um único chamado deve ser retornado sem alteração.

---

### CT13 — Apenas Alta e Baixa

**Entrada:**

`[C1-Baixa, C2-Alta, C3-Baixa]`

**Saída esperada:**

`[C2, C1, C3]`

**Regras esperadas:**

- Alta deve aparecer antes de Baixa.
- A ordem entre os chamados Baixa deve ser preservada.
- `C1` deve permanecer antes de `C3`.

---

### CT14 — Apenas Média e Baixa

**Entrada:**

`[C1-Baixa, C2-Média, C3-Baixa]`

**Saída esperada:**

`[C2, C1, C3]`

**Regras esperadas:**

- Média deve aparecer antes de Baixa.
- A ordem entre os chamados Baixa deve ser preservada.

---

### CT15 — Diferença de capitalização

**Entrada:**

`[C1-alta, C2-Média, C3-BAIXA]`

**Saída esperada:**

Depende da regra de normalização definida pela implementação.

**Regras esperadas:**

Caso a implementação aceite diferenças de maiúsculas e minúsculas:

- `alta` deve ser interpretado como `Alta`.
- `Média` deve ser interpretado como `Média`.
- `BAIXA` deve ser interpretado como `Baixa`.

Caso não exista normalização:

- As prioridades diferentes do formato esperado devem ser sinalizadas como desconhecidas.

---

### CT16 — Prioridade com espaços

**Entrada:**

`[C1-" Alta ", C2-Média]`

**Saída esperada:**

Depende da regra de normalização definida pela implementação.

**Regras esperadas:**

Caso exista normalização:

- Remover espaços no início e no final.
- Interpretar `" Alta "` como `Alta`.

Caso não exista normalização:

- Sinalizar `C1` como prioridade desconhecida.

---

## Cenários Detalhados

### CT17 — Preservação da ordem entre prioridades iguais

**Entrada:**

```
C1 - Baixa
C2 - Alta
C3 - Média
C4 - Alta
C5 - Baixa
C6 - Média
```

**Saída esperada:**

```
C2 - Alta
C4 - Alta
C3 - Média
C6 - Média
C1 - Baixa
C5 - Baixa
```

**Regras esperadas:**

- Todos os chamados Alta devem aparecer antes dos chamados Média e Baixa.
- Todos os chamados Média devem aparecer antes dos chamados Baixa.
- `C2` deve permanecer antes de `C4`.
- `C3` deve permanecer antes de `C6`.
- `C1` deve permanecer antes de `C5`.

---

### CT18 — Prioridades desconhecidas misturadas com válidas

**Entrada:**

```
C1 - Baixa
C2 - Urgente
C3 - Alta
C4 - Média
C5 - Crítica
C6 - Alta
```

**Saída esperada:**

Chamados priorizados:

```
C3 - Alta
C6 - Alta
C4 - Média
C1 - Baixa
```

Chamados sinalizados:

```
C2 - prioridade desconhecida: "Urgente"
C5 - prioridade desconhecida: "Crítica"
```

**Regras esperadas:**

- Alta possui maior prioridade.
- Média possui prioridade intermediária.
- Baixa possui menor prioridade.
- `Urgente` não deve ser automaticamente interpretado como Alta.
- `Crítica` não deve ser automaticamente interpretado como Alta.
- Toda prioridade desconhecida deve ser sinalizada.

---

### CT19 — Entrada já ordenada

**Entrada:**

```
C1 - Alta
C2 - Alta
C3 - Média
C4 - Média
C5 - Baixa
```

**Saída esperada:**

```
C1 - Alta
C2 - Alta
C3 - Média
C4 - Média
C5 - Baixa
```

**Regras esperadas:**

- Uma entrada que já esteja corretamente ordenada deve permanecer inalterada.
- A ordem dos chamados com a mesma prioridade deve ser preservada.

---

### CT20 — Entrada totalmente invertida

**Entrada:**

```
C1 - Baixa
C2 - Baixa
C3 - Média
C4 - Média
C5 - Alta
C6 - Alta
```

**Saída esperada:**

```
C5 - Alta
C6 - Alta
C3 - Média
C4 - Média
C1 - Baixa
C2 - Baixa
```

**Regras esperadas:**

- Os grupos de prioridade devem ser reorganizados para Alta → Média → Baixa.
- A ordem dos chamados dentro de cada grupo deve permanecer a mesma.
- `C5` deve permanecer antes de `C6`.
- `C3` deve permanecer antes de `C4`.
- `C1` deve permanecer antes de `C2`.

---

## Critérios de Aceite

- [ ] Alta sempre precede Média e Baixa.
- [ ] Média sempre precede Baixa.
- [ ] Chamados com a mesma prioridade mantêm sua ordem de chegada.
- [ ] Prioridades desconhecidas são sinalizadas.
- [ ] Prioridades desconhecidas não são silenciosamente convertidas para uma prioridade válida.
- [ ] Entrada vazia é processada sem erro.
- [ ] Um único chamado permanece inalterado.
- [ ] A sinalização de prioridade desconhecida identifica o chamado afetado.
- [ ] A sinalização de prioridade desconhecida identifica o valor recebido.
- [ ] O comportamento para letras maiúsculas/minúsculas está definido.
- [ ] O comportamento para espaços em branco está definido.
- [ ] A validação da prioridade ocorre antes da ordenação.

---

## Resumo das Regras de Negócio

| Regra | Comportamento |
| --- | --- |
| Prioridade Alta | Primeiro grupo a ser processado |
| Prioridade Média | Segundo grupo a ser processado |
| Prioridade Baixa | Terceiro grupo a ser processado |
| Mesma prioridade | Preservar ordem de chegada |
| Prioridade desconhecida | Sinalizar |
| Prioridade vazia | Sinalizar |
| Prioridade null | Sinalizar |
| Lista vazia | Retornar lista vazia |
| Chamado único | Retornar sem alteração |
| Maiúsculas/minúsculas | Definir se haverá normalização |
| Espaços em branco | Definir se haverá normalização |
| Validação | Deve ocorrer antes da ordenação |



