package com.example.prioritization.service;

import com.example.prioritization.api.dto.FlaggedTicket;
import com.example.prioritization.api.dto.PrioritizationResponse;
import com.example.prioritization.api.dto.PrioritizedTicket;
import com.example.prioritization.api.dto.TicketRequest;
import com.example.prioritization.domain.Priority;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Serviço responsável por organizar chamados de acordo com sua prioridade.
 *
 * <p>Regras aplicadas:</p>
 * <ul>
 *     <li>Prioridades válidas: Alta, Média e Baixa.</li>
 *     <li>Ordenação: Alta -&gt; Média -&gt; Baixa.</li>
 *     <li>Entre chamados de mesma prioridade, preserva-se a ordem de chegada.</li>
 *     <li>Prioridades desconhecidas (incluindo nula e vazia) são sinalizadas e
 *     nunca convertidas para uma prioridade válida.</li>
 *     <li>A validação da prioridade ocorre antes da ordenação.</li>
 * </ul>
 */
@ApplicationScoped
public class TicketPrioritizationService {

    private static final String UNKNOWN_PRIORITY_MESSAGE = "prioridade desconhecida";

    /**
     * Prioriza a lista de chamados recebida.
     *
     * @param tickets lista de chamados; se {@code null}, é tratada como vazia
     * @return resultado contendo os chamados priorizados e os sinalizados
     */
    public PrioritizationResponse prioritize(List<TicketRequest> tickets) {
        List<PrioritizedTicket> prioritized = new ArrayList<>();
        List<FlaggedTicket> flagged = new ArrayList<>();

        if (tickets == null || tickets.isEmpty()) {
            return new PrioritizationResponse(prioritized, flagged);
        }

        // Etapa 1 — Validação: ocorre antes da ordenação.
        List<ClassifiedTicket> classified = new ArrayList<>();
        for (TicketRequest ticket : tickets) {
            Optional<Priority> priority = Priority.fromRawValue(ticket.priority());
            if (priority.isPresent()) {
                classified.add(new ClassifiedTicket(ticket.id(), priority.get()));
            } else {
                flagged.add(buildFlaggedTicket(ticket));
            }
        }

        // Etapa 2 — Ordenação estável por prioridade (preserva ordem de chegada
        // entre chamados de mesma prioridade).
        classified.sort(Comparator.comparingInt(c -> c.priority().ordinal()));

        for (ClassifiedTicket ticket : classified) {
            prioritized.add(new PrioritizedTicket(ticket.id(), ticket.priority().getLabel()));
        }

        return new PrioritizationResponse(prioritized, flagged);
    }

    private FlaggedTicket buildFlaggedTicket(TicketRequest ticket) {
        String rawValue = ticket.priority();
        boolean hasValue = rawValue != null && !rawValue.trim().isEmpty();

        if (hasValue) {
            String receivedValue = rawValue.trim();
            return new FlaggedTicket(
                    ticket.id(),
                    receivedValue,
                    UNKNOWN_PRIORITY_MESSAGE + ": " + receivedValue);
        }

        return new FlaggedTicket(ticket.id(), null, UNKNOWN_PRIORITY_MESSAGE);
    }

    /** Associação interna entre o id do chamado e sua prioridade validada. */
    private record ClassifiedTicket(String id, Priority priority) {
    }
}
