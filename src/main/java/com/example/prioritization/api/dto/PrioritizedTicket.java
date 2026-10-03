package com.example.prioritization.api.dto;

/**
 * Chamado classificado com uma prioridade válida.
 *
 * @param id       identificador do chamado
 * @param priority rótulo da prioridade válida atribuída (Alta, Média ou Baixa)
 */
public record PrioritizedTicket(String id, String priority) {
}
