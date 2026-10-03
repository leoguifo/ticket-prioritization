package com.example.prioritization.api.dto;

/**
 * Chamado sinalizado por possuir prioridade desconhecida.
 *
 * @param id            identificador do chamado afetado
 * @param receivedValue valor de prioridade recebido ({@code null} quando a
 *                      prioridade for nula ou vazia)
 * @param message       mensagem descritiva da sinalização
 */
public record FlaggedTicket(String id, String receivedValue, String message) {
}
