package com.example.prioritization.api;

import com.example.prioritization.api.dto.PrioritizationResponse;
import com.example.prioritization.api.dto.TicketRequest;
import com.example.prioritization.service.TicketPrioritizationService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import java.util.List;

/**
 * Recurso REST para priorização de chamados.
 */
@Path("/tickets")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TicketPrioritizationResource {

    private final TicketPrioritizationService prioritizationService;

    public TicketPrioritizationResource(TicketPrioritizationService prioritizationService) {
        this.prioritizationService = prioritizationService;
    }

    /**
     * Prioriza a lista de chamados recebida.
     *
     * @param tickets lista de chamados a priorizar
     * @return chamados priorizados e sinalizados
     */
    @POST
    @Path("/prioritize")
    public PrioritizationResponse prioritize(
            @NotNull(message = "A lista de chamados é obrigatória")
            List<@Valid TicketRequest> tickets) {
        return prioritizationService.prioritize(tickets);
    }
}
