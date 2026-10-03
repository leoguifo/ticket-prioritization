package com.example.prioritization.api.dto;

import java.util.List;

/**
 * Resultado da priorização de uma lista de chamados.
 *
 * @param prioritized chamados válidos ordenados por prioridade
 *                    (Alta -> Média -> Baixa), preservando a ordem de chegada
 *                    entre chamados de mesma prioridade
 * @param flagged     chamados sinalizados por prioridade desconhecida,
 *                    na ordem de chegada
 */
public record PrioritizationResponse(
        List<PrioritizedTicket> prioritized,
        List<FlaggedTicket> flagged) {
}
