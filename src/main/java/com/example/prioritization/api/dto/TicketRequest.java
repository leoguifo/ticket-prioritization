package com.example.prioritization.api.dto;

import jakarta.validation.constraints.NotBlank;

/**
 * Representa um chamado recebido para priorização.
 *
 * @param id       identificador do chamado (obrigatório)
 * @param priority valor textual bruto da prioridade; pode ser nulo, vazio ou
 *                 desconhecido, sendo tratado pela regra de negócio
 */
public record TicketRequest(

        @NotBlank(message = "O id do chamado é obrigatório")
        String id,

        String priority) {
}
